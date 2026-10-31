package com.starwars.inventar.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.inventar.data.Article
import com.starwars.inventar.data.ArticleCategory
import com.starwars.inventar.data.ArticleStatus
import com.starwars.inventar.ui.InventoryViewModel
import com.starwars.inventar.ui.components.DatapadButton
import com.starwars.inventar.ui.components.DatapadDropdownSelector
import com.starwars.inventar.ui.components.DatapadScreen
import com.starwars.inventar.ui.components.DatapadTextField
import com.starwars.inventar.ui.components.EmptyState
import com.starwars.inventar.ui.components.HoloPanel
import com.starwars.inventar.ui.components.SmallCutShape
import com.starwars.inventar.ui.components.StatusBadge
import com.starwars.inventar.ui.components.articleStatusColor
import com.starwars.inventar.ui.formatCredits
import com.starwars.inventar.ui.theme.DatapadTheme
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.HoloCyan
import java.io.File

enum class ArticleSortOption(val label: String) {
    NAME_ASC("Name (A–Z)"),
    NAME_DESC("Name (Z–A)"),
    PRICE_DESC("Preis: Höchste zuerst"),
    PRICE_ASC("Preis: Niedrigste zuerst"),
    NEWEST("Neueste zuerst")
}

@Composable
fun ArticleImageView(
    imagePath: String?,
    fallbackCategory: ArticleCategory,
    modifier: Modifier = Modifier,
    tint: Color = HoloAmber
) {
    val bitmap = remember(imagePath) {
        if (!imagePath.isNullOrBlank()) {
            runCatching {
                val file = File(imagePath)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                } else null
            }.getOrNull()
        } else null
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Artikelbild",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        val icon = when (fallbackCategory) {
            ArticleCategory.WEAPON -> Icons.Default.Build
            ArticleCategory.ARMOR -> Icons.Default.Shield
            ArticleCategory.DROID -> Icons.Default.Memory
            ArticleCategory.SHIP_PART -> Icons.Default.RocketLaunch
            ArticleCategory.SUPPLY -> Icons.Default.Inventory
            ArticleCategory.HOLOCRON -> Icons.Default.Star
            ArticleCategory.MISC -> Icons.Default.Build
        }
        Icon(
            imageVector = icon,
            contentDescription = "Artikel-Symbol",
            tint = tint,
            modifier = modifier
        )
    }
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
                    DatapadTextField(value = query, onValueChange = { query = it }, label = "Katalog-Suche")
                }
                DatapadButton(
                    text = if (showFilters) "Filter aus" else "Filter & Sort",
                    onClick = { showFilters = !showFilters },
                    accent = HoloAmber,
                    filled = showFilters
                )
            }

            if (showFilters) {
                HoloPanel(accent = HoloAmber, title = "Katalog-Filter & Sortierung") {
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
                EmptyState(if (articles.isEmpty()) "Noch keine Artikel im Katalog erfasst" else "Keine Treffer im Katalog")
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
    val cardBg = Color(0xFF151820).copy(alpha = 0.7f)
    val cardBorder = Color(0xFF5A6478)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp) // Feste Höhe der Karte, damit der Eintrag nicht mit dem Bild wächst
            .clip(SmallCutShape)
            .background(cardBg)
            .border(1.dp, cardBorder, SmallCutShape)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bild links: 50% breiter (144.dp) und füllt die feste Kartenhöhe (116.dp) exakt aus.
            Box(
                modifier = Modifier
                    .width(144.dp)
                    .fillMaxHeight()
                    .clip(SmallCutShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                val bitmap = remember(article.imagePath) {
                    if (!article.imagePath.isNullOrBlank()) {
                        runCatching {
                            val file = File(article.imagePath)
                            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap() else null
                        }.getOrNull()
                    } else null
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Artikelbild",
                        modifier = Modifier
                            .fillMaxHeight()
                            .clipToBounds(),
                        contentScale = ContentScale.FillHeight
                    )
                } else {
                    val icon = when (article.category) {
                        ArticleCategory.WEAPON -> Icons.Default.Build
                        ArticleCategory.ARMOR -> Icons.Default.Shield
                        ArticleCategory.DROID -> Icons.Default.Memory
                        ArticleCategory.SHIP_PART -> Icons.Default.RocketLaunch
                        ArticleCategory.SUPPLY -> Icons.Default.Inventory
                        ArticleCategory.HOLOCRON -> Icons.Default.Star
                        ArticleCategory.MISC -> Icons.Default.Build
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Artikel-Symbol",
                        tint = HoloAmber,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Inhaltsbereich rechts
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = article.name.ifBlank { "(ohne Bezeichnung)" }.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = HoloAmber,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        StatusBadge(article.status.label, articleStatusColor(article.status))
                    }

                    Text(
                        text = article.category.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )

                    if (article.description.isNotBlank()) {
                        Text(
                            text = article.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val code = "STI-${article.category.name.take(3)}-${article.id.takeLast(4).padStart(4, '0')}"
                    Text(
                        text = "$code | ${formatCredits(article.price)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = HoloAmber
                    )
                    Text(
                        text = "${article.quantity}x · ${article.location.ifBlank { "Depot" }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleListScreenPreview() {
    val sampleArticles = listOf(
        Article(
            id = "SW-HW-TEST_1",
            name = "S.I.I. 'DL-44' Schwerer Blaster",
            category = ArticleCategory.WEAPON,
            status = ArticleStatus.BEREIT,
            description = "Kompakter, robuster Blaster mit hoher Durchschlagskraft für den Außeneinsatz.",
            owner = "Han Solo",
            partner = "Corellia Armory",
            quantity = 1,
            costs = 500.0,
            price = 1200.0
        ),
        Article(
            id = "SW-HW-TEST_2",
            name = "S.I.I. Sturmtruppen Rüstung",
            category = ArticleCategory.ARMOR,
            status = ArticleStatus.BESCHAEDIGT,
            description = "Modulare Panzerplatten mit integriertem Kommlink und Klima-Regulierung.",
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
