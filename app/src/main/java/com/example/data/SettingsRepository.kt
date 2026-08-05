package com.example.data

import android.content.Context
import android.content.SharedPreferences

data class AppSettings(
    val checkInIntervalMins: Int = 15,
    val reactionWindowSecs: Int = 30,
    val sleepAlarmMins: Int = 120, // Tahajjud alarm duration after sleep detected (e.g. 15 mins, 60 mins, etc.)
    val checkInSoundStyle: String = SOUND_CRYSTAL_TING,
    val alarmRingtoneStyle: String = RINGTONE_PEACEFUL_ADHAN,
    val customRingtoneUri: String? = null,
    val customRingtoneName: String? = null,
    val volume: Float = 0.8f,
    val vibrateOnCheckIn: Boolean = true,
    val blackoutModeEnabled: Boolean = true
) {
    companion object {
        const val SOUND_CRYSTAL_TING = "Crystal Ting"
        const val SOUND_BRASS_BELL = "Brass Bell"
        const val SOUND_RAIN_DROP = "Rain Drop"
        const val SOUND_SOFT_HARP = "Soft Harp"

        const val RINGTONE_CUSTOM = "Custom Storage Audio"
        const val RINGTONE_PEACEFUL_ADHAN = "Peaceful Adhan Tone"
        const val RINGTONE_DAWN_CHIME = "Dawn Sunrise Chime"
        const val RINGTONE_SINGING_BOWL = "Deep Singing Bowl"
        const val RINGTONE_RISING_ZEN = "Rising Solfeggio"
        const val RINGTONE_CLASSIC = "Classic Gentle Bell"
    }
}

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tahajjud_settings", Context.MODE_PRIVATE)

    fun getSettings(): AppSettings {
        return AppSettings(
            checkInIntervalMins = prefs.getInt("checkInIntervalMins", 15),
            reactionWindowSecs = prefs.getInt("reactionWindowSecs", 30),
            sleepAlarmMins = prefs.getInt("sleepAlarmMins", 120),
            checkInSoundStyle = prefs.getString("checkInSoundStyle", AppSettings.SOUND_CRYSTAL_TING) ?: AppSettings.SOUND_CRYSTAL_TING,
            alarmRingtoneStyle = prefs.getString("alarmRingtoneStyle", AppSettings.RINGTONE_PEACEFUL_ADHAN) ?: AppSettings.RINGTONE_PEACEFUL_ADHAN,
            customRingtoneUri = prefs.getString("customRingtoneUri", null),
            customRingtoneName = prefs.getString("customRingtoneName", null),
            volume = prefs.getFloat("volume", 0.8f),
            vibrateOnCheckIn = prefs.getBoolean("vibrateOnCheckIn", true),
            blackoutModeEnabled = prefs.getBoolean("blackoutModeEnabled", true)
        )
    }

    fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putInt("checkInIntervalMins", settings.checkInIntervalMins)
            .putInt("reactionWindowSecs", settings.reactionWindowSecs)
            .putInt("sleepAlarmMins", settings.sleepAlarmMins)
            .putString("checkInSoundStyle", settings.checkInSoundStyle)
            .putString("alarmRingtoneStyle", settings.alarmRingtoneStyle)
            .putString("customRingtoneUri", settings.customRingtoneUri)
            .putString("customRingtoneName", settings.customRingtoneName)
            .putFloat("volume", settings.volume)
            .putBoolean("vibrateOnCheckIn", settings.vibrateOnCheckIn)
            .putBoolean("blackoutModeEnabled", settings.blackoutModeEnabled)
            .apply()
    }
}
