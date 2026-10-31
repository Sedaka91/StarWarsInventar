package com.starwars.inventar.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.inventar.data.Article
import com.starwars.inventar.data.ArticleCategory
import com.starwars.inventar.data.ArticleStatus
import com.starwars.inventar.data.Contract
import com.starwars.inventar.ui.InventoryViewModel
import com.starwars.inventar.ui.components.ChipSelector
import com.starwars.inventar.ui.components.DatapadButton
import com.starwars.inventar.ui.components.DatapadDropdownSelector
import com.starwars.inventar.ui.components.DatapadScreen
import com.starwars.inventar.ui.components.DatapadTextField
import com.starwars.inventar.ui.components.HoloPanel
import com.starwars.inventar.ui.components.SmallCutShape
import com.starwars.inventar.ui.decimalToText
import com.starwars.inventar.ui.parseDecimal
import com.starwars.inventar.ui.theme.DatapadTheme
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.HoloRed
import java.io.File

@Composable
fun ArticleEditScreen(
    viewModel: InventoryViewModel,
    articleId: String,
    onClose: () -> Unit
) {
    val contracts by viewModel.contracts.collectAsStateWithLifecycle()
    val isNew = articleId.isBlank()
    var loadedArticle by remember { mutableStateOf<Article?>(null) }

    LaunchedEffect(articleId) {
        if (!isNew) {
            loadedArticle = viewModel.getArticle(articleId)
        }
    }

    if (isNew || loadedArticle != null) {
        ArticleEditContent(
            initialArticle = if (isNew) Article() else loadedArticle!!,
            contracts = contracts,
            isNew = isNew,
            onSave = { updatedArticle ->
                viewModel.saveArticle(updatedArticle, onClose)
            },
            onDelete = if (!isNew) {
                { viewModel.deleteArticle(articleId, onClose) }
            } else null,
            onClose = onClose
        )
    }
}

