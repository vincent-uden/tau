package com.uden.tau.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [WeightLog::class, Exercise::class, ExerciseLog::class, ExerciseSet::class, WorkoutLog::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun weightLogDao(): WeightLogDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseLogDao(): ExerciseLogDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun exerciseSetDao(): ExerciseSetDao

    companion object {
        private var INSTANCE: AppDb? = null

        fun getInstance(context: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): AppDb {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDb::class.java,
                "appdb"
            )
                .addCallback(seedDb(context))
                .build()
        }

        private fun seedDb(context: Context): Callback {
            return object : Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch {
                        val exDao = getInstance(context).exerciseDao()
                        if (exDao.count() == 0) {
                            exDao.insert(*(exerciseLists[1]!!))
                        }
                    }
                }
            }
        }
    }
}

var exerciseLists: Map<Int, Array<Exercise>> = hashMapOf(
    1 to arrayOf(
        Exercise(
            name = "Crunch",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Plank",
            quantity = ExerciseQuantity.Time,
        ),
        Exercise(
            name = "Russian Twist",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Back Extension",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Barbell Row",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Barbell Shrug",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Chin Up",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Deadlift",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Dumbbell Row",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Lat Pulldown",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Pull Up",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Rack Pull",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Seated Cable Row",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "T-Bar Row",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Barbell Curl",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Cable Curl",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Dumbbell Curl",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Dumbbell Hammer Curl",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Dumbbell Preacher Curl",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "EZ-Bar Curl",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "EZ-Bar Preacher Curl",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Seated Incline Dumbbell Curl",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Spider Curl",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Cycling",
            quantity = ExerciseQuantity.Time,
        ),
        Exercise(
            name = "Elliptical Trainer",
            quantity = ExerciseQuantity.Time,
        ),
        Exercise(
            name = "Rowing Machine",
            quantity = ExerciseQuantity.Time,
        ),
        Exercise(
            name = "Running (Outdoor)",
            quantity = ExerciseQuantity.Time,
        ),
        Exercise(
            name = "Running (Treadmill)",
            quantity = ExerciseQuantity.Time,
        ),
        Exercise(
            name = "Cable Crossover",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Cable Fly",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Decline Barbell Bench Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Flat Barbell Bench Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Flat Dumbbell Bench Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Flat Dumbbell Fly",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Incline Barbell Bench Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Incline Dumbbell Bench Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Push Up",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Barbell Front Squat",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Barbell Hip Thrust",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Barbell Squat",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Bulgarian Split Squat",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Leg Extension Machine",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Leg Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Lying Leg Curl Machine",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Romanian Deadlift",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Seated Calf Raise Machine",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Seated Leg Curl Machine",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Standing Calf Raise Machine",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Stiff-Legged Deadlift",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Sumo Deadlift",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Cable Face Pull",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Front Dumbbell Raise",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Lateral Dumbbell Raise",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Lateral Machine Raise",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Overhead Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Push Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Rear Delt Dumbbell Raise",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Seated Dumbbell Lateral Raise",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Seated Dumbbell Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Smith Machine Overhead Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Cable Overhead Triceps Extension",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Close Grip Barbell Bench Press",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Dumbbell Overhead Triceps Extension",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "EZ-Bar Skullcrusher",
            quantity = ExerciseQuantity.Weight,
        ),
        Exercise(
            name = "Rope Push Down",
            quantity = ExerciseQuantity.Weight,
        ),
    )
)