package com.uden.tau.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uden.tau.R
import com.uden.tau.db.Exercise
import com.uden.tau.db.ExerciseSet
import com.uden.tau.ui.theme.LightBlue
import com.uden.tau.ui.theme.LightGreen
import com.uden.tau.ui.theme.LightRed

@Composable
fun ExerciseSetLogger(viewModel: MainViewModel) {
    val _state = viewModel.workoutOverViewState.observeAsState()

    var weight by remember { mutableFloatStateOf(0.0f) }
    var reps by remember { mutableIntStateOf(0) }
    var modifyingSet by remember { mutableStateOf<ExerciseSet?>(null) }

    _state.value?.let { state ->
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            // Exercise Name
            Row {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = state.modifyingExercise?.name ?: "",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(text = "Weight (kg)", color = Color.Gray)
            key(modifyingSet) {
                // Weight Entry
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier
                            .width(36.dp)
                            .height(36.dp),
                        painter = painterResource(id = R.drawable.minus),
                        contentDescription = "Calendar",
                        tint = colorResource(id = R.color.light_blue),
                    )
                    Spacer(Modifier.width(12.dp))
                    NumberInput(
                        modifier = Modifier.width(120.dp),
                        onChange = {
                            it.toFloatOrNull()
                                ?.let { parsed -> weight = parsed }
                        },
                        value = weight.toString(),
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        modifier = Modifier
                            .width(36.dp)
                            .height(36.dp),
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Calendar",
                        tint = colorResource(id = R.color.light_blue),
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Reps", color = Color.Gray)
            key(modifyingSet) {
                // Reps entry
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier
                            .width(36.dp)
                            .height(36.dp),
                        painter = painterResource(id = R.drawable.minus),
                        contentDescription = "Calendar",
                        tint = colorResource(id = R.color.light_blue),
                    )
                    Spacer(Modifier.width(12.dp))
                    NumberInput(
                        modifier = Modifier.width(120.dp),
                        onChange = {
                            it.toIntOrNull()?.let { parsed -> reps = parsed }
                        },
                        value = reps.toString(),
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        modifier = Modifier
                            .width(36.dp)
                            .height(36.dp),
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Calendar",
                        tint = colorResource(id = R.color.light_blue),
                    )
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
            // Save and clear/cancel
            Row(Modifier.padding(horizontal = 16.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(
                            LightGreen
                        )
                        .padding(vertical = 16.dp)
                        .weight(1f)
                        .clickable {
                            if (modifyingSet == null) {
                                viewModel.createExerciseSet(
                                    state.workoutLog,
                                    state.modifyingExercise!!,
                                    state.modifyingExerciseLog!!,
                                    weight,
                                    reps
                                )
                            } else {
                                viewModel.updateExerciseSet(
                                    modifyingSet!!.copy(weight = weight, reps = reps),
                                )
                                viewModel.setActiveExerciseLog(state.modifyingExerciseLog!!)
                                modifyingSet = null
                                weight = 0.0f
                                reps = 0
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Save")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(
                            if (modifyingSet == null) LightBlue else LightRed
                        )
                        .padding(vertical = 16.dp)
                        .weight(1f)
                        .clickable {
                            if (modifyingSet != null) {
                                viewModel.deleteExerciseSet(modifyingSet!!)
                                modifyingSet = null
                            }
                            weight = 0.0f
                            reps = 0
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(modifier = Modifier, text = if (modifyingSet == null) "Clear" else "Delete")
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
            Table(
                modifier = Modifier,
                rowModifier = { i ->
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (modifyingSet?.id == state.matchingSets[i].id)
                                LightBlue.copy(alpha = 0.2f)
                            else
                                Color.Transparent
                        )
                        .padding(4.dp)
                },
                columnCount = 5,
                rowCount = state.matchingSets.size,
                cellContent = { j, i ->
                    ExerciseSetCell(
                        row = i,
                        col = j,
                        exSet = state.matchingSets[i],
                    )
                },
                alignments = listOf(
                    TableAlignment.Right,
                    TableAlignment.Right,
                    TableAlignment.Left,
                    TableAlignment.Right,
                    TableAlignment.Left,
                ),
                onRowClick = { i ->
                    if (modifyingSet?.id == state.matchingSets[i].id) {
                        modifyingSet = null
                    } else {
                        modifyingSet = state.matchingSets[i]
                        weight = modifyingSet!!.weight!!
                        reps = modifyingSet!!.reps
                    }
                }
            )

            // Confirm Button
            Row {
            }
        }
    }

}

@Composable
fun ExerciseSetCell(
    row: Int,
    col: Int,
    exSet: ExerciseSet
) {
    val bigFontSize = 22
    val bigText =
        MaterialTheme.typography.bodyLarge.copy(fontSize = bigFontSize.sp)
    val smallText =
        MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
    when (col) {
        0 -> Text(
            modifier = Modifier,
            textAlign = TextAlign.Right,
            text = "${row + 1}",
            style = bigText
        )

        1 -> Text(
            modifier = Modifier.padding(start = 64.dp),
            textAlign = TextAlign.Right,
            text = "${exSet.weight}",
            style = bigText
        )

        2 -> Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .offset(y = with(LocalDensity.current) { (bigFontSize - 16).sp.toDp() }),
            textAlign = TextAlign.Left,
            text = "kg",
            style = smallText
        )

        3 -> Text(
            modifier = Modifier.padding(start = 64.dp),
            textAlign = TextAlign.Center,
            text = "${exSet.reps}",
            style = bigText
        )

        4 -> Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .offset(y = with(LocalDensity.current) { (bigFontSize - 16).sp.toDp() }),
            textAlign = TextAlign.Left,
            text = "reps",
            style = smallText
        )
    }
}