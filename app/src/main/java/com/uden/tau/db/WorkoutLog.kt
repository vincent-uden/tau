package com.uden.tau.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import java.time.Instant

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Instant = Instant.now(),
    @ColumnInfo(name = "name") val name: String,
)

enum class ExerciseQuantity {
    Weight,
    Time,
}

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Instant = Instant.now(),
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "quantity") val quantity: ExerciseQuantity,
)

@Entity(tableName = "exercise_sets")
data class ExerciseSet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Instant = Instant.now(),
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "workout_id") val workoutId: Int,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "weight") val weight: Float? = null,
    @ColumnInfo(name = "time") val time: Long? = null,
    @ColumnInfo(name = "comment") val comment: String = "",
)

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM workout_logs ORDER BY created_at DESC")
    suspend fun getAll(): List<WorkoutLog>

    @Query("SELECT * FROM workout_logs WHERE id = :id")
    suspend fun getById(id: Int): WorkoutLog?

    @Query("SELECT * FROM workout_logs WHERE created_at >= :from AND created_at <= :to ORDER BY created_at ASC")
    suspend fun getAllInDateRange(from: Instant, to: Instant): List<WorkoutLog>

    @Query(
        "SELECT" +
        "   workout_logs.name as name," +
        "   workout_logs.id as workoutId," +
        "   IFNULL(inner_count, 0) as count " +
        "FROM" +
        "   workout_logs " +
        "   LEFT JOIN" +
        "      (" +
        "         SELECT" +
        "            workout_logs.id as workoutId," +
        "            COUNT(*) as inner_count " +
        "         FROM" +
        "            workout_logs " +
        "            JOIN" +
        "               exercise_sets " +
        "               ON workout_logs.id = exercise_sets.workout_id " +
        "            JOIN" +
        "               exercises " +
        "               ON exercise_id = exercises.id " +
        "         GROUP BY" +
        "            workout_logs.id" +
        "      )" +
        "      ON workoutId = workout_logs.id " +
        "WHERE" +
        "   created_at >= :from AND created_at <= :to " +
        "ORDER BY" +
        "   workout_logs.created_at ASC"
    )
    suspend fun getAllInDateRangeWithSetCounts(
        from: Instant,
        to: Instant
    ): List<WorkoutWithSetCount>

    @Insert
    suspend fun insert(vararg logs: WorkoutLog): List<Long>

    @Update
    suspend fun update(vararg logs: WorkoutLog)

    @Delete
    suspend fun delete(vararg logs: WorkoutLog)

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun get(id: Int): Exercise

    @Query("SELECT * FROM exercises ORDER BY created_at DESC")
    suspend fun getAll(): List<Exercise>

    @Query("SELECT * FROM exercises WHERE name LIKE :search")
    suspend fun search(search: String): List<Exercise>

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int

    @Insert
    suspend fun insert(vararg exercises: Exercise)

    @Update
    suspend fun update(vararg exercises: Exercise)

    @Delete
    suspend fun delete(vararg exercises: Exercise)
}

data class SetGroup(
    val count: Int,
    val name: String,
    val exerciseId: Int,
)

data class WorkoutWithSetCount(
    val name: String,
    val count: Int,
    val workoutId: Int,
)

@Dao
interface ExerciseSetDao {
    @Query("SELECT * FROM exercise_sets ORDER BY created_at DESC")
    suspend fun getAll(): List<ExerciseSet>

    @Query("SELECT * FROM exercise_sets WHERE created_at >= :from AND created_at <= :to ORDER BY created_at ASC")
    suspend fun getAllInDateRange(from: Instant, to: Instant): List<ExerciseSet>

    @Query("SELECT * FROM exercise_sets WHERE exercise_id = :exerciseId ORDER BY created_at ASC")
    suspend fun getAllForExercise(exerciseId: Int): List<ExerciseSet>

    @Query("SELECT * FROM exercise_sets WHERE workout_id = :workoutId ORDER BY created_at ASC")
    suspend fun getAllForWorkout(workoutId: Int): List<ExerciseSet>

    @Query("SELECT * FROM exercise_sets LEFT JOIN exercises ON exercise_sets.exercise_id = exercises.id WHERE workout_id = :workoutId AND exercise_id = :exerciseId ORDER BY created_at ASC")
    suspend fun getAllForWorkoutAndExercise(
        workoutId: Int,
        exerciseId: Int
    ): Map<ExerciseSet, Exercise>

    @Query("SELECT exercises.name as name, exercise_id as exerciseId, COUNT(*) as count FROM exercise_sets JOIN exercises ON exercise_id = exercises.id WHERE workout_id = :workoutId GROUP BY exercise_id")
    suspend fun countByExercise(workoutId: Int): List<SetGroup>

    @Insert
    suspend fun insert(vararg sets: ExerciseSet)

    @Update
    suspend fun update(vararg sets: ExerciseSet)

    @Delete
    suspend fun delete(vararg sets: ExerciseSet)
}