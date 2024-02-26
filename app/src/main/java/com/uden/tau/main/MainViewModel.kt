package com.uden.tau.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uden.tau.DateFormat
import com.uden.tau.ULog
import com.uden.tau.db.AppDb
import com.uden.tau.db.Exercise
import com.uden.tau.db.ExerciseLog
import com.uden.tau.db.ExerciseLogDisplay
import com.uden.tau.db.ExerciseSet
import com.uden.tau.db.WeightLog
import com.uden.tau.db.WorkoutLog
import com.uden.tau.db.WorkoutWithSetCount
import com.uden.tau.formatInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class MainState(
    val name: String,
    val i: Int,
    val visiblePanel: VisiblePanel
)

data class OverviewState(
    val addingEntry: Boolean,
    val activeDate: LocalDate,
    val visibleLogs: List<WeightLog>,
    val visibleWorkoutLogs: List<WorkoutWithSetCount>,
    val selectedLog: Int?,
)

data class WorkoutOverViewState(
    val workoutLog: WorkoutLog,
    val sets: List<ExerciseSet>,
    val exerciseLogs: List<ExerciseLogDisplay>,
    val addingSet: WorkoutAddingEntry?,
    val matchingExercises: List<Exercise>,

    val modifyingExercise: Exercise?,
    val modifyingExerciseLog: ExerciseLog?,
    val matchingSets: List<ExerciseSet>,
    val selectedSetGroup: Int?,
)

enum class VisiblePanel {
    OVERVIEW,
    WEIGHT_LOG,
    WORKOUT,
}

enum class WorkoutAddingEntry {
    PENDING,
    EXERCISE,
    SUPERSET,
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _state =
        MutableLiveData(MainState("1", 0, VisiblePanel.OVERVIEW))
    val state: LiveData<MainState>
        get() = _state

    private val db = AppDb.getInstance(application)

    private val _overviewState = MutableLiveData(
        OverviewState(
            false,
            LocalDate.now(),
            listOf(),
            listOf(),
            null
        )
    )
    val overviewState: LiveData<OverviewState>
        get() = _overviewState

    private val _workoutOverViewState = MutableLiveData(
        WorkoutOverViewState(
            WorkoutLog(name = ""),
            listOf(),
            listOf(),
            null,
            listOf(),
            null,
            null,
            listOf(),
            null,
        )
    )
    val workoutOverViewState: LiveData<WorkoutOverViewState>
        get() = _workoutOverViewState

    init {
        changeDate(_overviewState.value!!.activeDate)
    }

    fun changeDate(newDate: LocalDate?) {
        if (newDate == null) {
            return
        }
        _overviewState.value?.let {
            val from = newDate.atStartOfDay().atZone(ZoneId.systemDefault())
                    .toInstant()
            val to = newDate.plusDays(1).atStartOfDay()
                    .atZone(ZoneId.systemDefault()).toInstant()
            viewModelScope.launch(Dispatchers.IO) {
                val logs = db.weightLogDao().getAllInDateRange(from, to)
                val workoutLogs =
                        db.workoutLogDao().getAllInDateRangeWithSetCounts(from, to)
                updateOverviewState(
                        overviewState.value!!.copy(
                                activeDate = newDate,
                                visibleLogs = logs,
                                visibleWorkoutLogs = workoutLogs
                        )
                )
            }
        }
    }

