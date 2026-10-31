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
import com.starwars.inventar.data.Article
import com.starwars.inventar.data.ArticleCategory
import com.starwars.inventar.data.ArticleStatus
import com.starwars.inventar.ui.InventoryViewModel
import com.starwars.inventar.ui.components.DataRow
import com.starwars.inventar.ui.components.DatapadButton
import com.starwars.inventar.ui.components.DatapadDropdownSelector
import com.starwars.inventar.ui.components.DatapadScreen
import com.starwars.inventar.ui.components.DatapadTextField
import com.starwars.inventar.ui.components.EmptyState
import com.starwars.inventar.ui.components.HoloPanel
import com.starwars.inventar.ui.components.StatusBadge
import com.starwars.inventar.ui.components.articleStatusColor
import com.starwars.inventar.ui.formatCredits
import com.starwars.inventar.ui.theme.DatapadTheme
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.HoloCyan
import com.starwars.inventar.ui.theme.TextDim
import com.starwars.inventar.ui.theme.TextPrimary

enum class ArticleSortOption(val label: String) {
    NAME_ASC("Name (A–Z)"),
    NAME_DESC("Name (Z–A)"),
    PRICE_DESC("Preis: Höchste zuerst"),
    PRICE_ASC("Preis: Niedrigste zuerst"),
    NEWEST("Neueste zuerst")
}

@Composable
fun ArticleListScreen(
    viewModel: InventoryViewModel,
    onBack: () -> Unit,
    onOpen: (String) -> Unit
) {
    val articles by viewModel.articles.collectAsStateWithLifecycle()
    ArticleListContent(
        articles = articles,
        onBack = onBack,
        onOpen = onOpen
    )
}

@Composable
fun ArticleListContent(
    articles: List<Article>,
    onBack: () -> Unit,
    onOpen: (String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<ArticleCategory?>(null) }
    var selectedStatus by rememberSaveable { mutableStateOf<ArticleStatus?>(null) }
    var sortOption by rememberSaveable { mutableStateOf(ArticleSortOption.NAME_ASC) }
    var showFilters by rememberSaveable { mutableStateOf(false) }

    val filtered = remember(articles, query, selectedCategory, selectedStatus, sortOption) {
        articles.filter { article ->
            val matchesQuery = query.isBlank() || listOf(
                article.name,
                article.category.label,
                article.owner,
                article.partner,
                article.location,
                article.scene
            ).any { it.contains(query, ignoreCase = true) }

            val matchesCategory = selectedCategory == null || article.category == selectedCategory
            val matchesStatus = selectedStatus == null || article.status == selectedStatus

            matchesQuery && matchesCategory && matchesStatus
        }.let { list ->
            when (sortOption) {
                ArticleSortOption.NAME_ASC -> list.sortedBy { it.name.lowercase() }
                ArticleSortOption.NAME_DESC -> list.sortedByDescending { it.name.lowercase() }
                ArticleSortOption.PRICE_DESC -> list.sortedByDescending { it.price }
                ArticleSortOption.PRICE_ASC -> list.sortedBy { it.price }
                ArticleSortOption.NEWEST -> list.sortedByDescending { it.id }
            }
        }
    }

    DatapadScreen(
        title = "Artikel",
        subtitle = "${filtered.size} von ${articles.size} Einträgen",
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
                    DatapadTextField(value = query, onValueChange = { query = it }, label = "Suche")
                }
                DatapadButton(
                    text = if (showFilters) "Filter aus" else "Filter & Sort",
                    onClick = { showFilters = !showFilters },
                    accent = HoloCyan,
                    filled = showFilters
                )
            }

            if (showFilters) {
                HoloPanel(title = "Filter & Sortierung") {
                    val categoryOptions = listOf<ArticleCategory?>(null) + ArticleCategory.entries
                    DatapadDropdownSelector(
                        label = "Kategorie-Filter",
                        options = categoryOptions,
                        selected = selectedCategory,
                        onSelected = { selectedCategory = it },
                        optionLabel = { it?.label ?: "Alle Kategorien" }
                    )

                    val statusOptions = listOf<ArticleStatus?>(null) + ArticleStatus.entries
                    DatapadDropdownSelector(
                        label = "Status-Filter",
                        options = statusOptions,
                        selected = selectedStatus,
                        onSelected = { selectedStatus = it },
                        optionLabel = { it?.label ?: "Alle Status" }
                    )

                    DatapadDropdownSelector(
                        label = "Sortierung",
                        options = ArticleSortOption.entries.toList(),
                        selected = sortOption,
                        onSelected = { sortOption = it },
                        optionLabel = { it.label }
                    )
                }
            }

            if (filtered.isEmpty()) {
                EmptyState(if (articles.isEmpty()) "Noch keine Artikel erfasst" else "Keine Treffer")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(filtered, key = { it.id }) { article ->
                        ArticleCard(article = article, onClick = { onOpen(article.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(article: Article, onClick: () -> Unit) {
    HoloPanel(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = article.name.ifBlank { "(ohne Namen)" },
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            StatusBadge(article.status.label, articleStatusColor(article.status))
        }
        Text(
            text = "${article.category.label} · ${article.quantity}x",
            style = MaterialTheme.typography.bodySmall,
            color = TextDim
        )
        if (article.owner.isNotBlank() || article.partner.isNotBlank()) {
            DataRow("Besitzer", article.owner.ifBlank { "–" })
            DataRow("Partner", article.partner.ifBlank { "–" })
        }
        DataRow("Kosten", formatCredits(article.costs))
        DataRow("Preis", formatCredits(article.price))
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleListScreenPreview() {
    val sampleArticles = listOf(
        Article(
            id = "SW-HW-TEST_1",
            name = "DL-44 Blaster",
            category = ArticleCategory.WEAPON,
            status = ArticleStatus.BEREIT,
            owner = "Han Solo",
            partner = "Corellia Armory",
            quantity = 1,
            costs = 500.0,
            price = 1200.0
        ),
        Article(
            id = "SW-HW-TEST_2",
            name = "Sturmtruppen Rüstung",
            category = ArticleCategory.ARMOR,
            status = ArticleStatus.BESCHAEDIGT,
            owner = "Requisitenlager",
            quantity = 10,
            costs = 300.0,
            price = 750.0
        )
    )
    DatapadTheme {
        ArticleListContent(
            articles = sampleArticles,
            onBack = {},
            onOpen = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleListScreenEmptyPreview() {
    DatapadTheme {
        ArticleListContent(
            articles = emptyList(),
            onBack = {},
            onOpen = {}
        )
    }
}
