package com.uden.tau.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.uden.tau.DateFormat
import com.uden.tau.R
import com.uden.tau.db.WeightLog
import com.uden.tau.formatInstant
import com.uden.tau.ui.theme.ActiveRed
import com.uden.tau.ui.theme.Transparent

@Composable
fun WeightLogDisplay(log: WeightLog, active: Boolean, modifier: Modifier = Modifier, onDeleteClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(
            if (active) ActiveRed else Transparent, RoundedCornerShape(8.dp)
        ).padding(6.dp).then(modifier)
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
        ) {
            Text(
                text = "${
                    formatInstant(
                        DateFormat.Date,
                        log.createdAt
                    )
                } Weight Log",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
            )
            Text(
                text = "%.2f".format(log.weight),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
            Text(
                text = formatInstant(DateFormat.TimeShort, log.createdAt),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
        }
        if (active) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Delete Entry",
                tint = colorResource(id = R.color.light_red),
                modifier = Modifier.rotate(45f).clickable {
                    onDeleteClick()
                }
            )
        }
    }
}