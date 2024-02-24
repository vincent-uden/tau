package com.uden.tau.main

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NumberInput(
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit = {},
    value: String = "",
) {
    var number by remember { mutableStateOf(value) }

    OutlinedTextField(
        value = number,
        onValueChange = { value ->
            number = value.filter { it.isDigit() || it == '.' || it == ',' }
            onChange(number)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        label = { Text("") },
        modifier = modifier,
    )
}
