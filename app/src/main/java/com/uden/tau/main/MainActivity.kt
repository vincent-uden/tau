package com.uden.tau.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.ViewModelProvider
import com.uden.tau.R
import com.uden.tau.ui.theme.DarkBlue
import com.uden.tau.ui.theme.LightBlue
import com.uden.tau.ui.theme.TauTheme
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = this.onBackPressedDispatcher.addCallback(this) {
            when (viewModel.state.value?.visiblePanel) {
                VisiblePanel.OVERVIEW -> super.onBackPressed()
                VisiblePanel.WEIGHT_LOG -> {
                    viewModel.changeDate(viewModel.overviewState.value?.activeDate)
                    viewModel.openPanel(VisiblePanel.OVERVIEW)
                }
                VisiblePanel.WORKOUT -> {
                    if (viewModel.workoutOverViewState.value?.modifyingExercise != null) {
                        viewModel.setActiveExercise(null)
                        viewModel.findMatchingSetGroups(viewModel.workoutOverViewState.value!!.workoutLog)
                    } else if (viewModel.workoutOverViewState.value?.addingSet != null) {
                        viewModel.workoutAddEntry(null)
                        viewModel.findMatchingSetGroups(viewModel.workoutOverViewState.value!!.workoutLog)
                    } else {
                        viewModel.changeDate(viewModel.overviewState.value?.activeDate)
                        viewModel.openPanel(VisiblePanel.OVERVIEW)
                    }
                }
                null -> super.onBackPressed()
            }
        }
        callback.isEnabled = true

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            TauTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PanelHolder(viewModel)
                }
            }
        }
    }
}

@Composable
fun PanelHolder(viewModel: MainViewModel) {
    val state = viewModel.state.observeAsState()

    when (state.value?.visiblePanel) {
        VisiblePanel.OVERVIEW -> {
            MainOverview(viewModel)
        }

        VisiblePanel.WEIGHT_LOG -> {
            WeightLogger(viewModel)
        }

        VisiblePanel.WORKOUT -> {
            WorkoutOverview(viewModel = viewModel)
        }

        else -> {}
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainOverview(viewModel: MainViewModel) {
    val state = viewModel.overviewState.observeAsState()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var offsetX by remember { mutableFloatStateOf(0f) }

    val animBg by animateColorAsState(
        if (state.value?.addingEntry == true) colorResource(R.color.light_red) else colorResource(
            R.color.light_blue
        ),
        label = "bgColor"
    )
    val animRotAdd by animateFloatAsState(
        if (state.value?.addingEntry == true) 135f else 0f,
        label = "addRot"
    )
    val btnOpacity by animateFloatAsState(
        if (state.value?.addingEntry == true) 1f else 0f,
        label = "btnOpacity"
    )

    val haptics = LocalHapticFeedback.current

    Column(
        Modifier
            .padding(18.dp)
            .fillMaxHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Calendar",
                tint = colorResource(id = R.color.light_blue)
            )
            Text(
                state.value?.activeDate?.format(formatter) ?: "",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
            )
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Calendar",
                tint = colorResource(id = R.color.light_blue)
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            Column {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(
                        state.value?.visibleLogs ?: listOf()
                    ) { i, log ->
                        WeightLogDisplay(
                            log = log,
                            i == state.value!!.selectedLog,
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    haptics.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    if (state.value?.selectedLog == i) {
                                        viewModel.selectLog(null)
                                    } else {
                                        viewModel.selectLog(i)
                                    }

                                },
                                onLongClickLabel = "",
                            ),
                            onDeleteClick = { viewModel.deleteLog(log) }
                        )
                    }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(
                        state.value?.visibleWorkoutLogs ?: listOf()
                    ) { i, log ->
                        WorkoutLogDisplay(
                            log = log,
                            active = i + state.value!!.visibleLogs.size == state.value!!.selectedLog,
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    viewModel.openWorkoutLog(log.workoutId)
                                },
                                onLongClick = {
                                    haptics.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    if (state.value?.selectedLog == i + state.value!!.visibleLogs.size) {
                                        viewModel.selectLog(null)
                                    } else {
                                        viewModel.selectLog(i + state.value!!.visibleLogs.size)
                                    }

                                },
                                onLongClickLabel = "",
                            ),
                            onDeleteClick = { viewModel.deleteWorkoutLog(log.workoutId) }
                        )
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX += delta
                    },
                    onDragStopped = {
                        if (offsetX > 20) {
                            viewModel.changeDate(
                                state.value!!.activeDate.minusDays(1)
                            )
                        } else if(offsetX < -20) {
                            viewModel.changeDate(
                                state.value!!.activeDate.plusDays(1)
                            )
                        }
                        offsetX = 0f
                    }
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = "Calendar",
                tint = colorResource(id = R.color.white),
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        viewModel.changeDate(
                            state.value!!.activeDate.minusDays(1)
                        )
                    }
            )
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
                            viewModel.toggleAddingEntry()
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
                    onClick = { viewModel.createWorkoutLog() },
                    modifier = Modifier
                        .absoluteOffset(y = (-96).dp)
                        .alpha(btnOpacity),
                    shape = RoundedCornerShape(100),
                    containerColor = LightBlue,
                ) {
                    Text(
                        "Workout",
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .width(100.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.barbell),
                        contentDescription = "Add Workout",
                        tint = DarkBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                ExtendedFloatingActionButton(
                    onClick = { viewModel.openPanel(VisiblePanel.WEIGHT_LOG) },
                    modifier = Modifier
                        .absoluteOffset(y = (-160).dp)
                        .alpha(btnOpacity),
                    shape = RoundedCornerShape(100),
                    containerColor = LightBlue,
                ) {
                    Text(
                        "Weight Log",
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .width(100.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.scale),
                        contentDescription = "Add Workout",
                        tint = DarkBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "Calendar",
                tint = colorResource(id = R.color.white),
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        viewModel.changeDate(
                            state.value!!.activeDate.plusDays(1)
                        )
                    }
            )
        }
    }
}