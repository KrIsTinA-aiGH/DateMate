package com.example.datemate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_history")
data class ProductHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val storagePlace: String
)