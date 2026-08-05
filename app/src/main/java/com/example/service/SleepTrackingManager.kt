package com.example.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.audio.AudioSynthesizer
import com.example.data.AppDatabase
import com.example.data.AppSettings
import com.example.data.SettingsRepository
import com.example.data.SleepSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TrackingState {
    IDLE,
    CHECK_IN_COUNTDOWN,
    AWAITING_REACTION,
    SLEEP_DETECTED,
    ALARM_RINGING,
    COMPLETED
}

class SleepTrackingManager(private val context: Context) {

    private val audioSynth = AudioSynthesizer()
    private val settingsRepo = SettingsRepository(context)
    private val db = AppDatabase.getDatabase(context)

    private val _trackingState = MutableStateFlow(TrackingState.IDLE)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()

    private val _timeToNextCheckInSecs = MutableStateFlow(0)
    val timeToNextCheckInSecs: StateFlow<Int> = _timeToNextCheckInSecs.asStateFlow()

    private val _reactionTimeRemainingSecs = MutableStateFlow(0)
    val reactionTimeRemainingSecs: StateFlow<Int> = _reactionTimeRemainingSecs.asStateFlow()

    private val _timeToAlarmSecs = MutableStateFlow(0)
    val timeToAlarmSecs: StateFlow<Int> = _timeToAlarmSecs.asStateFlow()

    private val _reactionsCount = MutableStateFlow(0)
    val reactionsCount: StateFlow<Int> = _reactionsCount.asStateFlow()

    private val _detectedSleepTimeMs = MutableStateFlow<Long?>(null)
    val detectedSleepTimeMs: StateFlow<Long?> = _detectedSleepTimeMs.asStateFlow()

    private val _alarmScheduledTimeMs = MutableStateFlow<Long?>(null)
    val alarmScheduledTimeMs: StateFlow<Long?> = _alarmScheduledTimeMs.asStateFlow()

    private val _currentSessionStartMs = MutableStateFlow<Long?>(null)
    val currentSessionStartMs: StateFlow<Long?> = _currentSessionStartMs.asStateFlow()

    private var activeJob: Job? = null
    private var currentDbSessionId: Long? = null

    /**
     * Starts the Tahajjud sleep detection session.
     */
    fun startSleepSession() {
        stopSession()

        val settings = settingsRepo.getSettings()
        val nowMs = System.currentTimeMillis()

        _currentSessionStartMs.value = nowMs
        _reactionsCount.value = 0
        _detectedSleepTimeMs.value = null
        _alarmScheduledTimeMs.value = null

        // Create DB record
        CoroutineScope(Dispatchers.IO).launch {
            val session = SleepSession(
                startTimeMs = nowMs,
                detectedSleepTimeMs = null,
                alarmScheduledTimeMs = null,
                reactionsCount = 0,
                checkInIntervalMins = settings.checkInIntervalMins,
                sleepAlarmMins = settings.sleepAlarmMins,
                status = "CHECKING"
            )
            currentDbSessionId = db.sleepSessionDao().insertSession(session)
        }

        startCheckInCountdown(settings)
    }

    private fun startCheckInCountdown(settings: AppSettings) {
        _trackingState.value = TrackingState.CHECK_IN_COUNTDOWN
        val totalSecs = settings.checkInIntervalMins * 60
        _timeToNextCheckInSecs.value = totalSecs

        activeJob?.cancel()
        activeJob = CoroutineScope(Dispatchers.Default).launch {
            var current = totalSecs
            while (current > 0 && _trackingState.value == TrackingState.CHECK_IN_COUNTDOWN) {
                delay(1000)
                current--
                _timeToNextCheckInSecs.value = current
            }

            if (_trackingState.value == TrackingState.CHECK_IN_COUNTDOWN && current <= 0) {
                // Check-in interval elapsed! Trigger sweet check-in sound & await reaction
                triggerCheckInTing(settings)
            }
        }
    }

    private fun triggerCheckInTing(settings: AppSettings) {
        _trackingState.value = TrackingState.AWAITING_REACTION
        _reactionTimeRemainingSecs.value = settings.reactionWindowSecs

        // Play gentle check-in sound
        audioSynth.playCheckInSound(settings.checkInSoundStyle, settings.volume)

        if (settings.vibrateOnCheckIn) {
            vibratePulse()
        }

        activeJob?.cancel()
        activeJob = CoroutineScope(Dispatchers.Default).launch {
            var remaining = settings.reactionWindowSecs
            while (remaining > 0 && _trackingState.value == TrackingState.AWAITING_REACTION) {
                delay(1000)
                remaining--
                _reactionTimeRemainingSecs.value = remaining
            }

            if (_trackingState.value == TrackingState.AWAITING_REACTION && remaining <= 0) {
                // User DID NOT react within response window -> Sleep Detected!
                onSleepDetected(settings)
            }
        }
    }

