package com.starwars.inventar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.starwars.inventar.ui.formatDate
import com.starwars.inventar.ui.theme.DatapadBg
import com.starwars.inventar.ui.theme.DatapadTheme
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.HoloRed

@Composable
fun DatapadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = HoloAmber,
    filled: Boolean = true,
    enabled: Boolean = true
) {
    val shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)
    val strength = if (enabled) 1f else 0.35f
    Box(
        modifier = modifier
            .clip(shape)
            .background(DatapadBg)
            .background(if (filled) accent.copy(alpha = 0.22f * strength) else Color.Transparent)
            .border(1.dp, accent.copy(alpha = 0.9f * strength), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = accent.copy(alpha = strength)
        )
    }
}

@Composable
fun DatapadTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false,
    suffix: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label.uppercase(), color = Color.LightGray) },
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        suffix = if (suffix != null) {
            { Text(suffix, color = Color.LightGray) }
        } else null,
        trailingIcon = trailingIcon,
        shape = SmallCutShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HoloAmber,
            unfocusedBorderColor = Color(0xFF5A6478),
            focusedLabelColor = HoloAmber,
            unfocusedLabelColor = Color.LightGray,
            cursorColor = HoloAmber,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedSuffixColor = Color.LightGray,
            unfocusedSuffixColor = Color.LightGray
        )
    )
}

/** Dropdown-Menü (ExposedDropdownMenu) für Auswahlen wie Kategorie, Status, Vertragsart etc. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DatapadDropdownSelector(
    label: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        DatapadTextField(
            value = optionLabel(selected),
            onValueChange = {},
            label = label,
            readOnly = true,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF151820))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = optionLabel(option).uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (option == selected) HoloAmber else Color.White
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/** Auswahl aus einer festen Liste, dargestellt als Chips. T kann auch nullable sein ("Keiner"). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ChipSelector(
    label: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.LightGray
        )
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(option) },
                    label = {
                        Text(
                            text = optionLabel(option).uppercase(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        labelColor = Color.LightGray,
                        selectedContainerColor = HoloAmber.copy(alpha = 0.25f),
                        selectedLabelColor = HoloAmber
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color(0xFF5A6478),
                        selectedBorderColor = HoloAmber
                    )
                )
            }
        }
    }
}

/** Datumsfeld: Tippen öffnet den Kalender. Datum als UTC-Epoch-Millisekunden. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    millis: Long?,
    onChange: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var open by remember { mutableStateOf(false) }

    Box(modifier.fillMaxWidth()) {
        DatapadTextField(
            value = formatDate(millis),
            onValueChange = {},
            label = label,
            readOnly = true
        )
        // Transparente Fläche über dem Feld, damit der Tipp sicher ankommt
        Box(
            Modifier
                .matchParentSize()
                .clickable { open = true }
        )
    }

    if (open) {
        val state = rememberDatePickerState(initialSelectedDateMillis = millis)
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    onChange(state.selectedDateMillis)
                    open = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onChange(null)
                    open = false
                }) { Text("LEEREN") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.LightGray,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = HoloAmber,
                checkedTrackColor = HoloAmber.copy(alpha = 0.3f),
                checkedBorderColor = HoloAmber
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1017)
@Composable
private fun DatapadControlsPreview() {
    DatapadTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DatapadButton(text = "Speichern", onClick = {})
            DatapadButton(text = "Löschen", accent = HoloRed, filled = false, onClick = {})
            DatapadTextField(
                value = "DL-44 Blaster",
                onValueChange = {},
                label = "Bezeichnung"
            )
            DatapadDropdownSelector(
                label = "Kategorie",
                options = listOf("Waffen", "Rüstung", "Technik"),
                selected = "Waffen",
                onSelected = {},
                optionLabel = { it }
            )
            SwitchRow(
                label = "Unterzeichnet",
                checked = true,
                onCheckedChange = {}
            )
        }
    }
}
