package com.starwars.inventar.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.inventar.data.Article
import com.starwars.inventar.data.Contract
import com.starwars.inventar.data.ContractStatus
import com.starwars.inventar.data.ContractType
import com.starwars.inventar.ui.InventoryViewModel
import com.starwars.inventar.ui.components.DateField
import com.starwars.inventar.ui.components.DatapadButton
import com.starwars.inventar.ui.components.DatapadDropdownSelector
import com.starwars.inventar.ui.components.DatapadScreen
import com.starwars.inventar.ui.components.DatapadTextField
import com.starwars.inventar.ui.components.HoloPanel
import com.starwars.inventar.ui.components.SwitchRow
import com.starwars.inventar.ui.decimalToText
import com.starwars.inventar.ui.parseDecimal
import com.starwars.inventar.ui.theme.DatapadTheme
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.HoloRed
import com.starwars.inventar.ui.theme.TextDim

@Composable
fun ContractEditScreen(
    viewModel: InventoryViewModel,
    contractId: String,
    onClose: () -> Unit
) {
    val articles by viewModel.articles.collectAsStateWithLifecycle()
    val isNew = contractId.isBlank()
    var loadedContract by remember { mutableStateOf<Contract?>(null) }

    LaunchedEffect(contractId) {
        if (!isNew) {
            loadedContract = viewModel.getContract(contractId)
        }
    }

    val linkedArticles = remember(articles, contractId) {
        if (isNew) emptyList() else articles.filter { it.contractId == contractId }
    }

    if (isNew || loadedContract != null) {
        ContractEditContent(
            initialContract = if (isNew) Contract() else loadedContract!!,
            articles = linkedArticles,
            isNew = isNew,
            onSave = { updatedContract ->
                viewModel.saveContract(updatedContract, onClose)
            },
            onDelete = if (!isNew) {
                { viewModel.deleteContract(contractId, onClose) }
            } else null,
            onClose = onClose
        )
    }
}

@Composable
fun ContractEditContent(
    initialContract: Contract = Contract(),
    articles: List<Article> = emptyList(),
    isNew: Boolean = true,
    onSave: (Contract) -> Unit = {},
    onDelete: (() -> Unit)? = null,
    onClose: () -> Unit = {}
) {
    var draft by remember(initialContract) { mutableStateOf(initialContract) }
    var costsText by remember(initialContract) { mutableStateOf(decimalToText(initialContract.costs)) }
    var priceText by remember(initialContract) { mutableStateOf(decimalToText(initialContract.price)) }
    var depositText by remember(initialContract) { mutableStateOf(decimalToText(initialContract.deposit)) }
    var confirmDelete by remember { mutableStateOf(false) }

    DatapadScreen(
        title = if (isNew) "Neuer Vertrag" else "Vertrag bearbeiten",
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
            HoloPanel(title = "Vertragsdaten", accent = HoloAmber) {
                DatapadTextField(
                    value = draft.contractNumber,
                    onValueChange = { draft = draft.copy(contractNumber = it) },
                    label = "Vertragsnummer"
                )
                DatapadTextField(
                    value = draft.title,
                    onValueChange = { draft = draft.copy(title = it) },
                    label = "Titel"
                )
                DatapadDropdownSelector(
                    label = "Vertragsart",
                    options = ContractType.entries.toList(),
                    selected = draft.type,
                    onSelected = { draft = draft.copy(type = it) },
                    optionLabel = { it.label }
                )
                DatapadDropdownSelector(
                    label = "Status",
                    options = ContractStatus.entries.toList(),
                    selected = draft.status,
                    onSelected = { draft = draft.copy(status = it) },
                    optionLabel = { it.label }
                )
                SwitchRow(
                    label = "Unterzeichnet",
                    checked = draft.signed,
                    onCheckedChange = { draft = draft.copy(signed = it) }
                )
            }

            HoloPanel(title = "Parteien") {
                DatapadTextField(
                    value = draft.owner,
                    onValueChange = { draft = draft.copy(owner = it) },
                    label = "Besitzer / Auftraggeber"
                )
                DatapadTextField(
                    value = draft.partner,
                    onValueChange = { draft = draft.copy(partner = it) },
                    label = "Vertragspartner"
                )
            }

            HoloPanel(title = "Laufzeit") {
                DateField(
                    label = "Beginn",
                    millis = draft.startDate,
                    onChange = { draft = draft.copy(startDate = it) }
                )
                DateField(
                    label = "Ende",
                    millis = draft.endDate,
                    onChange = { draft = draft.copy(endDate = it) }
                )
            }

            HoloPanel(title = "Finanzen", accent = HoloAmber) {
                DatapadTextField(
                    value = costsText,
                    onValueChange = { costsText = it },
                    label = "Kosten",
                    keyboardType = KeyboardType.Decimal,
                    suffix = "cr"
                )
                DatapadTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = "Preis",
                    keyboardType = KeyboardType.Decimal,
                    suffix = "cr"
                )
                DatapadTextField(
                    value = depositText,
                    onValueChange = { depositText = it },
                    label = "Kaution",
                    keyboardType = KeyboardType.Decimal,
                    suffix = "cr"
                )
            }

            HoloPanel(title = "Bedingungen & Notizen") {
                DatapadTextField(
                    value = draft.terms,
                    onValueChange = { draft = draft.copy(terms = it) },
                    label = "Vertragsbedingungen",
                    singleLine = false,
                    minLines = 3
                )
                DatapadTextField(
                    value = draft.notes,
                    onValueChange = { draft = draft.copy(notes = it) },
                    label = "Notizen",
                    singleLine = false,
                    minLines = 2
                )
            }

            if (!isNew) {
                HoloPanel(title = "Zugeordnete Artikel") {
                    if (articles.isEmpty()) {
                        Text(
                            text = "Keine Artikel zugeordnet. Die Zuordnung erfolgt im Artikel.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDim
                        )
                    } else {
                        articles.forEach { article ->
                            Text(
                                text = "• ${article.name} (${article.quantity}x)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DatapadButton(
                    text = "Speichern",
                    enabled = draft.title.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSave(
                            draft.copy(
                                costs = parseDecimal(costsText),
                                price = parseDecimal(priceText),
                                deposit = parseDecimal(depositText)
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
            title = { Text("Vertrag löschen?") },
            text = {
                Text("„${draft.title}“ wird dauerhaft entfernt. Zugeordnete Artikel bleiben erhalten und verlieren nur die Zuordnung.")
            },
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

@Preview(showBackground = true)
@Composable
private fun ContractEditScreenNewPreview() {
    DatapadTheme {
        ContractEditContent(
            initialContract = Contract(),
            articles = emptyList(),
            isNew = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContractEditScreenEditPreview() {
    val sampleContract = Contract(
        id = "SW-HW-TEST_1",
        contractNumber = "VT-883",
        title = "Handelsvertrag Lichtschwert-Teile",
        type = ContractType.HANDEL,
        status = ContractStatus.LAUFEND,
        owner = "Jedi-Tempel Archiv",
        partner = "Produktion",
        signed = true,
        costs = 5000.0,
        price = 10000.0,
        deposit = 2000.0
    )
    val sampleArticles = listOf(
        Article(id = "a1", name = "Anakin's Lichtschwert", quantity = 1),
        Article(id = "a2", name = "Obi-Wan's Lichtschwert", quantity = 1)
    )
    DatapadTheme {
        ContractEditContent(
            initialContract = sampleContract,
            articles = sampleArticles,
            isNew = false
        )
    }
}
