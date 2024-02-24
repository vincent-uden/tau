package com.uden.tau.db

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Update
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Entity(tableName = "weight_logs")
data class WeightLog (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Instant = Instant.now(),
    val weight: Float,
)

@Dao
interface WeightLogDao {
    @Query("SELECT * FROM weight_logs ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatest(): WeightLog?

    @Query("SELECT * FROM weight_logs ORDER BY created_at DESC")
    suspend fun getAll(): List<WeightLog>

    @Query("SELECT * FROM weight_logs WHERE created_at >= :from AND created_at <= :to ORDER BY created_at ASC")
    suspend fun getAllInDateRange(from: Instant, to: Instant): List<WeightLog>

    @Insert
    suspend fun insert(vararg logs: WeightLog)

    @Update
    suspend fun update(vararg logs: WeightLog)

    @Delete
    suspend fun delete(vararg logs: WeightLog)
}

class Converters {
    @TypeConverter
    fun fromDate(date: Instant): Long {
        return date.toEpochMilli()
    }

    @TypeConverter
    fun toDate(millis: Long): Instant {
        return Instant.ofEpochMilli(millis)
    }

    @TypeConverter
    fun fromDuration(duration: Duration): Long {
        return duration.inWholeMilliseconds
    }

    @TypeConverter
    fun toDuration(millis: Long): Duration {
        return millis.milliseconds
    }
}