package com.uden.tau.main

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uden.tau.R
import com.uden.tau.ULog
import com.uden.tau.ui.theme.LightBlue


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkoutOverview(viewModel: MainViewModel) {
    val _state = viewModel.workoutOverViewState.observeAsState()

    val animBg by animateColorAsState(
        if (_state.value?.addingSet != null) colorResource(R.color.light_red) else colorResource(
            R.color.light_blue
        ),
        label = "bgColor"
    )
    val animRotAdd by animateFloatAsState(
        if (_state.value?.addingSet != null) 135f else 0f,
        label = "addRot"
    )
    val btnOpacity by animateFloatAsState(
        if (_state.value?.addingSet == WorkoutAddingEntry.PENDING) 1f else 0f,
        label = "btnOpacity"
    )
    val haptics = LocalHapticFeedback.current

    _state.value?.let { state ->
        // TODO: Why is this not recomposing?
        if (state.modifyingExercise == null) {
            Column(
                Modifier
                    .padding(18.dp)
                    .fillMaxHeight()
            ) {
                when (state.addingSet) {
                    WorkoutAddingEntry.EXERCISE -> {
                        ExerciseAdder(viewModel)
                    }

                    WorkoutAddingEntry.SUPERSET, WorkoutAddingEntry.PENDING, null -> {
                        Row {
                            TextInput(
                                modifier = Modifier.fillMaxWidth(),
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit),
                                        contentDescription = "Calendar",
                                        tint = colorResource(id = R.color.light_blue),
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                },
                                initialValue = state.workoutLog.name
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 16.dp)
                        ) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(
                                    8.dp
                                )
                            ) {
                                itemsIndexed(
                                    state.exerciseLogs
                                ) { i, log ->
                                    SetGroupDisplay(
                                        modifier = Modifier.combinedClickable(
                                            onClick = {
                                                ULog.d(log)
                                                viewModel.setActiveExerciseLogById(
                                                    log.exerciseLogId
                                                )
                                            },
                                            onLongClick = {
                                                haptics.performHapticFeedback(
                                                    HapticFeedbackType.LongPress
                                                )
                                                if (state.selectedSetGroup == i) {
                                                    viewModel.selectSetGroup(
                                                        null
                                                    )
                                                } else {
                                                    viewModel.selectSetGroup(i)
                                                }

                                            },
                                            onLongClickLabel = "",
                                        ),
                                        name = log.name,
                                        count = log.count,
                                        active = state.selectedSetGroup == i,
                                        onDeleteClick = { viewModel.deleteExerciseLog(log.exerciseLogId) },
                                    )
                                }
                            }

                        }
                    }
                }
                if (state.addingSet == WorkoutAddingEntry.SUPERSET) {
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    BoxWithConstraints(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(animBg)
                                .clickable(
                                    indication = null,
                                    interactionSource = MutableInteractionSource()
                                ) {
                                    if (state.addingSet != null) {
                                        viewModel.workoutAddEntry(null)
                                    } else {
                                        viewModel.workoutAddEntry(
                                            WorkoutAddingEntry.PENDING
                                        )
                                    }
                                }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.add),
                                contentDescription = "Add Entry",
                                tint = colorResource(id = R.color.white),
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.Center)
                                    .rotate(animRotAdd)
                            )
                        }
                        ExtendedFloatingActionButton(
                            onClick = {
                                viewModel.workoutAddEntry(
                                    WorkoutAddingEntry.SUPERSET
                                )
                            },
                            modifier = Modifier
                                .absoluteOffset(y = (-96).dp)
                                .alpha(btnOpacity),
                            shape = RoundedCornerShape(100),
                            containerColor = LightBlue,
                        ) {
                            Text(
                                "Superset",
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .width(100.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            /*
                            Icon(
                                painter = painterResource(id = R.drawable.barbell),
                                contentDescription = "",
                                tint = DarkBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            */
                        }
                        ExtendedFloatingActionButton(
                            onClick = {
                                viewModel.workoutAddEntry(
                                    WorkoutAddingEntry.EXERCISE
                                )
                            },
                            modifier = Modifier
                                .absoluteOffset(y = (-160).dp)
                                .alpha(btnOpacity),
                            shape = RoundedCornerShape(100),
                            containerColor = LightBlue,
                        ) {
                            Text(
                                "Exercise",
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .width(100.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            /*
                            Icon(
                                painter = painterResource(id = R.drawable.scale),
                                contentDescription = "",
                                tint = DarkBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            */
                        }
                    }
                }
            }
        } else {
            ExerciseSetLogger(viewModel = viewModel)
        }
    }
}

@Composable
fun ExerciseAdder(viewModel: MainViewModel) {
    val _state = viewModel.workoutOverViewState.observeAsState()

    _state.value?.let { state ->
        Column {
            Row {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = state.workoutLog.name
                )
            }
            Row {
                TextInput(
                    modifier = Modifier
                        .fillMaxWidth(),
                    initialValue = "",
                    onChange = { viewModel.searchExercises(it) }
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp
                    )
                ) {
                    itemsIndexed(
                        state.matchingExercises
                    ) { i, exercise ->
                        Text(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.workoutAddExerciseLog(state.workoutLog, exercise)
                                },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 24.sp
                            ),
                            text = exercise.name
                        )
                    }
                }
            }
        }
    }
}
