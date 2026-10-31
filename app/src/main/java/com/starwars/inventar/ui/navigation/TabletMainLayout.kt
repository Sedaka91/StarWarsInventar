package com.starwars.inventar.ui.navigation

import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.inventar.data.Article
import com.starwars.inventar.data.Contract
import com.starwars.inventar.data.ContractStatus
import com.starwars.inventar.data.UserConfig
import com.starwars.inventar.ui.InventoryViewModel
import com.starwars.inventar.ui.components.DataRow
import com.starwars.inventar.ui.components.DatapadBackground
import com.starwars.inventar.ui.components.DatapadButton
import com.starwars.inventar.ui.components.HoloPanel
import com.starwars.inventar.ui.components.SmallCutShape
import com.starwars.inventar.ui.formatCredits
import com.starwars.inventar.ui.screens.ArticleEditContent
import com.starwars.inventar.ui.screens.ArticleListContent
import com.starwars.inventar.ui.screens.CompanyLogoView
import com.starwars.inventar.ui.screens.ContractEditContent
import com.starwars.inventar.ui.screens.ContractListContent
import com.starwars.inventar.ui.screens.SettingsDialog
import com.starwars.inventar.ui.screens.WlanSyncDialog
import com.starwars.inventar.ui.theme.DatapadBgTop
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.HoloCyan
import com.starwars.inventar.ui.theme.HoloGreen
import com.starwars.inventar.ui.theme.HoloRed
import com.starwars.inventar.ui.theme.TextDim
import kotlinx.coroutines.launch

sealed interface WorkspaceRoute {
    data object ArticleList : WorkspaceRoute
    data class ArticleEdit(val id: String) : WorkspaceRoute
    data object ContractList : WorkspaceRoute
    data class ContractEdit(val id: String) : WorkspaceRoute
}

@Composable
fun TabletMainLayout(viewModel: InventoryViewModel) {
    val articles by viewModel.articles.collectAsStateWithLifecycle()
    val contracts by viewModel.contracts.collectAsStateWithLifecycle()
    val userConfig by viewModel.userConfig.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val storagePath = viewModel.getStoragePath()

    var activeRoute by remember { mutableStateOf<WorkspaceRoute>(WorkspaceRoute.ArticleList) }
    var showSyncDialog by remember { mutableStateOf(false) }

    if (showSyncDialog) {
        WlanSyncDialog(
            viewModel = viewModel,
            onDismiss = { showSyncDialog = false }
        )
    }

    TabletMainLayoutContent(
        articles = articles,
        contracts = contracts,
        userConfig = userConfig,
        storagePath = storagePath,
        activeRoute = activeRoute,
        onNavigate = { activeRoute = it },
        onOpenSync = { showSyncDialog = true },
        onReloadDisk = {
            viewModel.reloadFromDisk()
            Toast.makeText(context, "JSON-Dateien vom Datenträger neu geladen", Toast.LENGTH_SHORT).show()
        },
        onClearAllData = {
            viewModel.clearAllData {
                Toast.makeText(context, "Alle Daten gelöscht – Blank State wiederhergestellt", Toast.LENGTH_SHORT).show()
            }
        },
        onSaveConfig = { config ->
            viewModel.saveUserConfig(config) {
                Toast.makeText(context, "Konfiguration gespeichert", Toast.LENGTH_SHORT).show()
            }
        },
        getArticle = { id -> viewModel.getArticle(id) },
        getContract = { id -> viewModel.getContract(id) },
        saveArticle = { article, onDone -> viewModel.saveArticle(article, onDone) },
        saveContract = { contract, onDone -> viewModel.saveContract(contract, onDone) },
        deleteArticle = { id, onDone -> viewModel.deleteArticle(id, onDone) },
        deleteContract = { id, onDone -> viewModel.deleteContract(id, onDone) }
    )
}

@Composable
fun CompanyIdentityCard(
    userConfig: UserConfig,
    onOpenSettings: () -> Unit
) {
    HoloPanel(accent = HoloAmber, title = null, onClick = onOpenSettings) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = userConfig.companyName,
                    style = MaterialTheme.typography.titleLarge,
                    color = HoloAmber
                )
                Text(
                    text = "Bediener: ${userConfig.userName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDim
                )
            }
            // Firmenlogo rechts vom Firmennamen, 50% größer (96.dp)
            Box(
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.Center
            ) {
                CompanyLogoView(
                    userConfig = userConfig,
                    modifier = Modifier.fillMaxSize(),
                    tint = HoloAmber
                )
            }
        }
    }
}