private fun saveArticleImageFromUri(context: Context, uri: Uri): String? {
    return runCatching {
        val parent = runCatching { context.getExternalFilesDir(null) }.getOrNull() ?: context.filesDir
        val inventoryDir = File(parent, "inventory").let { File(it, "article_images") }
        if (!inventoryDir.exists()) {
            inventoryDir.mkdirs()
        }
        val destFile = File(inventoryDir, "article_${System.currentTimeMillis()}.png")

        context.contentResolver.openInputStream(uri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        destFile.absolutePath
    }.getOrNull()
}

@Composable
fun ArticleEditContent(
    initialArticle: Article = remember { Article() },
    contracts: List<Contract> = emptyList(),
    isNew: Boolean = true,
    onSave: (Article) -> Unit = {},
    onDelete: (() -> Unit)? = null,
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    var draft by remember { mutableStateOf(initialArticle) }
    var costsText by remember { mutableStateOf(decimalToText(initialArticle.costs)) }
    var priceText by remember { mutableStateOf(decimalToText(initialArticle.price)) }
    var quantityText by remember { mutableStateOf(draft.quantity.toString()) }
    var confirmDelete by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val savedPath = saveArticleImageFromUri(context, selectedUri)
            if (savedPath != null) {
                draft = draft.copy(imagePath = savedPath)
            }
        }
    }

    val contractOptions = remember(contracts) { listOf<String?>(null) + contracts.map { it.id } }

    DatapadScreen(
        title = if (isNew) "Neuer Artikel" else "Artikel bearbeiten",
        subtitle = "Datensatz",
        onBack = onClose
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HoloPanel(title = "Stammdaten") {
                // Artikelbild Bereich: Klick = Bild laden, Langer Klick = Bild entfernen (kein extra Button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(SmallCutShape)
                            .background(HoloAmber.copy(alpha = 0.15f))
                            .border(1.dp, HoloAmber, SmallCutShape)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { imagePickerLauncher.launch("image/*") },
                                    onLongPress = { draft = draft.copy(imagePath = null) }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        ArticleImageView(
                            imagePath = draft.imagePath,
                            fallbackCategory = draft.category,
                            modifier = Modifier.size(72.dp),
                            tint = HoloAmber
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "ARTIKELBILD",
                            style = MaterialTheme.typography.labelMedium,
                            color = HoloAmber
                        )
                        Text(
                            text = "Tippen zum Laden · Länger gedrückt halten zum Löschen",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }

                DatapadTextField(
                    value = draft.name,
                    onValueChange = { draft = draft.copy(name = it) },
                    label = "Bezeichnung"
                )
                DatapadDropdownSelector(
                    label = "Kategorie",
                    options = ArticleCategory.entries.toList(),
                    selected = draft.category,
                    onSelected = { draft = draft.copy(category = it) },
                    optionLabel = { it.label }
                )
                DatapadDropdownSelector(
                    label = "Status",
                    options = ArticleStatus.entries.toList(),
                    selected = draft.status,
                    onSelected = { draft = draft.copy(status = it) },
                    optionLabel = { it.label }
                )
                DatapadTextField(
                    value = draft.description,
                    onValueChange = { draft = draft.copy(description = it) },
                    label = "Beschreibung",
                    singleLine = false,
                    minLines = 3
                )
            }

            HoloPanel(title = "Beteiligte & Einsatz") {
                DatapadTextField(
                    value = draft.owner,
                    onValueChange = { draft = draft.copy(owner = it) },
                    label = "Besitzer"
                )
                DatapadTextField(
                    value = draft.partner,
                    onValueChange = { draft = draft.copy(partner = it) },
                    label = "Partner (Leihgeber, Lieferant)"
                )
                DatapadTextField(
                    value = draft.location,
                    onValueChange = { draft = draft.copy(location = it) },
                    label = "Lagerort"
                )
                DatapadTextField(
                    value = draft.scene,
                    onValueChange = { draft = draft.copy(scene = it) },
                    label = "Szene / Akt"
                )
            }

            HoloPanel(title = "Finanzen", accent = HoloAmber) {
                DatapadTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it.filter(Char::isDigit) },
                    label = "Stückzahl",
                    keyboardType = KeyboardType.Number
                )
                DatapadTextField(
                    value = costsText,
                    onValueChange = { costsText = it },
                    label = "Kosten pro Stück",
                    keyboardType = KeyboardType.Decimal,
                    suffix = "cr"
                )
                DatapadTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = "Preis pro Stück",
                    keyboardType = KeyboardType.Decimal,
                    suffix = "cr"
                )
            }

            HoloPanel(title = "Vertrag") {
                ChipSelector(
                    label = "Zugeordneter Vertrag",
                    options = contractOptions,
                    selected = draft.contractId,
                    onSelected = { draft = draft.copy(contractId = it) },
                    optionLabel = { id -> contractLabel(contracts, id) }
                )
            }

            HoloPanel(title = "Notizen") {
                DatapadTextField(
                    value = draft.notes,
                    onValueChange = { draft = draft.copy(notes = it) },
                    label = "Notizen",
                    singleLine = false,
                    minLines = 3
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DatapadButton(
                    text = "Speichern",
                    enabled = draft.name.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSave(
                            draft.copy(
                                costs = parseDecimal(costsText),
                                price = parseDecimal(priceText),
                                quantity = (quantityText.toIntOrNull() ?: 1).coerceAtLeast(1)
                            )
                        )
                    }
                )
                if (!isNew && onDelete != null) {
                    DatapadButton(
                        text = "Löschen",
                        accent = HoloRed,
                        filled = false,
                        modifier = Modifier.weight(1f),
                        onClick = { confirmDelete = true }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (confirmDelete && onDelete != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Artikel löschen?") },
            text = { Text("„${draft.name}“ wird dauerhaft entfernt.") },
            confirmButton = {
                TextButton(onClick = { onDelete() }) {
                    Text("LÖSCHEN", color = HoloRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("ABBRECHEN") }
            }
        )
    }
}

private fun contractLabel(contracts: List<Contract>, id: String?): String {
    if (id == null) return "Keiner"
    val contract = contracts.firstOrNull { it.id == id } ?: return "Vertrag #$id"
    return contract.title.ifBlank { "Vertrag #$id" }
}

@Preview(showBackground = true)
@Composable
private fun ArticleEditScreenNewPreview() {
    DatapadTheme {
        ArticleEditContent(
            initialArticle = Article(),
            contracts = emptyList(),
            isNew = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleEditScreenEditPreview() {
    val sampleArticle = Article(
        id = "SW-HW-TEST_1",
        name = "A280 Blastergewehr",
        category = ArticleCategory.WEAPON,
        status = ArticleStatus.IM_EINSATZ,
        description = "Standard-Blastergewehr der Rebellenallianz",
        owner = "Rebellenallianz",
        quantity = 5,
        location = "Waffenkammer Echo-Basis",
        costs = 450.0,
        price = 900.0
    )
    DatapadTheme {
        ArticleEditContent(
            initialArticle = sampleArticle,
            contracts = emptyList(),
            isNew = false
        )
    }
}
