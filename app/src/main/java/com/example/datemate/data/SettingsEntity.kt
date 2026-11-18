package com.example.datemate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Всегда 1, так как настройки у приложения одни

    // Формат отображения даты 
    val dateFormat: Int = 1,

    // За сколько дней до истечения срока отправлять уведомления
    val notificationDays: Int = 3,

    // Время уведомления в формате "HH:MM" (например, "09:00")
    val notificationTime: String = "09:00",

    // Тема приложения
    val theme: Int = 2,

    // ID пользователя Google (если вошел в аккаунт)
    val googleUserId: String? = null,
    val notificationsEnabled: Boolean = true
)