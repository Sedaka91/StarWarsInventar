package com.starwars.inventar.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
enum class ArticleCategory(val label: String) {
    WEAPON("Waffen & Blaster"),
    ARMOR("Rüstung & Helme"),
    DROID("Droiden & Technik"),
    SHIP_PART("Schiffs- & Antriebsteile"),
    SUPPLY("Vorräte & Rationen"),
    HOLOCRON("Artefakte & Holocron"),
    MISC("Ausrüstung & Sonstiges")
}

@Serializable
enum class ArticleStatus(val label: String) {
    BEREIT("Einsatzbereit"),
    IM_EINSATZ("Im aktiven Einsatz"),
    BESCHAEDIGT("Beschädigt / Reparaturbedarf"),
    IN_LAGER("Im Depot eingelagert"),
    VERSCHOLLEN("Verschollen / Vermisst")
}

@Serializable
enum class ContractType(val label: String) {
    HANDEL("Handelsvertrag"),
    KAUF("Kaufabwicklung"),
    BESCHAFFUNG("Beschaffungsauftrag"),
    KOPFGELD("Kopfgeld / Söldnerauftrag"),
    GEHEIM("Klassifizierte Operation")
}

@Serializable
enum class ContractStatus(val label: String) {
    ENTWURF("Entwurf"),
    LAUFEND("Laufend / Aktiv"),
    ERFUELLT("Erfüllt"),
    GESCHEITERT("Gescheitert / Abgebrochen")
}

@Serializable
@Entity(tableName = "contracts")
data class Contract(
    @PrimaryKey val id: String = "",
    val contractNumber: String = "",
    val title: String = "",
    val type: ContractType = ContractType.HANDEL,
    val status: ContractStatus = ContractStatus.ENTWURF,
    /** Besitzer bzw. Auftraggeber */
    val owner: String = "",
    /** Vertragspartner */
    val partner: String = "",
    /** Datum als UTC-Epoch-Millisekunden, null = nicht gesetzt */
    val startDate: Long? = null,
    val endDate: Long? = null,
    /** Kosten und Preis in Credits */
    val costs: Double = 0.0,
    val price: Double = 0.0,
    val deposit: Double = 0.0,
    val signed: Boolean = false,
    val terms: String = "",
    val notes: String = "",
    val hardwareId: String = "",
    var deleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "articles",
    foreignKeys = [
        ForeignKey(
            entity = Contract::class,
            parentColumns = ["id"],
            childColumns = ["contractId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("contractId")]
)
data class Article(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val category: ArticleCategory = ArticleCategory.WEAPON,
    val status: ArticleStatus = ArticleStatus.BEREIT,
    val description: String = "",
    /** Besitzer des Artikels */
    val owner: String = "",
    /** Partner, z. B. Leihgeber oder Lieferant */
    val partner: String = "",
    val quantity: Int = 1,
    val location: String = "",
    /** Szene bzw. Akt, in dem der Artikel gebraucht wird */
    val scene: String = "",
    /** Kosten und Preis pro Stück in Credits */
    val costs: Double = 0.0,
    val price: Double = 0.0,
    val contractId: String? = null,
    val notes: String = "",
    val hardwareId: String = "",
    var deleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val imagePath: String? = null,
    val imageBase64: String? = null
)
