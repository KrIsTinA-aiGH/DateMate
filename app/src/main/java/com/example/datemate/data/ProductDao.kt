package com.example.datemate.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // Получение всего списка продуктов (реактивный поток через Flow)
    @Query("SELECT * FROM products ORDER BY expiryDate ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    // Вставка нового продукта
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    // Обновление существующего продукта
    @Update
    suspend fun updateProduct(product: ProductEntity)


    // Удаление продукта
    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    // Выборка продуктов по категории
    @Query("SELECT * FROM products WHERE category = :categoryQuery ORDER BY expiryDate ASC")
    fun getProductsByCategory(categoryQuery: String): Flow<List<ProductEntity>>

    // Поиск продуктов по названию (оператор LIKE)
    @Query("SELECT * FROM products WHERE name LIKE '%' || :searchQuery || '%' ORDER BY expiryDate ASC")
    fun searchProducts(searchQuery: String): Flow<List<ProductEntity>>
}