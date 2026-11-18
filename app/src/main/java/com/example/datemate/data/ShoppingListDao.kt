package com.example.datemate.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    // Получение списка покупок (сначала неотмеченные, потом отмеченные, внутри - по алфавиту)
    @Query("SELECT * FROM shopping_list ORDER BY isChecked ASC, name ASC")
    fun getAllItems(): Flow<List<ShoppingItem>>

    // Добавление нового товара в список
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItem)

    // Обновление товара (например, чтобы поставить/снять галочку)
    @Update
    suspend fun updateItem(item: ShoppingItem)

    // Удаление товара из списка
    @Delete
    suspend fun deleteItem(item: ShoppingItem)
}