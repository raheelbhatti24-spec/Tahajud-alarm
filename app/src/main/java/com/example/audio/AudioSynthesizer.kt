package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.net.Uri
import android.util.Log
import com.example.data.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

class AudioSynthesizer {

    private val sampleRate = 44100
    private var alarmJob: Job? = null
    private var currentAlarmTrack: AudioTrack? = null
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Plays a sweet, gentle check-in sound based on style.
     */
    fun playCheckInSound(style: String, volume: Float = 0.8f) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                when (style) {
                    "Brass Bell" -> playBrassBell(volume)
                    "Rain Drop" -> playRainDrop(volume)
                    "Soft Harp" -> playSoftHarpArpeggio(volume)
                    else -> playCrystalTing(volume) // Default "Crystal Ting"
                }
            } catch (e: Exception) {
                Log.e("AudioSynthesizer", "Error playing check-in sound", e)
                fallbackTone()
            }
        }
    }

    /**
     * Sweet Crystal Ting: 1760Hz (A6) with 3520Hz overtone and exponential decay.
     */
    private fun playCrystalTing(volume: Float) {
        val durationSec = 1.2
        val numSamples = (durationSec * sampleRate).toInt()
        val buffer = ShortArray(numSamples)

        val freq1 = 1760.0 // A6
        val freq2 = 3520.0 // A7 overtone

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-4.5 * t) // Fast attack, smooth decay
            val sample = (sin(2.0 * Math.PI * freq1 * t) * 0.7 + sin(2.0 * Math.PI * freq2 * t) * 0.3) * envelope
            buffer[i] = (sample * Short.MAX_VALUE * volume).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playBuffer(buffer)
    }

    /**
     * Soft Brass Bell: 880Hz + 1320Hz warm resonance.
     */
    private fun playBrassBell(volume: Float) {
        val durationSec = 1.8
        val numSamples = (durationSec * sampleRate).toInt()
        val buffer = ShortArray(numSamples)

        val freq1 = 880.0
        val freq2 = 1320.0
        val freq3 = 1760.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-2.8 * t)
            val sample = (sin(2.0 * Math.PI * freq1 * t) * 0.5 +
                          sin(2.0 * Math.PI * freq2 * t) * 0.3 +
                          sin(2.0 * Math.PI * freq3 * t) * 0.2) * envelope
            buffer[i] = (sample * Short.MAX_VALUE * volume).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playBuffer(buffer)
    }

    /**
     * Gentle Rain Drop: Frequency glide 1400Hz to 900Hz.
     */
    private fun playRainDrop(volume: Float) {
        val durationSec = 0.8
        val numSamples = (durationSec * sampleRate).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val currentFreq = 1400.0 - (500.0 * (t / durationSec))
            val envelope = exp(-6.0 * t)
            val sample = sin(2.0 * Math.PI * currentFreq * t) * envelope
            buffer[i] = (sample * Short.MAX_VALUE * volume).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playBuffer(buffer)
    }

    /**
     * Soft Harp Arpeggio: A major chord sequence (A4, C#5, E5, A5).
     */
    private fun playSoftHarpArpeggio(volume: Float) {
        val notes = doubleArrayOf(440.0, 554.37, 659.25, 880.0) // A4, C#5, E5, A5
        val noteDuration = 0.25
        val totalDuration = noteDuration * notes.size + 1.0
        val numSamples = (totalDuration * sampleRate).toInt()
        val buffer = ShortArray(numSamples)

        for (nIndex in notes.indices) {
            val noteStartSample = (nIndex * noteDuration * sampleRate).toInt()
            val freq = notes[nIndex]
            val noteLength = (1.2 * sampleRate).toInt()

            for (i in 0 until noteLength) {
                val targetIndex = noteStartSample + i
                if (targetIndex >= numSamples) break

                val t = i.toDouble() / sampleRate
                val envelope = exp(-3.2 * t)
                val sample = sin(2.0 * Math.PI * freq * t) * envelope * 0.4

                val current = buffer[targetIndex].toInt()
                val newValue = (current + sample * Short.MAX_VALUE * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[targetIndex] = newValue.toShort()
            }
        }

        playBuffer(buffer)
    }

    /**
     * Starts continuous looping Tahajjud Alarm Ringtone until explicitly stopped.
     * Supports playing custom audio file URI picked from phone storage!
     */
    fun startAlarmRingtone(context: Context, style: String, customUriStr: String? = null, volume: Float = 0.9f) {
        stopAlarmRingtone()

        if (style == AppSettings.RINGTONE_CUSTOM && !customUriStr.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(customUriStr)
                val mp = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(context, uri)
                    setVolume(volume, volume)
                    isLooping = true
                    prepare()
                    start()
                }
                mediaPlayer = mp
                return
            } catch (e: Exception) {
                Log.e("AudioSynthesizer", "Error playing custom audio URI, falling back to synth tone", e)
            }
        }

        alarmJob = CoroutineScope(Dispatchers.Default).launch {
            while (alarmJob?.isActive == true) {
                when (style) {
                    "Dawn Sunrise Chime" -> playDawnChimePhrase(volume)
                    "Deep Singing Bowl" -> playSingingBowlPhrase(volume)
                    "Rising Solfeggio" -> playSolfeggioPhrase(volume)
                    "Classic Gentle Bell" -> playClassicBellPhrase(volume)
                    else -> playPeacefulAdhanPhrase(volume) // Default Adhan Tone
                }
                delay(1200) // Pause between melodic cycles
            }
        }
    }

    fun stopAlarmRingtone() {
        alarmJob?.cancel()
        alarmJob = null

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null

        try {
            currentAlarmTrack?.stop()
            currentAlarmTrack?.release()
        } catch (_: Exception) {}
        currentAlarmTrack = null
    }

    /**
     * Melodic phrase inspired by peaceful spiritual Adhan notes in Bayati scale.
     */
    private suspend fun playPeacefulAdhanPhrase(volume: Float) {
        // Scale notes approx: D4 (293.66), F4 (349.23), G4 (392.00), A4 (440.00), Bb4 (466.16), C5 (523.25)
        val melody = listOf(
            293.66 to 0.6,
            349.23 to 0.6,
            392.00 to 0.8,
            440.00 to 1.0,
            466.16 to 0.8,
            440.00 to 1.2
        )
        playMelodyPhrase(melody, volume)
    }

    private suspend fun playDawnChimePhrase(volume: Float) {
        val melody = listOf(
            523.25 to 0.4, // C5
            659.25 to 0.4, // E5
            783.99 to 0.5, // G5
            1046.50 to 0.8, // C6
            880.00 to 0.6  // A5
        )
        playMelodyPhrase(melody, volume)
    }

    private suspend fun playSingingBowlPhrase(volume: Float) {
        val durationSec = 3.5
        val numSamples = (durationSec * sampleRate).toInt()
        val buffer = ShortArray(numSamples)
        val freqBase = 216.0 // Warm 432Hz harmonic base
        val freqOvertone = 432.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = sin(Math.PI * (t / durationSec)) * exp(-0.8 * t) // Pulsing swell
            val sample = (sin(2.0 * Math.PI * freqBase * t) * 0.7 + sin(2.0 * Math.PI * freqOvertone * t) * 0.3) * envelope
            buffer[i] = (sample * Short.MAX_VALUE * volume).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playBufferSync(buffer)
    }

    private suspend fun playSolfeggioPhrase(volume: Float) {
        // 528Hz Transformation / Miracle Tone phrase
        val melody = listOf(
            528.0 to 0.8,
            639.0 to 0.8,
            741.0 to 0.8,
            852.0 to 1.2
        )
        playMelodyPhrase(melody, volume)
    }

    private suspend fun playClassicBellPhrase(volume: Float) {
        val melody = listOf(
            880.0 to 0.5,
            880.0 to 0.5,
            880.0 to 0.8,
            1174.66 to 1.2
        )
        playMelodyPhrase(melody, volume)
    }

    private suspend fun playMelodyPhrase(notes: List<Pair<Double, Double>>, volume: Float) {
        for ((freq, duration) in notes) {
            if (alarmJob?.isActive != true) break
            val numSamples = (duration * sampleRate).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-2.5 * t)
                val sample = sin(2.0 * Math.PI * freq * t) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * volume).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playBufferSync(buffer)
            delay(50)
        }
    }

    private fun playBuffer(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            CoroutineScope(Dispatchers.Default).launch {
                delay(2000)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            Log.e("AudioSynthesizer", "Error playing buffer", e)
        }
    }

    private fun playBufferSync(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            currentAlarmTrack = track
            track.write(buffer, 0, buffer.size)
            track.play()

            // Wait for audio buffer to finish playing
            val durationMs = (buffer.size.toDouble() / sampleRate * 1000).toLong()
            Thread.sleep(durationMs)
            track.stop()
            track.release()
        } catch (_: Exception) {}
    }

    private fun fallbackTone() {
        try {
            val tone = ToneGenerator(AudioManager.STREAM_ALARM, 80)
            tone.startTone(ToneGenerator.TONE_PROP_BEEP, 300)
        } catch (_: Exception) {}
    }
}
