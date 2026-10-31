package com.starwars.inventar.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.inventar.data.Article
import com.starwars.inventar.data.ArticleCategory
import com.starwars.inventar.data.Contract
import com.starwars.inventar.data.ContractStatus
import com.starwars.inventar.data.ContractType
import com.starwars.inventar.ui.InventoryViewModel
import com.starwars.inventar.ui.components.DataRow
import com.starwars.inventar.ui.components.DatapadScreen
import com.starwars.inventar.ui.components.HoloPanel
import com.starwars.inventar.ui.formatCredits
import com.starwars.inventar.ui.theme.DatapadTheme
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.HoloCyan
import com.starwars.inventar.ui.theme.TextDim

@Composable
fun HomeScreen(
    viewModel: InventoryViewModel,
    onOpenArticles: () -> Unit,
    onOpenContracts: () -> Unit
) {
    val articles by viewModel.articles.collectAsStateWithLifecycle()
    val contracts by viewModel.contracts.collectAsStateWithLifecycle()

    HomeScreenContent(
        articles = articles,
        contracts = contracts,
        onOpenArticles = onOpenArticles,
        onOpenContracts = onOpenContracts
    )
}

@Composable
fun HomeScreenContent(
    articles: List<Article>,
    contracts: List<Contract>,
    onOpenArticles: () -> Unit,
    onOpenContracts: () -> Unit
) {
    val pieces = articles.sumOf { it.quantity }
    val stockValue = articles.sumOf { it.price * it.quantity }
    val stockCosts = articles.sumOf { it.costs * it.quantity }
    val activeContracts = contracts.count { it.status == ContractStatus.LAUFEND }

    DatapadScreen(title = "Datapad", subtitle = "Inventar der Produktion") { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HoloPanel(title = "Statusbericht") {
                DataRow("Artikel (Positionen)", articles.size.toString())
                DataRow("Stückzahl gesamt", pieces.toString())
                DataRow("Warenwert (Preise)", formatCredits(stockValue))
                DataRow("Warenkosten", formatCredits(stockCosts))
                DataRow("Aktive Verträge", activeContracts.toString())
            }

            MenuTile(
                title = "Artikel",
                description = "Waffen, Rüstung, Droiden, Vorräte",
                count = articles.size.toString(),
                accent = HoloCyan,
                onClick = onOpenArticles
            )
            MenuTile(
                title = "Verträge",
                description = "Handel, Kauf, Beschaffung, Kopfgeld",
                count = contracts.size.toString(),
                accent = HoloAmber,
                onClick = onOpenContracts
            )
        }
    }
}

@Composable
private fun MenuTile(
    title: String,
    description: String,
    count: String,
    accent: Color,
    onClick: () -> Unit
) {
    HoloPanel(accent = accent, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = accent
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim
                )
            }
            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium,
                color = accent
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val sampleArticles = listOf(
        Article(id = "SW-HW-TEST_1", name = "Lichtschwert", category = ArticleCategory.WEAPON, quantity = 2, price = 1500000.0, costs = 800000.0),
        Article(id = "SW-HW-TEST_2", name = "Mandalorianische Rüstung", category = ArticleCategory.ARMOR, quantity = 5, price = 200000.0, costs = 100000.0)
    )
    val sampleContracts = listOf(
        Contract(id = "SW-HW-TEST_C1", title = "Handelsvertrag Waffenteile", status = ContractStatus.LAUFEND, type = ContractType.HANDEL)
    )
    DatapadTheme {
        HomeScreenContent(
            articles = sampleArticles,
            contracts = sampleContracts,
            onOpenArticles = {},
            onOpenContracts = {}
        )
    }
}
