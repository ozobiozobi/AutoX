package com.aiselp.autox.ui.material3.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.reflect.KMutableProperty0

@Composable
fun CheckboxOption(
    modifier: Modifier = Modifier,
    value: KMutableProperty0<Boolean>,
    name: String
) {
    CheckboxOption(
        modifier = modifier,
        checked = value.get(),
        onCheckedChange = { value.set(it) },
        name = name
    )
}

@Composable
fun CheckboxOption(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {},
    name: String
) {
    Row(
        modifier = modifier.clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(text = name)
    }
}

@Composable
fun CheckboxOption(value: KMutableProperty0<Boolean>, name: String) {
    CheckboxOption(modifier = Modifier.fillMaxWidth(), value = value, name = name)
}