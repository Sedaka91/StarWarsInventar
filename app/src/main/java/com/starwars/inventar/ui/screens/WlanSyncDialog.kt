package com.starwars.inventar.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.starwars.inventar.ui.InventoryViewModel
import com.starwars.inventar.ui.components.DatapadButton
import com.starwars.inventar.ui.components.PanelShape
import com.starwars.inventar.ui.components.SmallCutShape
import com.starwars.inventar.ui.theme.DatapadBg
import com.starwars.inventar.ui.theme.DatapadSurface
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.HoloCyan
import com.starwars.inventar.ui.theme.HoloGreen
import com.starwars.inventar.ui.theme.HoloRed
import com.starwars.inventar.ui.theme.TextDim
import com.starwars.inventar.ui.theme.TextPrimary

fun parseQrCodeUrl(rawText: String): Pair<String, Int> {
    var text = rawText.trim()
    if (text.startsWith("http://", ignoreCase = true)) {
        text = text.removePrefix("http://").removePrefix("HTTP://")
    } else if (text.startsWith("https://", ignoreCase = true)) {
        text = text.removePrefix("https://").removePrefix("HTTPS://")
    }

    val slashIndex = text.indexOf('/')
    if (slashIndex != -1) {
        text = text.substring(0, slashIndex)
    }

    val colonIndex = text.indexOf(':')
    return if (colonIndex != -1) {
        val h = text.substring(0, colonIndex)
        val p = text.substring(colonIndex + 1).toIntOrNull() ?: 8080
        Pair(h, p)
    } else {
        Pair(text, 8080)
    }
}

@Composable
fun QrScannerDialog(
    onQrScanned: (String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DatapadBg,
        shape = PanelShape,
        title = {
            Text(text = "QR-CODE SCANNEN", style = MaterialTheme.typography.titleMedium, color = HoloCyan)
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(SmallCutShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = androidx.camera.core.Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val barcodeScanner = BarcodeScanning.getClient(
                                BarcodeScannerOptions.Builder()
                                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                                    .build()
                            )

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                    barcodeScanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            for (barcode in barcodes) {
                                                val rawValue = barcode.rawValue
                                                if (!rawValue.isNullOrBlank()) {
                                                    val (host, port) = parseQrCodeUrl(rawValue)
                                                    onQrScanned(host, port)
                                                    break
                                                }
                                            }
                                        }
                                        .addOnFailureListener {
                                            // ignore
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } else {
                                    imageProxy.close()
                                }
                            }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = "QR-Code vor die Kamera halten...",
                    style = MaterialTheme.typography.bodySmall,
                    color = HoloCyan,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.6f), SmallCutShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        },
        confirmButton = {
            DatapadButton(
                text = "ABBRECHEN",
                onClick = onDismiss,
                accent = HoloCyan,
                filled = false
            )
        }
    )
}

