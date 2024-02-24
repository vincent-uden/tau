package com.uden.tau.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.uden.tau.R
import com.uden.tau.ui.theme.LightGreen
import java.time.Instant

@Composable
fun WeightLogger(viewModel: MainViewModel) {
    val state = viewModel.overviewState.observeAsState()
    val animBg by animateColorAsState(
        if (state.value?.addingEntry == true) colorResource(R.color.light_red) else colorResource(
            R.color.light_blue
        ),
        label = "bgColor"
    )

    val pendingWeight = remember { mutableStateOf<Float?>(null) }

    val animRotAdd by animateFloatAsState(
        if (state.value?.addingEntry == true) 135f else 0f,
        label = "addRot"
    )

    Column(
        Modifier
            .padding((18 + 24).dp)
            .fillMaxHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Log Weight",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            NumberInput(modifier = Modifier.fillMaxWidth(), onChange = {
                pendingWeight.value = it.toFloatOrNull()
            })
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
                            viewModel.openPanel(VisiblePanel.OVERVIEW)
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
                FloatingActionButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .absoluteOffset(y = (-96).dp),
                    shape = RoundedCornerShape(100),
                    containerColor = LightGreen,
                ) {
                    Text(
                        "Log Weight",
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .align(Alignment.Center)
                            .width(100.dp)
                            .clickable {
                                if (pendingWeight.value != null) {
                                    viewModel.createWeightLog(
                                        Instant.now(),
                                        pendingWeight.value!!
                                    )
                                    pendingWeight.value = null
                                    viewModel.openPanel(VisiblePanel.OVERVIEW)
                                    viewModel.toggleAddingEntry()
                                    viewModel.changeDate(state.value?.activeDate)
                                }
                            },
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
