package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_sessions")
data class SleepSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimeMs: Long,
    val detectedSleepTimeMs: Long?,
    val alarmScheduledTimeMs: Long?,
    val actualWakeUpTimeMs: Long? = null,
    val reactionsCount: Int,
    val checkInIntervalMins: Int,
    val sleepAlarmMins: Int,
    val status: String, // "CHECKING", "SLEEP_DETECTED", "ALARM_RINGING", "WOKE_UP", "CANCELLED"
    val note: String = ""
)
