package com.starwars.inventar.data

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class JsonFileRepository(private val context: Context) {

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val baseDir: File by lazy {
        File(context.filesDir, "inventory").also { if (!it.exists()) it.mkdirs() }
    }

    private val articlesDir: File by lazy {
        File(baseDir, "articles").also { if (!it.exists()) it.mkdirs() }
    }

    private val contractsDir: File by lazy {
        File(baseDir, "contracts").also { if (!it.exists()) it.mkdirs() }
    }

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _contracts = MutableStateFlow<List<Contract>>(emptyList())
    val contracts: StateFlow<List<Contract>> = _contracts.asStateFlow()

    private val configFile: File by lazy {
        File(baseDir, "config.json")
    }

    private val _userConfig = MutableStateFlow(UserConfig())
    val userConfig: StateFlow<UserConfig> = _userConfig.asStateFlow()

    init {
        loadConfig()
        reloadFromDisk()
    }

    private fun generateHardwareId(): String {
        return runCatching {
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
            val hardwareHash = "${androidId}_${Build.BOARD}_${Build.DEVICE}_${Build.HARDWARE}".hashCode().toUInt().toString(16).uppercase()
            "SW-HW-$hardwareHash"
        }.getOrElse {
            "SW-HW-${System.currentTimeMillis().toString(16).uppercase()}"
        }
    }

    private fun loadConfig() {
        runCatching {
            if (configFile.exists()) {
                val config = json.decodeFromString<UserConfig>(configFile.readText())
                val finalConfig = if (config.deviceId.isBlank()) {
                    config.copy(deviceId = generateHardwareId())
                } else {
                    config
                }
                _userConfig.value = finalConfig
                if (config.deviceId.isBlank()) {
                    writeConfig(finalConfig)
                }
            } else {
                val defaultConfig = UserConfig(deviceId = generateHardwareId())
                writeConfig(defaultConfig)
                _userConfig.value = defaultConfig
            }
        }.onFailure {
            _userConfig.value = UserConfig(deviceId = generateHardwareId())
        }
    }

    private fun writeConfig(config: UserConfig) {
        runCatching {
            configFile.writeText(json.encodeToString(config))
        }
    }

    suspend fun saveUserConfig(config: UserConfig): Unit = withContext(Dispatchers.IO) {
        val toSave = if (config.deviceId.isBlank()) {
            config.copy(deviceId = _userConfig.value.deviceId.ifBlank { generateHardwareId() })
        } else {
            config
        }
        writeConfig(toSave)
        _userConfig.value = toSave
    }

    fun clearAllData() {
        runCatching {
            articlesDir.listFiles()?.forEach { it.delete() }
            contractsDir.listFiles()?.forEach { it.delete() }
            val imagesDir = File(baseDir, "article_images")
            if (imagesDir.exists()) {
                imagesDir.listFiles()?.forEach { it.delete() }
            }
            reloadFromDisk()
        }
    }

    fun applySyncBundle(newArticles: List<Article>, newContracts: List<Contract>, serverConfig: UserConfig?) {
        newArticles.forEach { mergeAndWriteArticle(it) }
        newContracts.forEach { mergeAndWriteContract(it) }
        if (serverConfig != null) {
            val myHardwareId = _userConfig.value.deviceId
            val mergedConfig = serverConfig.copy(deviceId = myHardwareId.ifBlank { serverConfig.deviceId })
            writeConfig(mergedConfig)
            _userConfig.value = mergedConfig
        }
        reloadFromDisk()
    }

    fun replaceAllFromBundle(newArticles: List<Article>, newContracts: List<Contract>, serverConfig: UserConfig?) {
        newArticles.forEach { mergeAndWriteArticle(it) }
        newContracts.forEach { mergeAndWriteContract(it) }
        if (serverConfig != null) {
            val myHardwareId = _userConfig.value.deviceId
            val mergedConfig = serverConfig.copy(deviceId = myHardwareId.ifBlank { serverConfig.deviceId })
            writeConfig(mergedConfig)
            _userConfig.value = mergedConfig
        }
        reloadFromDisk()
    }

    fun getAllArticlesIncludingDeleted(): List<Article> {
        return runCatching {
            articlesDir.listFiles { file -> file.extension == "json" }
                ?.mapNotNull { file ->
                    runCatching { json.decodeFromString<Article>(file.readText()) }.getOrNull()
                }
                ?: emptyList()
        }.getOrDefault(emptyList())
    }

    fun getAllContractsIncludingDeleted(): List<Contract> {
        return runCatching {
            contractsDir.listFiles { file -> file.extension == "json" }
                ?.mapNotNull { file ->
                    runCatching { json.decodeFromString<Contract>(file.readText()) }.getOrNull()
                }
                ?: emptyList()
        }.getOrDefault(emptyList())
    }

    fun reloadFromDisk() {
        runCatching {
            val myHwId = _userConfig.value.deviceId
            val loadedArticles = articlesDir.listFiles { file -> file.extension == "json" }
                ?.mapNotNull { file ->
                    runCatching {
                        val art = json.decodeFromString<Article>(file.readText())
                        if (art.deleted) return@mapNotNull null

                        val localPath = if (art.imagePath.isNullOrBlank() && !art.imageBase64.isNullOrBlank()) {
                            decodeBase64ToLocalFile(art.imageBase64)
                        } else {
                            art.imagePath
                        }

                        val finalArt = if (art.hardwareId.isBlank() && myHwId.isNotBlank() || localPath != art.imagePath) {
                            art.copy(hardwareId = if (art.hardwareId.isBlank()) myHwId else art.hardwareId, imagePath = localPath, updatedAt = System.currentTimeMillis())
                        } else art

                        writeArticleDirect(finalArt)
                        finalArt
                    }.getOrNull()
                }
                ?.sortedBy { it.name.lowercase() }
                ?: emptyList()

            val loadedContracts = contractsDir.listFiles { file -> file.extension == "json" }
                ?.mapNotNull { file ->
                    runCatching {
                        val con = json.decodeFromString<Contract>(file.readText())
                        if (con.deleted) return@mapNotNull null
                        if (con.hardwareId.isBlank() && myHwId.isNotBlank()) {
                            val patched = con.copy(hardwareId = myHwId, updatedAt = System.currentTimeMillis())
                            writeContractDirect(patched)
                            patched
                        } else con
                    }.getOrNull()
                }
                ?.sortedBy { it.title.lowercase() }
                ?: emptyList()

            _articles.value = loadedArticles
            _contracts.value = loadedContracts
        }
    }

    suspend fun getArticle(id: String): Article? = withContext(Dispatchers.IO) {
        val file = File(articlesDir, "$id.json")
        if (!file.exists()) return@withContext null
        val art = runCatching { json.decodeFromString<Article>(file.readText()) }.getOrNull()
        if (art == null || art.deleted) null else art
    }

    suspend fun getContract(id: String): Contract? = withContext(Dispatchers.IO) {
        val file = File(contractsDir, "$id.json")
        if (!file.exists()) return@withContext null
        val con = runCatching { json.decodeFromString<Contract>(file.readText()) }.getOrNull()
        if (con == null || con.deleted) null else con
    }

    suspend fun saveArticle(article: Article): Unit = withContext(Dispatchers.IO) {
        val hwId = article.hardwareId.ifBlank { _userConfig.value.deviceId.ifBlank { generateHardwareId() } }
        val timestamp = System.currentTimeMillis()
        val targetId = if (article.id.isBlank()) "${hwId}_$timestamp" else article.id
        val updated = article.copy(id = targetId, hardwareId = hwId, updatedAt = timestamp)
        writeArticleDirect(updated)
        reloadFromDisk()
    }

    suspend fun saveContract(contract: Contract): Unit = withContext(Dispatchers.IO) {
        val hwId = contract.hardwareId.ifBlank { _userConfig.value.deviceId.ifBlank { generateHardwareId() } }
        val timestamp = System.currentTimeMillis()
        val targetId = if (contract.id.isBlank()) "${hwId}_$timestamp" else contract.id
        val updated = contract.copy(id = targetId, hardwareId = hwId, updatedAt = timestamp)
        writeContractDirect(updated)
        reloadFromDisk()
    }

    suspend fun deleteArticle(id: String): Unit = withContext(Dispatchers.IO) {
        val file = File(articlesDir, "$id.json")
        val existing = if (file.exists()) {
            runCatching { json.decodeFromString<Article>(file.readText()) }.getOrNull()
        } else null

        val timestamp = System.currentTimeMillis()
        val deletedArticle = existing?.copy(deleted = true, updatedAt = timestamp)
            ?: Article(id = id, deleted = true, updatedAt = timestamp)
        writeArticleDirect(deletedArticle)
        reloadFromDisk()
    }

    suspend fun deleteContract(id: String): Unit = withContext(Dispatchers.IO) {
        val file = File(contractsDir, "$id.json")
        val existing = if (file.exists()) {
            runCatching { json.decodeFromString<Contract>(file.readText()) }.getOrNull()
        } else null

        val timestamp = System.currentTimeMillis()
        val deletedContract = existing?.copy(deleted = true, updatedAt = timestamp)
            ?: Contract(id = id, deleted = true, updatedAt = timestamp)
        writeContractDirect(deletedContract)
        reloadFromDisk()
    }

    private fun encodeImageToBase64(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return runCatching {
            val file = File(path)
            if (file.exists()) {
                val bytes = file.readBytes()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else null
        }.getOrNull()
    }

    private fun decodeBase64ToLocalFile(base64Str: String?): String? {
        if (base64Str.isNullOrBlank()) return null
        return runCatching {
            val bytes = Base64.decode(base64Str, Base64.NO_WRAP)
            val imagesDir = File(baseDir, "article_images").also { if (!it.exists()) it.mkdirs() }
            val destFile = File(imagesDir, "sync_img_${System.currentTimeMillis()}.png")
            destFile.writeBytes(bytes)
            destFile.absolutePath
        }.getOrNull()
    }

    private fun mergeAndWriteArticle(incoming: Article) {
        val file = File(articlesDir, "${incoming.id}.json")
        val myHwId = _userConfig.value.deviceId.ifBlank { generateHardwareId() }
        val finalIncoming = if (incoming.hardwareId.isBlank()) incoming.copy(hardwareId = myHwId) else incoming

        if (file.exists()) {
            val local = runCatching { json.decodeFromString<Article>(file.readText()) }.getOrNull()
            if (local != null) {
                if (finalIncoming.updatedAt >= local.updatedAt) {
                    writeArticleDirect(finalIncoming)
                } else {
                    // Local is newer
                }
            } else {
                writeArticleDirect(finalIncoming)
            }
        } else {
            writeArticleDirect(finalIncoming)
        }
    }

    private fun writeArticleDirect(article: Article) {
        runCatching {
            val hwId = article.hardwareId.ifBlank { _userConfig.value.deviceId.ifBlank { generateHardwareId() } }
            val timestamp = if (article.updatedAt == 0L) System.currentTimeMillis() else article.updatedAt

            val b64 = article.imageBase64 ?: encodeImageToBase64(article.imagePath)
            val localPath = if (article.imagePath.isNullOrBlank() && !b64.isNullOrBlank()) {
                decodeBase64ToLocalFile(b64)
            } else {
                article.imagePath
            }

            val toWrite = article.copy(
                hardwareId = hwId,
                updatedAt = timestamp,
                imagePath = localPath,
                imageBase64 = b64
            )
            val file = File(articlesDir, "${toWrite.id}.json")
            val content = json.encodeToString(toWrite)
            file.writeText(content)
        }
    }

    private fun mergeAndWriteContract(incoming: Contract) {
        val file = File(contractsDir, "${incoming.id}.json")
        val myHwId = _userConfig.value.deviceId.ifBlank { generateHardwareId() }
        val finalIncoming = if (incoming.hardwareId.isBlank()) incoming.copy(hardwareId = myHwId) else incoming

        if (file.exists()) {
            val local = runCatching { json.decodeFromString<Contract>(file.readText()) }.getOrNull()
            if (local != null) {
                if (finalIncoming.updatedAt >= local.updatedAt) {
                    writeContractDirect(finalIncoming)
                } else {
                    // Local is newer
                }
            } else {
                writeContractDirect(finalIncoming)
            }
        } else {
            writeContractDirect(finalIncoming)
        }
    }

    private fun writeContractDirect(contract: Contract) {
        runCatching {
            val hwId = contract.hardwareId.ifBlank { _userConfig.value.deviceId.ifBlank { generateHardwareId() } }
            val timestamp = if (contract.updatedAt == 0L) System.currentTimeMillis() else contract.updatedAt
            val toWrite = contract.copy(hardwareId = hwId, updatedAt = timestamp)
            val file = File(contractsDir, "${toWrite.id}.json")
            val content = json.encodeToString(toWrite)
            file.writeText(content)
        }
    }

    fun getStoragePath(): String = baseDir.absolutePath
}
