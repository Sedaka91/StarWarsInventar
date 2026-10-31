package com.starwars.inventar.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.IllegalArgumentException
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ArticleDao_Impl(
  __db: RoomDatabase,
) : ArticleDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfArticle: EntityUpsertAdapter<Article>
  init {
    this.__db = __db
    this.__upsertAdapterOfArticle = EntityUpsertAdapter<Article>(object : EntityInsertAdapter<Article>() {
      protected override fun createQuery(): String = "INSERT INTO `articles` (`id`,`name`,`category`,`status`,`description`,`owner`,`partner`,`quantity`,`location`,`scene`,`costs`,`price`,`contractId`,`notes`,`hardwareId`,`deleted`,`updatedAt`,`imagePath`,`imageBase64`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Article) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, __ArticleCategory_enumToString(entity.category))
        statement.bindText(4, __ArticleStatus_enumToString(entity.status))
        statement.bindText(5, entity.description)
        statement.bindText(6, entity.owner)
        statement.bindText(7, entity.partner)
        statement.bindLong(8, entity.quantity.toLong())
        statement.bindText(9, entity.location)
        statement.bindText(10, entity.scene)
        statement.bindDouble(11, entity.costs)
        statement.bindDouble(12, entity.price)
        val _tmpContractId: String? = entity.contractId
        if (_tmpContractId == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpContractId)
        }
        statement.bindText(14, entity.notes)
        statement.bindText(15, entity.hardwareId)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(16, _tmp.toLong())
        statement.bindLong(17, entity.updatedAt)
        val _tmpImagePath: String? = entity.imagePath
        if (_tmpImagePath == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpImagePath)
        }
        val _tmpImageBase64: String? = entity.imageBase64
        if (_tmpImageBase64 == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpImageBase64)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<Article>() {
      protected override fun createQuery(): String = "UPDATE `articles` SET `id` = ?,`name` = ?,`category` = ?,`status` = ?,`description` = ?,`owner` = ?,`partner` = ?,`quantity` = ?,`location` = ?,`scene` = ?,`costs` = ?,`price` = ?,`contractId` = ?,`notes` = ?,`hardwareId` = ?,`deleted` = ?,`updatedAt` = ?,`imagePath` = ?,`imageBase64` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Article) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, __ArticleCategory_enumToString(entity.category))
        statement.bindText(4, __ArticleStatus_enumToString(entity.status))
        statement.bindText(5, entity.description)
        statement.bindText(6, entity.owner)
        statement.bindText(7, entity.partner)
        statement.bindLong(8, entity.quantity.toLong())
        statement.bindText(9, entity.location)
        statement.bindText(10, entity.scene)
        statement.bindDouble(11, entity.costs)
        statement.bindDouble(12, entity.price)
        val _tmpContractId: String? = entity.contractId
        if (_tmpContractId == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpContractId)
        }
        statement.bindText(14, entity.notes)
        statement.bindText(15, entity.hardwareId)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(16, _tmp.toLong())
        statement.bindLong(17, entity.updatedAt)
        val _tmpImagePath: String? = entity.imagePath
        if (_tmpImagePath == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpImagePath)
        }
        val _tmpImageBase64: String? = entity.imageBase64
        if (_tmpImageBase64 == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpImageBase64)
        }
        statement.bindText(20, entity.id)
      }
    })
  }

  public override suspend fun upsert(article: Article): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __upsertAdapterOfArticle.upsertAndReturnId(_connection, article)
    _result
  }

  public override fun observeAll(): Flow<List<Article>> {
    val _sql: String = "SELECT * FROM articles ORDER BY name COLLATE NOCASE ASC"
    return createFlow(__db, false, arrayOf("articles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfOwner: Int = getColumnIndexOrThrow(_stmt, "owner")
        val _columnIndexOfPartner: Int = getColumnIndexOrThrow(_stmt, "partner")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfScene: Int = getColumnIndexOrThrow(_stmt, "scene")
        val _columnIndexOfCosts: Int = getColumnIndexOrThrow(_stmt, "costs")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfContractId: Int = getColumnIndexOrThrow(_stmt, "contractId")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfHardwareId: Int = getColumnIndexOrThrow(_stmt, "hardwareId")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfImagePath: Int = getColumnIndexOrThrow(_stmt, "imagePath")
        val _columnIndexOfImageBase64: Int = getColumnIndexOrThrow(_stmt, "imageBase64")
        val _result: MutableList<Article> = mutableListOf()
        while (_stmt.step()) {
          val _item: Article
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategory: ArticleCategory
          _tmpCategory = __ArticleCategory_stringToEnum(_stmt.getText(_columnIndexOfCategory))
          val _tmpStatus: ArticleStatus
          _tmpStatus = __ArticleStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpOwner: String
          _tmpOwner = _stmt.getText(_columnIndexOfOwner)
          val _tmpPartner: String
          _tmpPartner = _stmt.getText(_columnIndexOfPartner)
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpScene: String
          _tmpScene = _stmt.getText(_columnIndexOfScene)
          val _tmpCosts: Double
          _tmpCosts = _stmt.getDouble(_columnIndexOfCosts)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpContractId: String?
          if (_stmt.isNull(_columnIndexOfContractId)) {
            _tmpContractId = null
          } else {
            _tmpContractId = _stmt.getText(_columnIndexOfContractId)
          }
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpHardwareId: String
          _tmpHardwareId = _stmt.getText(_columnIndexOfHardwareId)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpImagePath: String?
          if (_stmt.isNull(_columnIndexOfImagePath)) {
            _tmpImagePath = null
          } else {
            _tmpImagePath = _stmt.getText(_columnIndexOfImagePath)
          }
          val _tmpImageBase64: String?
          if (_stmt.isNull(_columnIndexOfImageBase64)) {
            _tmpImageBase64 = null
          } else {
            _tmpImageBase64 = _stmt.getText(_columnIndexOfImageBase64)
          }
          _item = Article(_tmpId,_tmpName,_tmpCategory,_tmpStatus,_tmpDescription,_tmpOwner,_tmpPartner,_tmpQuantity,_tmpLocation,_tmpScene,_tmpCosts,_tmpPrice,_tmpContractId,_tmpNotes,_tmpHardwareId,_tmpDeleted,_tmpUpdatedAt,_tmpImagePath,_tmpImageBase64)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): Article? {
    val _sql: String = "SELECT * FROM articles WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfOwner: Int = getColumnIndexOrThrow(_stmt, "owner")
        val _columnIndexOfPartner: Int = getColumnIndexOrThrow(_stmt, "partner")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfScene: Int = getColumnIndexOrThrow(_stmt, "scene")
        val _columnIndexOfCosts: Int = getColumnIndexOrThrow(_stmt, "costs")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfContractId: Int = getColumnIndexOrThrow(_stmt, "contractId")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfHardwareId: Int = getColumnIndexOrThrow(_stmt, "hardwareId")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfImagePath: Int = getColumnIndexOrThrow(_stmt, "imagePath")
        val _columnIndexOfImageBase64: Int = getColumnIndexOrThrow(_stmt, "imageBase64")
        val _result: Article?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategory: ArticleCategory
          _tmpCategory = __ArticleCategory_stringToEnum(_stmt.getText(_columnIndexOfCategory))
          val _tmpStatus: ArticleStatus
          _tmpStatus = __ArticleStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpOwner: String
          _tmpOwner = _stmt.getText(_columnIndexOfOwner)
          val _tmpPartner: String
          _tmpPartner = _stmt.getText(_columnIndexOfPartner)
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpScene: String
          _tmpScene = _stmt.getText(_columnIndexOfScene)
          val _tmpCosts: Double
          _tmpCosts = _stmt.getDouble(_columnIndexOfCosts)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpContractId: String?
          if (_stmt.isNull(_columnIndexOfContractId)) {
            _tmpContractId = null
          } else {
            _tmpContractId = _stmt.getText(_columnIndexOfContractId)
          }
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpHardwareId: String
          _tmpHardwareId = _stmt.getText(_columnIndexOfHardwareId)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpImagePath: String?
          if (_stmt.isNull(_columnIndexOfImagePath)) {
            _tmpImagePath = null
          } else {
            _tmpImagePath = _stmt.getText(_columnIndexOfImagePath)
          }
          val _tmpImageBase64: String?
          if (_stmt.isNull(_columnIndexOfImageBase64)) {
            _tmpImageBase64 = null
          } else {
            _tmpImageBase64 = _stmt.getText(_columnIndexOfImageBase64)
          }
          _result = Article(_tmpId,_tmpName,_tmpCategory,_tmpStatus,_tmpDescription,_tmpOwner,_tmpPartner,_tmpQuantity,_tmpLocation,_tmpScene,_tmpCosts,_tmpPrice,_tmpContractId,_tmpNotes,_tmpHardwareId,_tmpDeleted,_tmpUpdatedAt,_tmpImagePath,_tmpImageBase64)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM articles WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __ArticleCategory_enumToString(_value: ArticleCategory): String = when (_value) {
    ArticleCategory.WEAPON -> "WEAPON"
    ArticleCategory.ARMOR -> "ARMOR"
    ArticleCategory.DROID -> "DROID"
    ArticleCategory.SHIP_PART -> "SHIP_PART"
    ArticleCategory.SUPPLY -> "SUPPLY"
    ArticleCategory.HOLOCRON -> "HOLOCRON"
    ArticleCategory.MISC -> "MISC"
  }

  private fun __ArticleStatus_enumToString(_value: ArticleStatus): String = when (_value) {
    ArticleStatus.BEREIT -> "BEREIT"
    ArticleStatus.IM_EINSATZ -> "IM_EINSATZ"
    ArticleStatus.BESCHAEDIGT -> "BESCHAEDIGT"
    ArticleStatus.IN_LAGER -> "IN_LAGER"
    ArticleStatus.VERSCHOLLEN -> "VERSCHOLLEN"
  }

  private fun __ArticleCategory_stringToEnum(_value: String): ArticleCategory = when (_value) {
    "WEAPON" -> ArticleCategory.WEAPON
    "ARMOR" -> ArticleCategory.ARMOR
    "DROID" -> ArticleCategory.DROID
    "SHIP_PART" -> ArticleCategory.SHIP_PART
    "SUPPLY" -> ArticleCategory.SUPPLY
    "HOLOCRON" -> ArticleCategory.HOLOCRON
    "MISC" -> ArticleCategory.MISC
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __ArticleStatus_stringToEnum(_value: String): ArticleStatus = when (_value) {
    "BEREIT" -> ArticleStatus.BEREIT
    "IM_EINSATZ" -> ArticleStatus.IM_EINSATZ
    "BESCHAEDIGT" -> ArticleStatus.BESCHAEDIGT
    "IN_LAGER" -> ArticleStatus.IN_LAGER
    "VERSCHOLLEN" -> ArticleStatus.VERSCHOLLEN
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