@Composable
fun DatapadDrawerContent(
    storagePath: String,
    onClearAllData: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Alle Daten löschen?") },
            text = { Text("Möchtest du wirklich alle Artikel, Verträge und Bilder unwiderruflich löschen und das Datapad auf den Blank State zurücksetzen?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onClearAllData()
                    onCloseDrawer()
                }) {
                    Text("ALLES LÖSCHEN", color = HoloRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("ABBRECHEN")
                }
            }
        )
    }

    ModalDrawerSheet(
        drawerContainerColor = DatapadBgTop,
        modifier = Modifier.width(320.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HoloPanel(accent = HoloCyan, title = "Anwendungs-Einstellungen") {
                Text(
                    text = "DATAPAD SYSTEM",
                    style = MaterialTheme.typography.titleMedium,
                    color = HoloCyan
                )
                Text(
                    text = "Lokaler Speicherpfad:\n$storagePath",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDim
                )
                Text(
                    text = "Datenformat: JSON (Dateibasiert)\nProtokoll: Star Wars In-Game Datapad",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDim
                )
            }

            Spacer(Modifier.weight(1f))

            DatapadButton(
                text = "⚠️ WERKSEINSTELLUNG / BLANK",
                onClick = { showConfirmDialog = true },
                accent = HoloRed,
                filled = false,
                modifier = Modifier.fillMaxWidth()
            )

            DatapadButton(
                text = "SCHLIESSEN",
                onClick = onCloseDrawer,
                accent = HoloCyan,
                filled = false,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "DATAPAD INVENTAR OS v0.1.0\nSTARKILLER SECTOR SEC-4",
                style = MaterialTheme.typography.labelSmall,
                color = TextDim
            )
        }
    }
}

