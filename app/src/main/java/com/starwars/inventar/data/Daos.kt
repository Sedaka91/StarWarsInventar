package com.starwars.inventar.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getById(id: String): Article?

    @Upsert
    suspend fun upsert(article: Article): Long

    @Query("DELETE FROM articles WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface ContractDao {
    @Query("SELECT * FROM contracts ORDER BY title COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<Contract>>

    @Query("SELECT * FROM contracts WHERE id = :id")
    suspend fun getById(id: String): Contract?

    @Upsert
    suspend fun upsert(contract: Contract): Long

    @Query("DELETE FROM contracts WHERE id = :id")
    suspend fun deleteById(id: String)
}