    /**
     * Called when user presses Volume button, Lock key, or Taps screen during check-in or countdown.
     */
    fun onUserReacted() {
        val currentState = _trackingState.value
        if (currentState == TrackingState.AWAITING_REACTION || currentState == TrackingState.CHECK_IN_COUNTDOWN) {
            _reactionsCount.value += 1
            vibrateShortConfirm()

            // Soft confirmation audio
            audioSynth.playCheckInSound("Crystal Ting", 0.3f)

            // Reset check-in countdown interval (Postpone sleep detection)
            val settings = settingsRepo.getSettings()
            startCheckInCountdown(settings)
        }
    }

    /**
     * Sleep detected! Calculate Tahajjud alarm time and run alarm timer.
     */
    private fun onSleepDetected(settings: AppSettings) {
        _trackingState.value = TrackingState.SLEEP_DETECTED
        val sleepTime = System.currentTimeMillis()
        val alarmDurationMs = settings.sleepAlarmMins * 60 * 1000L
        val alarmTime = sleepTime + alarmDurationMs

        _detectedSleepTimeMs.value = sleepTime
        _alarmScheduledTimeMs.value = alarmTime

        val totalAlarmSecs = (settings.sleepAlarmMins * 60)
        _timeToAlarmSecs.value = totalAlarmSecs

        // Update Room session
        currentDbSessionId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                val existing = db.sleepSessionDao().getLatestSession()
                if (existing != null && existing.id == id) {
                    db.sleepSessionDao().updateSession(
                        existing.copy(
                            detectedSleepTimeMs = sleepTime,
                            alarmScheduledTimeMs = alarmTime,
                            reactionsCount = _reactionsCount.value,
                            status = "SLEEP_DETECTED"
                        )
                    )
                }
            }
        }

        // Start Tahajjud Alarm Countdown
        activeJob?.cancel()
        activeJob = CoroutineScope(Dispatchers.Default).launch {
            var currentSecs = totalAlarmSecs
            while (currentSecs > 0 && _trackingState.value == TrackingState.SLEEP_DETECTED) {
                delay(1000)
                currentSecs--
                _timeToAlarmSecs.value = currentSecs
            }

            if (_trackingState.value == TrackingState.SLEEP_DETECTED && currentSecs <= 0) {
                // Time for Tahajjud! Trigger Ringtone Alarm
                triggerTahajjudAlarm(settings)
            }
        }
    }

    private fun triggerTahajjudAlarm(settings: AppSettings) {
        _trackingState.value = TrackingState.ALARM_RINGING

        // Start ringing Tahajjud Alarm sound (using custom sound if selected)
        audioSynth.startAlarmRingtone(context, settings.alarmRingtoneStyle, settings.customRingtoneUri, settings.volume)

        // Continuous vibration
        vibrateAlarmLoop()

        // Update Room session
        currentDbSessionId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                val existing = db.sleepSessionDao().getLatestSession()
                if (existing != null && existing.id == id) {
                    db.sleepSessionDao().updateSession(
                        existing.copy(status = "ALARM_RINGING")
                    )
                }
            }
        }
    }

    /**
     * Dismiss alarm / wake up for Tahajjud.
     */
    fun dismissAlarm() {
        audioSynth.stopAlarmRingtone()
        val wakeUpMs = System.currentTimeMillis()
        _trackingState.value = TrackingState.COMPLETED

        currentDbSessionId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                val existing = db.sleepSessionDao().getLatestSession()
                if (existing != null && existing.id == id) {
                    db.sleepSessionDao().updateSession(
                        existing.copy(
                            actualWakeUpTimeMs = wakeUpMs,
                            status = "WOKE_UP"
                        )
                    )
                }
            }
        }
        activeJob?.cancel()
    }

    /**
     * Stop tracking session completely.
     */
    fun stopSession() {
        activeJob?.cancel()
        activeJob = null
        audioSynth.stopAlarmRingtone()

        if (_trackingState.value != TrackingState.COMPLETED && _trackingState.value != TrackingState.IDLE) {
            currentDbSessionId?.let { id ->
                CoroutineScope(Dispatchers.IO).launch {
                    val existing = db.sleepSessionDao().getLatestSession()
                    if (existing != null && existing.id == id) {
                        db.sleepSessionDao().updateSession(
                            existing.copy(status = "CANCELLED")
                        )
                    }
                }
            }
        }

        _trackingState.value = TrackingState.IDLE
        _timeToNextCheckInSecs.value = 0
        _reactionTimeRemainingSecs.value = 0
        _timeToAlarmSecs.value = 0
    }

    private fun vibratePulse() {
        try {
            val vibrator = getVibrator()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(300)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateShortConfirm() {
        try {
            val vibrator = getVibrator()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateAlarmLoop() {
        try {
            val vibrator = getVibrator()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 500, 500, 500, 1000), 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 500, 500), 0)
            }
        } catch (_: Exception) {}
    }

    private fun getVibrator(): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
