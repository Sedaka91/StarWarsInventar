package com.starwars.inventar.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.starwars.inventar.data.UserConfig
import com.starwars.inventar.ui.components.DatapadButton
import com.starwars.inventar.ui.components.PanelShape
import com.starwars.inventar.ui.components.SmallCutShape
import com.starwars.inventar.ui.theme.DatapadBg
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.HoloCyan
import com.starwars.inventar.ui.theme.HoloGreen
import com.starwars.inventar.ui.theme.HoloRed
import com.starwars.inventar.ui.theme.TextDim
import java.io.File

data class LogoPresetOption(
    val id: String,
    val title: String,
    val icon: ImageVector
)

val logoPresets = listOf(
    LogoPresetOption("CARGO", "Logistik & Fracht", Icons.Default.LocalShipping),
    LogoPresetOption("KUAT", "Kuat Shipyards", Icons.Default.RocketLaunch),
    LogoPresetOption("CZERKA", "Czerka Corp", Icons.Default.Business),
    LogoPresetOption("CORELLIA", "Corellia Freight", Icons.Default.Store),
    LogoPresetOption("ALLIANCE", "Allianz Rebellen", Icons.Default.Shield),
    LogoPresetOption("IMPERIUM", "Imperium Logistik", Icons.Default.Person)
)

fun getLogoIcon(preset: String): ImageVector {
    return logoPresets.find { it.id == preset }?.icon ?: Icons.Default.LocalShipping
}

@Composable
fun CompanyLogoView(
    userConfig: UserConfig,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = HoloCyan
) {
    val customPath = userConfig.customLogoPath
    val bitmap = remember(customPath) {
        if (!customPath.isNullOrBlank()) {
            runCatching {
                val file = File(customPath)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                } else null
            }.getOrNull()
        } else null
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Firmenlogo",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        val icon = getLogoIcon(userConfig.logoPreset)
        Icon(
            imageVector = icon,
            contentDescription = "Firmenlogo",
            tint = tint,
            modifier = modifier
        )
    }
}

private fun saveCustomLogoFromUri(context: Context, uri: Uri): String? {
    return runCatching {
        val parent = runCatching { context.getExternalFilesDir(null) }.getOrNull() ?: context.filesDir
        val inventoryDir = File(parent, "inventory")
        if (!inventoryDir.exists()) inventoryDir.mkdirs()
        val destFile = File(inventoryDir, "company_logo_${System.currentTimeMillis()}.png")

        context.contentResolver.openInputStream(uri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        destFile.absolutePath
    }.getOrNull()
}

@Composable
fun SettingsDialog(
    currentConfig: UserConfig,
    onSave: (UserConfig) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var companyName by remember { mutableStateOf(currentConfig.companyName) }
    var userName by remember { mutableStateOf(currentConfig.userName) }
    var selectedPreset by remember { mutableStateOf(currentConfig.logoPreset) }
    var customLogoPath by remember { mutableStateOf(currentConfig.customLogoPath) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val savedPath = saveCustomLogoFromUri(context, selectedUri)
            if (savedPath != null) {
                customLogoPath = savedPath
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DatapadBg,
        shape = PanelShape,
        title = {
            Text(
                text = "EINSTELLUNGEN — FIRMA & BENUTZER",
                style = MaterialTheme.typography.titleMedium,
                color = HoloCyan
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Firmenname
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Firmenname", color = HoloCyan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HoloCyan,
                        unfocusedBorderColor = HoloCyan.copy(alpha = 0.5f),
                        focusedLabelColor = HoloCyan,
                        unfocusedLabelColor = TextDim,
                        cursorColor = HoloCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Benutzername
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Benutzername", color = HoloAmber) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HoloAmber,
                        unfocusedBorderColor = HoloAmber.copy(alpha = 0.5f),
                        focusedLabelColor = HoloAmber,
                        unfocusedLabelColor = TextDim,
                        cursorColor = HoloAmber
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Hardware-Geräte-ID (Nur lesen)
                OutlinedTextField(
                    value = currentConfig.deviceId.ifBlank { "Wird ermittelt..." },
                    onValueChange = {},
                    label = { Text("Hardware-Geräte-ID", color = TextDim) },
                    readOnly = true,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextDim,
                        unfocusedBorderColor = TextDim.copy(alpha = 0.5f),
                        focusedLabelColor = TextDim,
                        unfocusedLabelColor = TextDim,
                        cursorColor = HoloCyan,
                        focusedTextColor = TextDim,
                        unfocusedTextColor = TextDim
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Logo Bereich: Import & Vorlagen
                Text(
                    text = "FIRMENLOGO (BILD IMPORTIEREN ODER VORLAGE)",
                    style = MaterialTheme.typography.labelMedium,
                    color = HoloCyan
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Vorschau aktuelles Logo
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(SmallCutShape)
                            .background(HoloCyan.copy(alpha = 0.15f))
                            .border(1.dp, HoloCyan, SmallCutShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CompanyLogoView(
                            userConfig = UserConfig(logoPreset = selectedPreset, customLogoPath = customLogoPath),
                            modifier = Modifier.size(32.dp),
                            tint = HoloCyan
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        DatapadButton(
                            text = "📁 BILD IMPORTIEREN...",
                            onClick = { imagePickerLauncher.launch("image/*") },
                            accent = HoloCyan,
                            filled = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (!customLogoPath.isNullOrBlank()) {
                            DatapadButton(
                                text = "❌ EIGENES BILD ENTFERNEN",
                                onClick = { customLogoPath = null },
                                accent = HoloRed,
                                filled = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Logo Preset Auswahl (wenn kein eigenes Bild aktiv oder als Alternativen)
                Text(
                    text = "ODER PLATZHALTER-VORLAGE WÄHLEN:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDim
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    logoPresets.chunked(2).forEach { rowPresets ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowPresets.forEach { preset ->
                                val isSelected = preset.id == selectedPreset && customLogoPath.isNullOrBlank()
                                val borderColor = if (isSelected) HoloGreen else HoloCyan.copy(alpha = 0.4f)
                                val bgBrush = if (isSelected) {
                                    Brush.linearGradient(listOf(HoloGreen.copy(alpha = 0.2f), HoloGreen.copy(alpha = 0.05f)))
                                } else {
                                    Brush.linearGradient(listOf(HoloCyan.copy(alpha = 0.08f), HoloCyan.copy(alpha = 0.02f)))
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(SmallCutShape)
                                        .background(bgBrush)
                                        .border(1.dp, borderColor, SmallCutShape)
                                        .clickable {
                                            selectedPreset = preset.id
                                            customLogoPath = null
                                        }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = preset.icon,
                                            contentDescription = preset.title,
                                            tint = if (isSelected) HoloGreen else HoloCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = preset.title,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) HoloGreen else HoloCyan
                                        )
                                    }
                                }
                            }
                            if (rowPresets.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            DatapadButton(
                text = "SPEICHERN",
                onClick = {
                    onSave(
                        currentConfig.copy(
                            companyName = companyName.ifBlank { "Logistik-Firma" },
                            userName = userName.ifBlank { "Spieler" },
                            logoPreset = selectedPreset,
                            customLogoPath = customLogoPath
                        )
                    )
                },
                accent = HoloGreen,
                filled = true
            )
        },
        dismissButton = {
            DatapadButton(
                text = "ABBRECHEN",
                onClick = onDismiss,
                accent = HoloCyan,
                filled = false
            )
        }
    )
}
