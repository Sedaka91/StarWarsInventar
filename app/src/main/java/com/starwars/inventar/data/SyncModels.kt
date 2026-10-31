package com.starwars.inventar.data

import kotlinx.serialization.Serializable

@Serializable
data class SyncBundle(
    val articles: List<Article> = emptyList(),
    val contracts: List<Contract> = emptyList(),
    val userConfig: UserConfig? = null,
    val clientTimestamp: Long = System.currentTimeMillis(),
    val syncMode: String = "BIDIRECTIONAL",
    val hardwareId: String = ""
)

@Serializable
data class SyncResult(
    val success: Boolean,
    val message: String,
    val articles: List<Article> = emptyList(),
    val contracts: List<Contract> = emptyList(),
    val userConfig: UserConfig? = null,
    val serverTimestamp: Long = 0L,
    val importedArticlesCount: Int = 0,
    val importedContractsCount: Int = 0,
    val hardwareId: String = ""
)

@Serializable
data class ServerStatus(
    val appName: String = "",
    val version: String = "",
    val status: String = "",
    val localAddresses: List<String> = emptyList(),
    val port: Int = 8080,
    val articleCount: Int = 0,
    val contractCount: Int = 0,
    val userCount: Int = 0,
    val userConfig: UserConfig? = null,
    val lastSyncTimestamp: Long? = null,
    val serverTime: Long = 0L
)