@Composable
fun WlanSyncDialog(
    viewModel: InventoryViewModel,
    onDismiss: () -> Unit
) {
    val syncManager = viewModel.syncManager
    var host by remember { mutableStateOf(syncManager.savedHost) }
    var portStr by remember { mutableStateOf(syncManager.savedPort.toString()) }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf<Boolean?>(null) }
    var showCameraScanner by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showCameraScanner = true
        } else {
            statusMessage = "Kamerazugriff wurde verweigert."
            isSuccess = false
        }
    }

    if (showCameraScanner) {
        QrScannerDialog(
            onQrScanned = { scannedHost, scannedPort ->
                host = scannedHost
                portStr = scannedPort.toString()
                showCameraScanner = false
                statusMessage = "QR-Code erfolgreich gelesen: $scannedHost:$scannedPort"
                isSuccess = true
            },
            onDismiss = { showCameraScanner = false }
        )
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        containerColor = DatapadBg,
        modifier = Modifier
            .width(540.dp)
            .clip(PanelShape)
            .border(
                1.dp,
                Brush.linearGradient(listOf(HoloCyan, HoloCyan.copy(alpha = 0.3f))),
                PanelShape
            ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            when (isSuccess) {
                                true -> HoloGreen
                                false -> HoloRed
                                null -> HoloCyan
                            },
                            CircleShape
                        )
                )
                Text(
                    text = "📡 WLAN DATAPAD SYNCHRONISATION",
                    style = MaterialTheme.typography.titleMedium,
                    color = HoloCyan
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Verbinde dieses Datapad mit der zentralen PC-Anwendung (StarWarsSync) im selben WLAN-Netzwerk.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDim
                )

                // QR Scan Button
                DatapadButton(
                    text = "📷 QR-CODE SCANNEN",
                    onClick = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    accent = HoloAmber,
                    filled = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Host & Port Fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = host,
                        onValueChange = { host = it },
                        label = { Text("PC WLAN IP-Adresse", color = TextDim) },
                        modifier = Modifier.weight(2.5f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HoloCyan,
                            unfocusedBorderColor = HoloCyan.copy(alpha = 0.4f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = portStr,
                        onValueChange = { portStr = it },
                        label = { Text("Port", color = TextDim) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HoloCyan,
                            unfocusedBorderColor = HoloCyan.copy(alpha = 0.4f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                // Test Connection Button
                DatapadButton(
                    text = "🔍 TESTVERBINDUNG PRÜFEN",
                    onClick = {
                        val port = portStr.toIntOrNull() ?: 8080
                        isLoading = true
                        statusMessage = "Prüfe Verbindung zu $host:$port..."
                        viewModel.testSyncConnection(host.trim(), port) { res ->
                            isLoading = false
                            res.onSuccess { msg ->
                                statusMessage = msg
                                isSuccess = true
                            }.onFailure { err ->
                                statusMessage = "Fehler: ${err.message}"
                                isSuccess = false
                            }
                        }
                    },
                    accent = HoloCyan,
                    filled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                // Sync Actions
                Text(
                    text = "SYNCHRONISATIONS-MODUS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = HoloAmber
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DatapadButton(
                        text = "🔄 BIDIREKTIONAL",
                        onClick = {
                            val port = portStr.toIntOrNull() ?: 8080
                            isLoading = true
                            statusMessage = "Synchronisiere bidirektional mit PC..."
                            viewModel.syncBidirectional(host.trim(), port) { res ->
                                isLoading = false
                                res.onSuccess { msg ->
                                    statusMessage = msg
                                    isSuccess = true
                                }.onFailure { err ->
                                    statusMessage = "Fehler: ${err.message}"
                                    isSuccess = false
                                }
                            }
                        },
                        accent = HoloGreen,
                        filled = true,
                        modifier = Modifier.weight(1f)
                    )

                    DatapadButton(
                        text = "⬇️ VOM PC",
                        onClick = {
                            val port = portStr.toIntOrNull() ?: 8080
                            isLoading = true
                            statusMessage = "Lade Datenbestand vom PC..."
                            viewModel.pullFromServer(host.trim(), port) { res ->
                                isLoading = false
                                res.onSuccess { msg ->
                                    statusMessage = msg
                                    isSuccess = true
                                }.onFailure { err ->
                                    statusMessage = "Fehler: ${err.message}"
                                    isSuccess = false
                                }
                            }
                        },
                        accent = HoloCyan,
                        filled = false,
                        modifier = Modifier.weight(1f)
                    )

                    DatapadButton(
                        text = "⬆️ AN PC",
                        onClick = {
                            val port = portStr.toIntOrNull() ?: 8080
                            isLoading = true
                            statusMessage = "Sende Daten an den PC..."
                            viewModel.pushToServer(host.trim(), port) { res ->
                                isLoading = false
                                res.onSuccess { msg ->
                                    statusMessage = msg
                                    isSuccess = true
                                }.onFailure { err ->
                                    statusMessage = "Fehler: ${err.message}"
                                    isSuccess = false
                                }
                            }
                        },
                        accent = HoloAmber,
                        filled = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Progress Indicator
                if (isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = HoloCyan,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Übertrage Daten...",
                            style = MaterialTheme.typography.bodySmall,
                            color = HoloCyan
                        )
                    }
                }

                // Status Message Box
                statusMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(SmallCutShape)
                            .background(DatapadSurface)
                            .border(
                                1.dp,
                                when (isSuccess) {
                                    true -> HoloGreen
                                    false -> HoloRed
                                    null -> HoloAmber
                                },
                                SmallCutShape
                            )
                            .padding(10.dp)
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = when (isSuccess) {
                                true -> HoloGreen
                                false -> HoloRed
                                null -> HoloAmber
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            DatapadButton(
                text = "SCHLIESSEN",
                onClick = onDismiss,
                accent = HoloCyan,
                filled = false
            )
        }
    )
}
