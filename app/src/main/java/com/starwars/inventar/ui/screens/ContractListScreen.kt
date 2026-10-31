package com.starwars.inventar.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.inventar.data.Contract
import com.starwars.inventar.data.ContractStatus
import com.starwars.inventar.data.ContractType
import com.starwars.inventar.ui.InventoryViewModel
import com.starwars.inventar.ui.components.DataRow
import com.starwars.inventar.ui.components.DatapadButton
import com.starwars.inventar.ui.components.DatapadDropdownSelector
import com.starwars.inventar.ui.components.DatapadScreen
import com.starwars.inventar.ui.components.DatapadTextField
import com.starwars.inventar.ui.components.EmptyState
import com.starwars.inventar.ui.components.HoloPanel
import com.starwars.inventar.ui.components.StatusBadge
import com.starwars.inventar.ui.components.contractStatusColor
import com.starwars.inventar.ui.formatCredits
import com.starwars.inventar.ui.formatDate
import com.starwars.inventar.ui.theme.DatapadTheme
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.TextDim
import com.starwars.inventar.ui.theme.TextPrimary

enum class ContractSortOption(val label: String) {
    TITLE_ASC("Titel (A–Z)"),
    TITLE_DESC("Titel (Z–A)"),
    PRICE_DESC("Preis: Höchste zuerst"),
    PRICE_ASC("Preis: Niedrigste zuerst"),
    NEWEST("Neueste zuerst")
}

@Composable
fun ContractListScreen(
    viewModel: InventoryViewModel,
    onBack: () -> Unit,
    onOpen: (String) -> Unit
) {
    val contracts by viewModel.contracts.collectAsStateWithLifecycle()
    ContractListContent(
        contracts = contracts,
        onBack = onBack,
        onOpen = onOpen
    )
}

@Composable
fun ContractListContent(
    contracts: List<Contract>,
    onBack: () -> Unit,
    onOpen: (String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf<ContractType?>(null) }
    var selectedStatus by rememberSaveable { mutableStateOf<ContractStatus?>(null) }
    var sortOption by rememberSaveable { mutableStateOf(ContractSortOption.TITLE_ASC) }
    var showFilters by rememberSaveable { mutableStateOf(false) }

    val filtered = remember(contracts, query, selectedType, selectedStatus, sortOption) {
        contracts.filter { contract ->
            val matchesQuery = query.isBlank() || listOf(
                contract.title,
                contract.contractNumber,
                contract.type.label,
                contract.owner,
                contract.partner
            ).any { it.contains(query, ignoreCase = true) }

            val matchesType = selectedType == null || contract.type == selectedType
            val matchesStatus = selectedStatus == null || contract.status == selectedStatus

            matchesQuery && matchesType && matchesStatus
        }.let { list ->
            when (sortOption) {
                ContractSortOption.TITLE_ASC -> list.sortedBy { it.title.lowercase() }
                ContractSortOption.TITLE_DESC -> list.sortedByDescending { it.title.lowercase() }
                ContractSortOption.PRICE_DESC -> list.sortedByDescending { it.price }
                ContractSortOption.PRICE_ASC -> list.sortedBy { it.price }
                ContractSortOption.NEWEST -> list.sortedByDescending { it.id }
            }
        }
    }

    DatapadScreen(
        title = "Verträge",
        subtitle = "${filtered.size} von ${contracts.size} Einträgen",
        onBack = onBack,
        floatingActionButton = {
            DatapadButton(text = "+ Neu", onClick = { onOpen("") }, accent = HoloAmber)
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    DatapadTextField(value = query, onValueChange = { query = it }, label = "Vertrags-Suche")
                }
                DatapadButton(
                    text = if (showFilters) "Filter aus" else "Filter & Sort",
                    onClick = { showFilters = !showFilters },
                    accent = HoloAmber,
                    filled = showFilters
                )
            }

            if (showFilters) {
                HoloPanel(accent = HoloAmber, title = "Vertrags-Filter & Sortierung") {
                    val typeOptions = listOf<ContractType?>(null) + ContractType.entries
                    DatapadDropdownSelector(
                        label = "Vertragsart-Filter",
                        options = typeOptions,
                        selected = selectedType,
                        onSelected = { selectedType = it },
                        optionLabel = { it?.label ?: "Alle Vertragsarten" }
                    )

                    val statusOptions = listOf<ContractStatus?>(null) + ContractStatus.entries
                    DatapadDropdownSelector(
                        label = "Status-Filter",
                        options = statusOptions,
                        selected = selectedStatus,
                        onSelected = { selectedStatus = it },
                        optionLabel = { it?.label ?: "Alle Status" }
                    )

                    DatapadDropdownSelector(
                        label = "Sortierung",
                        options = ContractSortOption.entries.toList(),
                        selected = sortOption,
                        onSelected = { sortOption = it },
                        optionLabel = { it.label }
                    )
                }
            }

            if (filtered.isEmpty()) {
                EmptyState(if (contracts.isEmpty()) "Noch keine Verträge im Katalog erfasst" else "Keine Treffer im Vertragskatalog")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(filtered, key = { it.id }) { contract ->
                        ContractCard(contract = contract, onClick = { onOpen(contract.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ContractCard(contract: Contract, onClick: () -> Unit) {
    val heading = listOf(contract.contractNumber, contract.title)
        .filter { it.isNotBlank() }
        .joinToString(" · ")
        .ifBlank { "(ohne Titel)" }

    HoloPanel(accent = HoloAmber, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = heading.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            StatusBadge(contract.status.label, contractStatusColor(contract.status))
        }
        Text(
            text = "${contract.type.label.uppercase()}${if (contract.signed) " · UNTERZEICHNET" else ""}",
            style = MaterialTheme.typography.labelMedium,
            color = HoloAmber
        )
        DataRow("Auftraggeber", contract.owner.ifBlank { "–" })
        DataRow("Vertragspartner", contract.partner.ifBlank { "–" })
        if (contract.startDate != null || contract.endDate != null) {
            DataRow(
                "Laufzeit",
                "${formatDate(contract.startDate).ifBlank { "?" }} – ${formatDate(contract.endDate).ifBlank { "offen" }}"
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WERT: ${formatCredits(contract.price)}",
                style = MaterialTheme.typography.labelLarge,
                color = HoloAmber
            )
            Text(
                text = "KOSTEN: ${formatCredits(contract.costs)}",
                style = MaterialTheme.typography.labelSmall,
                color = TextDim
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContractListScreenPreview() {
    val sampleContracts = listOf(
        Contract(
            id = "SW-HW-TEST_C1",
            contractNumber = "K-2024-001",
            title = "Handelsvertrag Antriebsteile",
            type = ContractType.HANDEL,
            status = ContractStatus.LAUFEND,
            owner = "Karthon Logistik",
            partner = "Kuat Audio Systems",
            signed = true,
            costs = 12000.0,
            price = 18000.0
        ),
        Contract(
            id = "SW-HW-TEST_C2",
            contractNumber = "K-2024-002",
            title = "Beschaffung Raumschiff-Panzerung",
            type = ContractType.BESCHAFFUNG,
            status = ContractStatus.ENTWURF,
            owner = "Produktion",
            partner = "Incom Corp",
            signed = false,
            costs = 0.0,
            price = 50000.0
        )
    )
    DatapadTheme {
        ContractListContent(
            contracts = sampleContracts,
            onBack = {},
            onOpen = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContractListScreenEmptyPreview() {
    DatapadTheme {
        ContractListContent(
            contracts = emptyList(),
            onBack = {},
            onOpen = {}
        )
    }
}
