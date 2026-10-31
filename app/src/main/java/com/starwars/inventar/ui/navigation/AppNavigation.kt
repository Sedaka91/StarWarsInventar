package com.starwars.inventar.ui.navigation

import androidx.compose.runtime.Composable
import com.starwars.inventar.ui.InventoryViewModel

@Composable
fun AppNavigation(viewModel: InventoryViewModel) {
    TabletMainLayout(viewModel = viewModel)
}
