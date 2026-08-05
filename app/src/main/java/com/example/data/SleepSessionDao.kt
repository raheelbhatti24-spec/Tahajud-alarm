package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SleepSession): Long

    @Update
    suspend fun updateSession(session: SleepSession)

    @Query("SELECT * FROM sleep_sessions ORDER BY startTimeMs DESC")
    fun getAllSessions(): Flow<List<SleepSession>>

    @Query("SELECT * FROM sleep_sessions ORDER BY startTimeMs DESC LIMIT 1")
    suspend fun getLatestSession(): SleepSession?

    @Query("DELETE FROM sleep_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    @Query("DELETE FROM sleep_sessions")
    suspend fun clearAll()
}
