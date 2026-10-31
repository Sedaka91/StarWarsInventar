package com.starwars.inventar.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _articleDao: Lazy<ArticleDao> = lazy {
    ArticleDao_Impl(this)
  }

  private val _contractDao: Lazy<ContractDao> = lazy {
    ContractDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "230210c6cabe8bc0787e54b4e6523364", "9b7ce7818b846f9c7d78acce4f48748d") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `articles` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `status` TEXT NOT NULL, `description` TEXT NOT NULL, `owner` TEXT NOT NULL, `partner` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `location` TEXT NOT NULL, `scene` TEXT NOT NULL, `costs` REAL NOT NULL, `price` REAL NOT NULL, `contractId` TEXT, `notes` TEXT NOT NULL, `hardwareId` TEXT NOT NULL, `deleted` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `imagePath` TEXT, `imageBase64` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`contractId`) REFERENCES `contracts`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_articles_contractId` ON `articles` (`contractId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `contracts` (`id` TEXT NOT NULL, `contractNumber` TEXT NOT NULL, `title` TEXT NOT NULL, `type` TEXT NOT NULL, `status` TEXT NOT NULL, `owner` TEXT NOT NULL, `partner` TEXT NOT NULL, `startDate` INTEGER, `endDate` INTEGER, `costs` REAL NOT NULL, `price` REAL NOT NULL, `deposit` REAL NOT NULL, `signed` INTEGER NOT NULL, `terms` TEXT NOT NULL, `notes` TEXT NOT NULL, `hardwareId` TEXT NOT NULL, `deleted` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '230210c6cabe8bc0787e54b4e6523364')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `articles`")
        connection.execSQL("DROP TABLE IF EXISTS `contracts`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsArticles: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsArticles.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("category", TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("status", TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("description", TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("owner", TableInfo.Column("owner", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("partner", TableInfo.Column("partner", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("quantity", TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("location", TableInfo.Column("location", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("scene", TableInfo.Column("scene", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("costs", TableInfo.Column("costs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("price", TableInfo.Column("price", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("contractId", TableInfo.Column("contractId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("hardwareId", TableInfo.Column("hardwareId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("imagePath", TableInfo.Column("imagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("imageBase64", TableInfo.Column("imageBase64", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysArticles: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysArticles.add(TableInfo.ForeignKey("contracts", "SET NULL", "NO ACTION", listOf("contractId"), listOf("id")))
        val _indicesArticles: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesArticles.add(TableInfo.Index("index_articles_contractId", false, listOf("contractId"), listOf("ASC")))
        val _infoArticles: TableInfo = TableInfo("articles", _columnsArticles, _foreignKeysArticles, _indicesArticles)
        val _existingArticles: TableInfo = read(connection, "articles")
        if (!_infoArticles.equals(_existingArticles)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |articles(com.starwars.inventar.data.Article).
              | Expected:
              |""".trimMargin() + _infoArticles + """
              |
              | Found:
              |""".trimMargin() + _existingArticles)
        }
        val _columnsContracts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsContracts.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("contractNumber", TableInfo.Column("contractNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("type", TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("status", TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("owner", TableInfo.Column("owner", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("partner", TableInfo.Column("partner", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("startDate", TableInfo.Column("startDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("endDate", TableInfo.Column("endDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("costs", TableInfo.Column("costs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("price", TableInfo.Column("price", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("deposit", TableInfo.Column("deposit", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("signed", TableInfo.Column("signed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("terms", TableInfo.Column("terms", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("hardwareId", TableInfo.Column("hardwareId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysContracts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesContracts: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoContracts: TableInfo = TableInfo("contracts", _columnsContracts, _foreignKeysContracts, _indicesContracts)
        val _existingContracts: TableInfo = read(connection, "contracts")
        if (!_infoContracts.equals(_existingContracts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |contracts(com.starwars.inventar.data.Contract).
              | Expected:
              |""".trimMargin() + _infoContracts + """
              |
              | Found:
              |""".trimMargin() + _existingContracts)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "articles", "contracts")
  }

  public override fun clearAllTables() {
    super.performClear(true, "articles", "contracts")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(ArticleDao::class, ArticleDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ContractDao::class, ContractDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun articleDao(): ArticleDao = _articleDao.value

  public override fun contractDao(): ContractDao = _contractDao.value
}
