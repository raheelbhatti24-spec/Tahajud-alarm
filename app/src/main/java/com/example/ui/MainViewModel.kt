package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioSynthesizer
import com.example.data.AppDatabase
import com.example.data.AppSettings
import com.example.data.SettingsRepository
import com.example.data.SleepSession
import com.example.service.SleepTrackingManager
import com.example.service.TrackingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val trackingManager = SleepTrackingManager(application)
    private val settingsRepo = SettingsRepository(application)
    private val db = AppDatabase.getDatabase(application)
    private val audioSynth = AudioSynthesizer()

    private val _settings = MutableStateFlow(settingsRepo.getSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    val trackingState: StateFlow<TrackingState> = trackingManager.trackingState
    val timeToNextCheckInSecs: StateFlow<Int> = trackingManager.timeToNextCheckInSecs
    val reactionTimeRemainingSecs: StateFlow<Int> = trackingManager.reactionTimeRemainingSecs
    val timeToAlarmSecs: StateFlow<Int> = trackingManager.timeToAlarmSecs
    val reactionsCount: StateFlow<Int> = trackingManager.reactionsCount
    val detectedSleepTimeMs: StateFlow<Long?> = trackingManager.detectedSleepTimeMs
    val alarmScheduledTimeMs: StateFlow<Long?> = trackingManager.alarmScheduledTimeMs

    val allSessions = db.sleepSessionDao().getAllSessions()

    private val _isBlackoutModeActive = MutableStateFlow(false)
    val isBlackoutModeActive: StateFlow<Boolean> = _isBlackoutModeActive.asStateFlow()

    private val _testPlaying = MutableStateFlow(false)
    val testPlaying: StateFlow<Boolean> = _testPlaying.asStateFlow()

    fun startSession(enterBlackout: Boolean = true) {
        trackingManager.startSleepSession()
        if (enterBlackout && _settings.value.blackoutModeEnabled) {
            _isBlackoutModeActive.value = true
        }
    }

    fun stopSession() {
        trackingManager.stopSession()
        _isBlackoutModeActive.value = false
    }

    fun onUserReacted() {
        trackingManager.onUserReacted()
    }

    fun dismissAlarm() {
        trackingManager.dismissAlarm()
        _isBlackoutModeActive.value = false
    }

    fun setBlackoutActive(active: Boolean) {
        _isBlackoutModeActive.value = active
    }

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
        viewModelScope.launch {
            settingsRepo.saveSettings(newSettings)
        }
    }

    fun testCheckInSound() {
        audioSynth.playCheckInSound(_settings.value.checkInSoundStyle, _settings.value.volume)
    }

    fun testAlarmRingtone() {
        if (_testPlaying.value) {
            audioSynth.stopAlarmRingtone()
            _testPlaying.value = false
        } else {
            _testPlaying.value = true
            audioSynth.startAlarmRingtone(
                context = getApplication(),
                style = _settings.value.alarmRingtoneStyle,
                customUriStr = _settings.value.customRingtoneUri,
                volume = _settings.value.volume
            )
        }
    }

    fun stopTestAudio() {
        audioSynth.stopAlarmRingtone()
        _testPlaying.value = false
    }

    fun deleteSession(session: SleepSession) {
        viewModelScope.launch {
            db.sleepSessionDao().deleteSessionById(session.id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            db.sleepSessionDao().clearAll()
        }
    }
}
