package com.example.datemate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,              // Название продукта
    val category: String,          // Категория
    val storagePlace: String,      // Место хранения
    val expiryDate: String         //дата окончания
)