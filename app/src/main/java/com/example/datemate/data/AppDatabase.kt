package com.example.datemate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductEntity::class,
        ShoppingItem::class,
        SettingsEntity::class,
        ProductHistoryEntity::class // <-- Обязательно добавлено сюда
    ],
    version = 4, // Версия увеличена для миграции
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun settingsDao(): SettingsDao
    abstract fun productHistoryDao(): ProductHistoryDao // <-- Добавлен DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "datemate_database"
                )
                    .fallbackToDestructiveMigration() // Автоматически пересоздаст базу при смене версии
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}