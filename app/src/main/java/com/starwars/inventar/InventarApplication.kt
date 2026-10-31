package com.starwars.inventar

import android.app.Application
import com.starwars.inventar.data.JsonFileRepository

class InventarApplication : Application() {
    val repository by lazy {
        JsonFileRepository(this)
    }
}
