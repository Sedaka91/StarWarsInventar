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
public class ContractDao_Impl(
  __db: RoomDatabase,
) : ContractDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfContract: EntityUpsertAdapter<Contract>
  init {
    this.__db = __db
    this.__upsertAdapterOfContract = EntityUpsertAdapter<Contract>(object : EntityInsertAdapter<Contract>() {
      protected override fun createQuery(): String = "INSERT INTO `contracts` (`id`,`contractNumber`,`title`,`type`,`status`,`owner`,`partner`,`startDate`,`endDate`,`costs`,`price`,`deposit`,`signed`,`terms`,`notes`,`hardwareId`,`deleted`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Contract) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.contractNumber)
        statement.bindText(3, entity.title)
        statement.bindText(4, __ContractType_enumToString(entity.type))
        statement.bindText(5, __ContractStatus_enumToString(entity.status))
        statement.bindText(6, entity.owner)
        statement.bindText(7, entity.partner)
        val _tmpStartDate: Long? = entity.startDate
        if (_tmpStartDate == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpStartDate)
        }
        val _tmpEndDate: Long? = entity.endDate
        if (_tmpEndDate == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpEndDate)
        }
        statement.bindDouble(10, entity.costs)
        statement.bindDouble(11, entity.price)
        statement.bindDouble(12, entity.deposit)
        val _tmp: Int = if (entity.signed) 1 else 0
        statement.bindLong(13, _tmp.toLong())
        statement.bindText(14, entity.terms)
        statement.bindText(15, entity.notes)
        statement.bindText(16, entity.hardwareId)
        val _tmp_1: Int = if (entity.deleted) 1 else 0
        statement.bindLong(17, _tmp_1.toLong())
        statement.bindLong(18, entity.updatedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<Contract>() {
      protected override fun createQuery(): String = "UPDATE `contracts` SET `id` = ?,`contractNumber` = ?,`title` = ?,`type` = ?,`status` = ?,`owner` = ?,`partner` = ?,`startDate` = ?,`endDate` = ?,`costs` = ?,`price` = ?,`deposit` = ?,`signed` = ?,`terms` = ?,`notes` = ?,`hardwareId` = ?,`deleted` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Contract) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.contractNumber)
        statement.bindText(3, entity.title)
        statement.bindText(4, __ContractType_enumToString(entity.type))
        statement.bindText(5, __ContractStatus_enumToString(entity.status))
        statement.bindText(6, entity.owner)
        statement.bindText(7, entity.partner)
        val _tmpStartDate: Long? = entity.startDate
        if (_tmpStartDate == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpStartDate)
        }
        val _tmpEndDate: Long? = entity.endDate
        if (_tmpEndDate == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpEndDate)
        }
        statement.bindDouble(10, entity.costs)
        statement.bindDouble(11, entity.price)
        statement.bindDouble(12, entity.deposit)
        val _tmp: Int = if (entity.signed) 1 else 0
        statement.bindLong(13, _tmp.toLong())
        statement.bindText(14, entity.terms)
        statement.bindText(15, entity.notes)
        statement.bindText(16, entity.hardwareId)
        val _tmp_1: Int = if (entity.deleted) 1 else 0
        statement.bindLong(17, _tmp_1.toLong())
        statement.bindLong(18, entity.updatedAt)
        statement.bindText(19, entity.id)
      }
    })
  }

  public override suspend fun upsert(contract: Contract): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __upsertAdapterOfContract.upsertAndReturnId(_connection, contract)
    _result
  }

  public override fun observeAll(): Flow<List<Contract>> {
    val _sql: String = "SELECT * FROM contracts ORDER BY title COLLATE NOCASE ASC"
    return createFlow(__db, false, arrayOf("contracts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfContractNumber: Int = getColumnIndexOrThrow(_stmt, "contractNumber")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOwner: Int = getColumnIndexOrThrow(_stmt, "owner")
        val _columnIndexOfPartner: Int = getColumnIndexOrThrow(_stmt, "partner")
        val _columnIndexOfStartDate: Int = getColumnIndexOrThrow(_stmt, "startDate")
        val _columnIndexOfEndDate: Int = getColumnIndexOrThrow(_stmt, "endDate")
        val _columnIndexOfCosts: Int = getColumnIndexOrThrow(_stmt, "costs")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfDeposit: Int = getColumnIndexOrThrow(_stmt, "deposit")
        val _columnIndexOfSigned: Int = getColumnIndexOrThrow(_stmt, "signed")
        val _columnIndexOfTerms: Int = getColumnIndexOrThrow(_stmt, "terms")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfHardwareId: Int = getColumnIndexOrThrow(_stmt, "hardwareId")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<Contract> = mutableListOf()
        while (_stmt.step()) {
          val _item: Contract
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpContractNumber: String
          _tmpContractNumber = _stmt.getText(_columnIndexOfContractNumber)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpType: ContractType
          _tmpType = __ContractType_stringToEnum(_stmt.getText(_columnIndexOfType))
          val _tmpStatus: ContractStatus
          _tmpStatus = __ContractStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpOwner: String
          _tmpOwner = _stmt.getText(_columnIndexOfOwner)
          val _tmpPartner: String
          _tmpPartner = _stmt.getText(_columnIndexOfPartner)
          val _tmpStartDate: Long?
          if (_stmt.isNull(_columnIndexOfStartDate)) {
            _tmpStartDate = null
          } else {
            _tmpStartDate = _stmt.getLong(_columnIndexOfStartDate)
          }
          val _tmpEndDate: Long?
          if (_stmt.isNull(_columnIndexOfEndDate)) {
            _tmpEndDate = null
          } else {
            _tmpEndDate = _stmt.getLong(_columnIndexOfEndDate)
          }
          val _tmpCosts: Double
          _tmpCosts = _stmt.getDouble(_columnIndexOfCosts)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpDeposit: Double
          _tmpDeposit = _stmt.getDouble(_columnIndexOfDeposit)
          val _tmpSigned: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSigned).toInt()
          _tmpSigned = _tmp != 0
          val _tmpTerms: String
          _tmpTerms = _stmt.getText(_columnIndexOfTerms)
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpHardwareId: String
          _tmpHardwareId = _stmt.getText(_columnIndexOfHardwareId)
          val _tmpDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp_1 != 0
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = Contract(_tmpId,_tmpContractNumber,_tmpTitle,_tmpType,_tmpStatus,_tmpOwner,_tmpPartner,_tmpStartDate,_tmpEndDate,_tmpCosts,_tmpPrice,_tmpDeposit,_tmpSigned,_tmpTerms,_tmpNotes,_tmpHardwareId,_tmpDeleted,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): Contract? {
    val _sql: String = "SELECT * FROM contracts WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfContractNumber: Int = getColumnIndexOrThrow(_stmt, "contractNumber")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOwner: Int = getColumnIndexOrThrow(_stmt, "owner")
        val _columnIndexOfPartner: Int = getColumnIndexOrThrow(_stmt, "partner")
        val _columnIndexOfStartDate: Int = getColumnIndexOrThrow(_stmt, "startDate")
        val _columnIndexOfEndDate: Int = getColumnIndexOrThrow(_stmt, "endDate")
        val _columnIndexOfCosts: Int = getColumnIndexOrThrow(_stmt, "costs")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfDeposit: Int = getColumnIndexOrThrow(_stmt, "deposit")
        val _columnIndexOfSigned: Int = getColumnIndexOrThrow(_stmt, "signed")
        val _columnIndexOfTerms: Int = getColumnIndexOrThrow(_stmt, "terms")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfHardwareId: Int = getColumnIndexOrThrow(_stmt, "hardwareId")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: Contract?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpContractNumber: String
          _tmpContractNumber = _stmt.getText(_columnIndexOfContractNumber)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpType: ContractType
          _tmpType = __ContractType_stringToEnum(_stmt.getText(_columnIndexOfType))
          val _tmpStatus: ContractStatus
          _tmpStatus = __ContractStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpOwner: String
          _tmpOwner = _stmt.getText(_columnIndexOfOwner)
          val _tmpPartner: String
          _tmpPartner = _stmt.getText(_columnIndexOfPartner)
          val _tmpStartDate: Long?
          if (_stmt.isNull(_columnIndexOfStartDate)) {
            _tmpStartDate = null
          } else {
            _tmpStartDate = _stmt.getLong(_columnIndexOfStartDate)
          }
          val _tmpEndDate: Long?
          if (_stmt.isNull(_columnIndexOfEndDate)) {
            _tmpEndDate = null
          } else {
            _tmpEndDate = _stmt.getLong(_columnIndexOfEndDate)
          }
          val _tmpCosts: Double
          _tmpCosts = _stmt.getDouble(_columnIndexOfCosts)
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpDeposit: Double
          _tmpDeposit = _stmt.getDouble(_columnIndexOfDeposit)
          val _tmpSigned: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSigned).toInt()
          _tmpSigned = _tmp != 0
          val _tmpTerms: String
          _tmpTerms = _stmt.getText(_columnIndexOfTerms)
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpHardwareId: String
          _tmpHardwareId = _stmt.getText(_columnIndexOfHardwareId)
          val _tmpDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp_1 != 0
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = Contract(_tmpId,_tmpContractNumber,_tmpTitle,_tmpType,_tmpStatus,_tmpOwner,_tmpPartner,_tmpStartDate,_tmpEndDate,_tmpCosts,_tmpPrice,_tmpDeposit,_tmpSigned,_tmpTerms,_tmpNotes,_tmpHardwareId,_tmpDeleted,_tmpUpdatedAt)
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
    val _sql: String = "DELETE FROM contracts WHERE id = ?"
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

  private fun __ContractType_enumToString(_value: ContractType): String = when (_value) {
    ContractType.HANDEL -> "HANDEL"
    ContractType.KAUF -> "KAUF"
    ContractType.BESCHAFFUNG -> "BESCHAFFUNG"
    ContractType.KOPFGELD -> "KOPFGELD"
    ContractType.GEHEIM -> "GEHEIM"
  }

  private fun __ContractStatus_enumToString(_value: ContractStatus): String = when (_value) {
    ContractStatus.ENTWURF -> "ENTWURF"
    ContractStatus.LAUFEND -> "LAUFEND"
    ContractStatus.ERFUELLT -> "ERFUELLT"
    ContractStatus.GESCHEITERT -> "GESCHEITERT"
  }

  private fun __ContractType_stringToEnum(_value: String): ContractType = when (_value) {
    "HANDEL" -> ContractType.HANDEL
    "KAUF" -> ContractType.KAUF
    "BESCHAFFUNG" -> ContractType.BESCHAFFUNG
    "KOPFGELD" -> ContractType.KOPFGELD
    "GEHEIM" -> ContractType.GEHEIM
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __ContractStatus_stringToEnum(_value: String): ContractStatus = when (_value) {
    "ENTWURF" -> ContractStatus.ENTWURF
    "LAUFEND" -> ContractStatus.LAUFEND
    "ERFUELLT" -> ContractStatus.ERFUELLT
    "GESCHEITERT" -> ContractStatus.GESCHEITERT
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
