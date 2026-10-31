package com.starwars.inventar.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import java.net.HttpURLConnection
import java.net.URL

class WlanSyncManager(
    private val repository: JsonFileRepository,
    context: Context
) {
    private val prefs = context.getSharedPreferences("wlan_sync_prefs", Context.MODE_PRIVATE)

    var savedHost: String
        get() = prefs.getString("server_host", "192.168.1.100") ?: "192.168.1.100"
        set(value) = prefs.edit().putString("server_host", value).apply()

    var savedPort: Int
        get() = prefs.getInt("server_port", 8080)
        set(value) = prefs.edit().putInt("server_port", value).apply()

    private val json = repository.json

    suspend fun testConnection(host: String, port: Int): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            savedHost = host
            savedPort = port
            val url = URL("http://$host:$port/api/status")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 3000
                readTimeout = 3000
                setRequestProperty("Accept", "application/json")
            }
            val code = conn.responseCode
            if (code in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val status = json.decodeFromString<ServerStatus>(responseText)
                "Verbindung erfolgreich! Server: ${status.appName} (${status.articleCount} Artikel, ${status.contractCount} Verträge)"
            } else {
                throw Exception("HTTP-Status $code: ${conn.responseMessage}")
            }
        }
    }

    suspend fun syncBidirectional(host: String, port: Int): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            savedHost = host
            savedPort = port
            val url = URL("http://$host:$port/api/sync/bidirectional")
            val outgoingBundle = SyncBundle(
                articles = repository.getAllArticlesIncludingDeleted(),
                contracts = repository.getAllContractsIncludingDeleted(),
                userConfig = repository.userConfig.value,
                clientTimestamp = System.currentTimeMillis(),
                syncMode = "BIDIRECTIONAL"
            )
            val jsonBody = json.encodeToString(outgoingBundle)

            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 7000
                readTimeout = 7000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }

            conn.outputStream.use { os ->
                os.write(jsonBody.toByteArray(Charsets.UTF_8))
            }

            val code = conn.responseCode
            if (code in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val result = json.decodeFromString<SyncResult>(responseText)
                repository.applySyncBundle(result.articles, result.contracts, result.userConfig ?: UserConfig())
                "Bidirektional synchronisiert: ${result.articles.size} Artikel, ${result.contracts.size} Verträge aktuell."
            } else {
                throw Exception("HTTP-Fehler $code: ${conn.responseMessage}")
            }
        }
    }

    suspend fun pullFromServer(host: String, port: Int): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            savedHost = host
            savedPort = port
            val url = URL("http://$host:$port/api/sync/bundle")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 7000
                readTimeout = 7000
                setRequestProperty("Accept", "application/json")
            }
            val code = conn.responseCode
            if (code in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val bundle = json.decodeFromString<SyncBundle>(responseText)
                repository.replaceAllFromBundle(bundle.articles, bundle.contracts, bundle.userConfig ?: UserConfig())
                "Vom PC geladen: ${bundle.articles.size} Artikel, ${bundle.contracts.size} Verträge übernommen."
            } else {
                throw Exception("HTTP-Fehler $code: ${conn.responseMessage}")
            }
        }
    }

    suspend fun pushToServer(host: String, port: Int): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            savedHost = host
            savedPort = port
            val url = URL("http://$host:$port/api/sync/push")
            val outgoingBundle = SyncBundle(
                articles = repository.getAllArticlesIncludingDeleted(),
                contracts = repository.getAllContractsIncludingDeleted(),
                userConfig = repository.userConfig.value,
                clientTimestamp = System.currentTimeMillis(),
                syncMode = "PUSH_TO_SERVER"
            )
            val jsonBody = json.encodeToString(outgoingBundle)

            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 7000
                readTimeout = 7000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }

            conn.outputStream.use { os ->
                os.write(jsonBody.toByteArray(Charsets.UTF_8))
            }

            val code = conn.responseCode
            if (code in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val result = json.decodeFromString<SyncResult>(responseText)
                "Erfolgreich an den PC gesendet: ${repository.articles.value.size} Artikel, ${repository.contracts.value.size} Verträge."
            } else {
                throw Exception("HTTP-Fehler $code: ${conn.responseMessage}")
            }
        }
    }
}
