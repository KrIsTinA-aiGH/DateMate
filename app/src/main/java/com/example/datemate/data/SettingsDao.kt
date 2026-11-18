package com.example.datemate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    // Получение настроек (у нас всегда одна запись с id = 1)
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<SettingsEntity>

    // Вставка настроек (если запись уже есть — заменяем)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity)

    // Обновление формата даты
    @Query("UPDATE settings SET dateFormat = :format WHERE id = 1")
    suspend fun updateDateFormat(format: Int)

    // Обновление дней уведомления
    @Query("UPDATE settings SET notificationDays = :days WHERE id = 1")
    suspend fun updateNotificationDays(days: Int)

    // Обновление времени уведомления
    @Query("UPDATE settings SET notificationTime = :time WHERE id = 1")
    suspend fun updateNotificationTime(time: String)

    // Обновление темы
    @Query("UPDATE settings SET theme = :theme WHERE id = 1")
    suspend fun updateTheme(theme: Int)

    // Обновление Google ID пользователя
    @Query("UPDATE settings SET googleUserId = :userId WHERE id = 1")
    suspend fun updateGoogleUserId(userId: String?)

    // Инициализация настроек по умолчанию (вызывается один раз при первом запуске)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultSettings(settings: SettingsEntity)

    // Обновление состояния уведомлений (включены/выключены)
    @Query("UPDATE settings SET notificationsEnabled = :enabled WHERE id = 1")
    suspend fun updateNotificationsEnabled(enabled: Boolean)
}