@Composable
fun TabletMainLayoutContent(
    articles: List<Article>,
    contracts: List<Contract>,
    userConfig: UserConfig = UserConfig(),
    storagePath: String = "",
    activeRoute: WorkspaceRoute,
    onNavigate: (WorkspaceRoute) -> Unit,
    onReloadDisk: () -> Unit,
    onClearAllData: () -> Unit,
    onOpenSync: () -> Unit = {},
    onSaveConfig: (UserConfig) -> Unit = {},
    getArticle: suspend (String) -> Article? = { null },
    getContract: suspend (String) -> Contract? = { null },
    saveArticle: (Article, () -> Unit) -> Unit = { _, done -> done() },
    saveContract: (Contract, () -> Unit) -> Unit = { _, done -> done() },
    deleteArticle: (String, () -> Unit) -> Unit = { _, done -> done() },
    deleteContract: (String, () -> Unit) -> Unit = { _, done -> done() }
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showSettingsDialog by remember { mutableStateOf(false) }

    val transition = rememberInfiniteTransition(label = "syncPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "syncPulseAlpha"
    )

    if (showSettingsDialog) {
        SettingsDialog(
            currentConfig = userConfig,
            onSave = { newConfig ->
                onSaveConfig(newConfig)
                showSettingsDialog = false
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DatapadDrawerContent(
                storagePath = storagePath,
                onClearAllData = onClearAllData,
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        val pieces = articles.sumOf { it.quantity }
        val stockValue = articles.sumOf { it.price * it.quantity }
        val stockCosts = articles.sumOf { it.costs * it.quantity }
        val activeContracts = contracts.count { it.status == ContractStatus.LAUFEND }

        DatapadBackground {
            Row(Modifier.fillMaxSize()) {
                // 1/3 LINKE LEISTE: Identität, Statusbericht, Aktionen & Sync
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Top Bar in linker Leiste mit App-Menü Button (Anwendungs-Einstellungen)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(SmallCutShape)
                                .background(HoloCyan.copy(alpha = 0.15f))
                                .border(1.dp, HoloCyan.copy(alpha = 0.7f), SmallCutShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "App-Einstellungen",
                                tint = HoloCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "DATAPAD SYS",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan
                            )
                            Box(
                                Modifier
                                    .size(8.dp)
                                    .alpha(pulse)
                                    .background(HoloGreen, CircleShape)
                            )
                        }
                    }

                    // Identität & Firma in der linken Leiste
                    CompanyIdentityCard(
                        userConfig = userConfig,
                        onOpenSettings = { showSettingsDialog = true }
                    )

                    // Statusbericht
                    HoloPanel(title = "Statusbericht") {
                        DataRow("Einträge (Artikel)", articles.size.toString())
                        DataRow("Stückzahl gesamt", pieces.toString())
                        DataRow("Gesamtwert", formatCredits(stockValue), valueColor = HoloCyan)
                        DataRow("Gesamtkosten", formatCredits(stockCosts))
                        DataRow("Laufende Verträge", activeContracts.toString(), valueColor = HoloAmber)
                    }

                    // Aktionen & Sync
                    HoloPanel(title = "Aktionen & Sync") {
                        DatapadButton(
                            text = "📡 WLAN SYNC",
                            onClick = onOpenSync,
                            accent = HoloCyan,
                            filled = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        DatapadButton(
                            text = "+ NEUER ARTIKEL",
                            onClick = { onNavigate(WorkspaceRoute.ArticleEdit("")) },
                            accent = HoloCyan,
                            filled = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                        DatapadButton(
                            text = "+ NEUER VERTRAG",
                            onClick = { onNavigate(WorkspaceRoute.ContractEdit("")) },
                            accent = HoloAmber,
                            filled = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                        DatapadButton(
                            text = "🔄 DATEN NEU LADEN",
                            onClick = onReloadDisk,
                            accent = HoloGreen,
                            filled = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Vertikale Trennlinie
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    HoloCyan.copy(alpha = 0.6f),
                                    HoloAmber.copy(alpha = 0.4f),
                                    HoloCyan.copy(alpha = 0.1f)
                                )
                            )
                        )
                )

                // 2/3 RECHTER ARBEITSBEREICH mit Reitern (Tabs) oben über dem Frame
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    // Reiter (Tabs) über dem rechten Frame
                    val isArticleActive = activeRoute is WorkspaceRoute.ArticleList || activeRoute is WorkspaceRoute.ArticleEdit
                    val isContractActive = activeRoute is WorkspaceRoute.ContractList || activeRoute is WorkspaceRoute.ContractEdit

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DatapadBgTop)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DatapadButton(
                            text = "📦 ARTIKEL (${articles.size})",
                            onClick = { onNavigate(WorkspaceRoute.ArticleList) },
                            accent = HoloCyan,
                            filled = isArticleActive,
                            modifier = Modifier.weight(1f)
                        )
                        DatapadButton(
                            text = "📜 VERTRÄGE (${contracts.size})",
                            onClick = { onNavigate(WorkspaceRoute.ContractList) },
                            accent = HoloAmber,
                            filled = isContractActive,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(HoloCyan.copy(alpha = 0.3f))
                    )

                    // Workspace Content
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        when (activeRoute) {
                            is WorkspaceRoute.ArticleList -> {
                                ArticleListContent(
                                    articles = articles,
                                    onBack = { },
                                    onOpen = { id -> onNavigate(WorkspaceRoute.ArticleEdit(id)) }
                                )
                            }

                            is WorkspaceRoute.ArticleEdit -> {
                                val articleId = activeRoute.id
                                val isNew = articleId.isBlank()
                                var loadedArticle by remember(articleId) { mutableStateOf<Article?>(null) }

                                LaunchedEffect(articleId) {
                                    if (!isNew) {
                                        loadedArticle = getArticle(articleId)
                                    }
                                }

                                if (isNew || loadedArticle != null) {
                                    ArticleEditContent(
                                        initialArticle = if (isNew) Article() else loadedArticle!!,
                                        contracts = contracts,
                                        isNew = isNew,
                                        onSave = { updatedArticle ->
                                            saveArticle(updatedArticle) {
                                                onNavigate(WorkspaceRoute.ArticleList)
                                            }
                                        },
                                        onDelete = if (!isNew) {
                                            {
                                                deleteArticle(articleId) {
                                                    onNavigate(WorkspaceRoute.ArticleList)
                                                }
                                            }
                                        } else null,
                                        onClose = { onNavigate(WorkspaceRoute.ArticleList) }
                                    )
                                }
                            }

                            is WorkspaceRoute.ContractList -> {
                                ContractListContent(
                                    contracts = contracts,
                                    onBack = { },
                                    onOpen = { id -> onNavigate(WorkspaceRoute.ContractEdit(id)) }
                                )
                            }

                            is WorkspaceRoute.ContractEdit -> {
                                val contractId = activeRoute.id
                                val isNew = contractId.isBlank()
                                var loadedContract by remember(contractId) { mutableStateOf<Contract?>(null) }

                                LaunchedEffect(contractId) {
                                    if (!isNew) {
                                        loadedContract = getContract(contractId)
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
                                            saveContract(updatedContract) {
                                                onNavigate(WorkspaceRoute.ContractList)
                                            }
                                        },
                                        onDelete = if (!isNew) {
                                            {
                                                deleteContract(contractId) {
                                                    onNavigate(WorkspaceRoute.ContractList)
                                                }
                                            }
                                        } else null,
                                        onClose = { onNavigate(WorkspaceRoute.ContractList) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
