package com.example.datemate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductHistoryDao {

    // Получить все уникальные названия продуктов для автодополнения
    @Query("SELECT DISTINCT name FROM product_history ORDER BY name ASC")
    fun getAllProductNames(): Flow<List<String>>

    // Получить продукт по названию (для быстрого заполнения полей)
    @Query("SELECT * FROM product_history WHERE name = :name LIMIT 1")
    suspend fun getProductByName(name: String): ProductHistoryEntity?

    // Добавить продукт в историю (если такого названия ещё нет)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProductHistory(product: ProductHistoryEntity)

    // Получить все записи истории
    @Query("SELECT * FROM product_history ORDER BY name ASC")
    fun getAllHistory(): Flow<List<ProductHistoryEntity>>
}