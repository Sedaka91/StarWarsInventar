package com.starwars.inventar

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.starwars.inventar.ui.InventoryViewModel
import com.starwars.inventar.ui.navigation.AppNavigation
import com.starwars.inventar.ui.theme.DatapadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            DatapadTheme {
                // Ein ViewModel für die ganze Activity, alle Bildschirme teilen sich die Daten
                val viewModel: InventoryViewModel = viewModel()
                AppNavigation(viewModel)
            }
        }
    }
}