    fun createExerciseSet(
            workoutLog: WorkoutLog,
            exercise: Exercise,
            exerciseLog: ExerciseLog,
            weight: Float,
            reps: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            db.exerciseSetDao().insert(
                    ExerciseSet(
                            workoutId = workoutLog.id,
                            exerciseId = exercise.id,
                            exerciseLogId = exerciseLog.id,
                            weight = weight,
                            reps = reps,
                    )
            )
            updateWorkoutOverViewState(
                    _workoutOverViewState.value!!.copy(
                            matchingSets = findMatchingSets(exerciseLog)
                    )
            )
        }
    }


    fun createWeightLog(timestamp: Instant, weight: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            db.weightLogDao()
                    .insert(WeightLog(createdAt = timestamp, weight = weight))
            refreshLogs()
        }
    }


    fun createWorkoutLog() {
        viewModelScope.launch(Dispatchers.IO) {
            val now = Instant.now()
            val newLog = db.workoutLogDao().getById(
                    db.workoutLogDao().insert(
                            WorkoutLog(
                                    name = formatInstant(
                                            DateFormat.Date,
                                            now
                                    ) + " Workout"
                            )
                    )[0].toInt()
            )
            updateWorkoutOverViewState(
                    _workoutOverViewState.value!!.copy(workoutLog = newLog!!)
            )
            openPanel(VisiblePanel.WORKOUT)
        }
    }

    fun deleteExerciseSet(exerciseSet: ExerciseSet) {
        viewModelScope.launch(Dispatchers.IO) {
            db.exerciseSetDao().delete(exerciseSet)
            updateWorkoutOverViewState(
                    _workoutOverViewState.value!!.copy(
                            matchingSets = findMatchingSets(
                                    _workoutOverViewState.value!!.modifyingExerciseLog
                            )
                    )
            )
        }
    }

    fun deleteExerciseLog(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.exerciseSetDao().delete(*db.exerciseSetDao().getAllForExerciseLog(id).map { it }.toTypedArray())
            db.exerciseLogDao().deleteById(id)
            updateWorkoutOverViewState(
                _workoutOverViewState.value!!.copy(
                    exerciseLogs = findMatchingSetGroups(_workoutOverViewState.value!!.workoutLog)
                )
            )
        }
    }

    fun deleteWeightLog(log: WeightLog) {
        viewModelScope.launch(Dispatchers.IO) {
            db.weightLogDao().delete(log)
            refreshLogs()
        }
        selectLog(null)
    }

    fun deleteWorkoutLog(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.exerciseSetDao().delete(*db.exerciseSetDao().getAllForWorkout(id).map { it }.toTypedArray())
            db.exerciseLogDao().delete(*db.exerciseLogDao().getInWorkout(id).map { it }.toTypedArray())
            db.workoutLogDao().deleteById(id)
            refreshLogs()
        }
        selectLog(null)
    }

    fun openPanel(panel: VisiblePanel) {
        updateMainState(state.value!!.copy(visiblePanel = panel))
    }


    fun openWorkoutLog(workoutId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val log = db.workoutLogDao().getById(workoutId)
            log?.let {
                updateWorkoutOverViewState(
                    _workoutOverViewState.value!!.copy(
                        workoutLog = log,
                        exerciseLogs = findMatchingSetGroups(log)
                    )
                )
                openPanel(VisiblePanel.WORKOUT)
            }
        }
    }

    fun searchExercises(nameQuery: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val matches = db.exerciseDao().search("%$nameQuery%")
            updateWorkoutOverViewState(
                _workoutOverViewState.value!!.copy(
                    matchingExercises = matches
                )
            )
        }
    }

    fun selectLog(index: Int?) {
        _overviewState.value?.let {
            _overviewState.value = it.copy(selectedLog = index)
        }
    }

    fun selectSetGroup(index: Int?) {
        _workoutOverViewState.value?.let {
            _workoutOverViewState.value = it.copy(selectedSetGroup = index)
        }
    }


    fun setActiveExerciseLog(exerciseLog: ExerciseLog?) {
        viewModelScope.launch(Dispatchers.IO) {
            val exercise = db.exerciseDao().get(exerciseLog?.exerciseId ?: 0)
            ULog.d("$exerciseLog $exercise")

            updateWorkoutOverViewState(
                _workoutOverViewState.value!!.copy(
                    modifyingExercise = exercise,
                    modifyingExerciseLog = exerciseLog,
                    matchingSets = findMatchingSets(exerciseLog),
                )
            )
        }
    }

    fun setActiveExerciseLogById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val modifyingExercise = db.exerciseLogDao().getById(id)
            ULog.d(modifyingExercise)
            modifyingExercise?.let { setActiveExerciseLog(it) }
        }
    }

    fun toggleAddingEntry() {
        when (_state.value!!.visiblePanel) {
            VisiblePanel.OVERVIEW, VisiblePanel.WEIGHT_LOG -> {
                _overviewState.value?.let {
                    _overviewState.value =
                            _overviewState.value?.copy(addingEntry = !it.addingEntry)
                }
            }

            else -> {}
        }
    }

    fun updateExerciseSet(exerciseSet: ExerciseSet) {
        viewModelScope.launch(Dispatchers.IO) {
            db.exerciseSetDao().update(exerciseSet)
        }
    }

    fun workoutAddEntry(type: WorkoutAddingEntry?) {
        updateWorkoutOverViewState(_workoutOverViewState.value!!.copy(addingSet = type))
    }

    fun workoutAddExerciseLog(workout: WorkoutLog, exercise: Exercise) {
        viewModelScope.launch(Dispatchers.IO) {
            db.exerciseLogDao().insert(
                ExerciseLog(
                    id = 0, exerciseId = exercise.id, workoutId = workout.id
                )
            )
            updateWorkoutOverViewState(
                _workoutOverViewState.value!!.copy(
                    exerciseLogs = findMatchingSetGroups(workout),
                    addingSet = null,
                )
            )
        }
    }


    private fun refreshLogs() {
        _overviewState.value?.let {
            changeDate(it.activeDate)
        }
    }


    private fun updateOverviewState(state: OverviewState) {
        viewModelScope.launch(Dispatchers.Main) {
            _overviewState.value = state
        }
    }

    private fun updateWorkoutOverViewState(state: WorkoutOverViewState) {
        ULog.d("Updating to new state: $state", stackTraceLevels = 3)
        viewModelScope.launch(Dispatchers.Main) {
            _workoutOverViewState.value = state
        }
    }

    private fun updateMainState(state: MainState) {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = state
        }
    }

    private suspend fun findMatchingSets(exerciseLog: ExerciseLog?): List<ExerciseSet> {
        return db.exerciseSetDao().getAllForExerciseLog(exerciseLog?.id ?: 0)
    }

    private suspend fun findMatchingSetGroups(workoutLog: WorkoutLog): List<ExerciseLogDisplay> {
        return db.exerciseLogDao().getInWorkoutWithSetCounts(workoutLog.id)
    }

}