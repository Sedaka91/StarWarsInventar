package com.starwars.inventar.data

import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val articleDao: ArticleDao,
    private val contractDao: ContractDao
) {
    val articles: Flow<List<Article>> = articleDao.observeAll()
    val contracts: Flow<List<Contract>> = contractDao.observeAll()

}
