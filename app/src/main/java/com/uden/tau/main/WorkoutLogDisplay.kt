package com.uden.tau.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uden.tau.R
import com.uden.tau.db.Exercise
import com.uden.tau.db.ExerciseQuantity
import com.uden.tau.db.WorkoutLog
import com.uden.tau.db.WorkoutWithSetCount
import com.uden.tau.ui.theme.ActiveRed
import com.uden.tau.ui.theme.LightRed
import com.uden.tau.ui.theme.Transparent
import java.time.Instant

@Preview
@Composable
fun WorkoutLogDisplay(
    modifier: Modifier = Modifier,
    log: WorkoutWithSetCount = WorkoutWithSetCount("", 0, 0),
    active: Boolean = true, onDeleteClick: () -> Unit = {}
) {
    val deleteStyle = MaterialTheme.typography.bodyLarge.copy(color = LightRed)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(6.dp)
            .then(modifier)
    ) {
        if (active) {
            Text(
                text = "Delete",
                style = deleteStyle,
                modifier = Modifier.padding(horizontal = 12.dp).clickable { onDeleteClick() }
            )
            Box(modifier = Modifier.background(Color.White).width(2.dp).height(36.dp))
            Box(modifier = Modifier.background(Color.Transparent).width(12.dp).height(36.dp))
        }
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
        ) {
            Text(
                text = log.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
            )
            Text(
                text = "${log.count} sets",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
        }
    }
}