package com.example.datemate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,          // Название товара, который нужно купить
    val isChecked: Boolean = false
)