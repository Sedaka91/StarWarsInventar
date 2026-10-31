package com.starwars.inventar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.starwars.inventar.InventarApplication
import com.starwars.inventar.data.Article
import com.starwars.inventar.data.Contract
import com.starwars.inventar.data.UserConfig
import com.starwars.inventar.data.WlanSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    val repository = (application as InventarApplication).repository

    val syncManager by lazy {
        WlanSyncManager(repository, getApplication())
    }

    val articles: StateFlow<List<Article>> = repository.articles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val contracts: StateFlow<List<Contract>> = repository.contracts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val userConfig: StateFlow<UserConfig> = repository.userConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserConfig())

    val deviceId: String
        get() = userConfig.value.deviceId

    fun saveUserConfig(config: UserConfig, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveUserConfig(config)
            onDone()
        }
    }

    fun clearAllData(onDone: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllData()
            withContext(Dispatchers.Main) {
                onDone()
            }
        }
    }

    suspend fun getArticle(id: String): Article? = repository.getArticle(id)

    suspend fun getContract(id: String): Contract? = repository.getContract(id)

    fun saveArticle(article: Article, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveArticle(article)
            onDone()
        }
    }

    fun saveContract(contract: Contract, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveContract(contract)
            onDone()
        }
    }

    fun deleteArticle(id: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteArticle(id)
            onDone()
        }
    }

    fun deleteContract(id: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteContract(id)
            onDone()
        }
    }

    fun reloadFromDisk() {
        repository.reloadFromDisk()
    }

    fun getStoragePath(): String = repository.getStoragePath()

    fun testSyncConnection(host: String, port: Int, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val res = syncManager.testConnection(host, port)
            onResult(res)
        }
    }

    fun syncBidirectional(host: String, port: Int, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val res = syncManager.syncBidirectional(host, port)
            onResult(res)
        }
    }

    fun pullFromServer(host: String, port: Int, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val res = syncManager.pullFromServer(host, port)
            onResult(res)
        }
    }

    fun pushToServer(host: String, port: Int, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val res = syncManager.pushToServer(host, port)
            onResult(res)
        }
    }
}
