package com.uden.tau.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.uden.tau.R
import com.uden.tau.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit = {},
    icon: @Composable () (() -> Unit) = {},
    initialValue: String = "",
) {
    var text by remember { mutableStateOf(initialValue) }

    TextField(
        value = text,
        onValueChange = { value ->
            text = value
            onChange(text)
        },
        keyboardOptions = KeyboardOptions(
        ),
        label = { Text("") },
        modifier = Modifier
            .then(modifier),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.Gray,
        ),
        trailingIcon = icon,
        textStyle = Typography.titleLarge,
    )
}
