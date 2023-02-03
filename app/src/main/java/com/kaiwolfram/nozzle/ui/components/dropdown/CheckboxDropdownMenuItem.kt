package com.kaiwolfram.nozzle.ui.components.dropdown

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun CheckboxDropdownMenuItem(
    isChecked: Boolean,
    text: String,
    onToggle: () -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    DropdownMenuItem(
        onClick = onToggle,
        contentPadding = contentPadding
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onToggle() })
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
