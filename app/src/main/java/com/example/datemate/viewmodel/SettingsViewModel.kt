package com.example.datemate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.datemate.data.AppDatabase
import com.example.datemate.data.SettingsEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).settingsDao()

    // Реактивный поток настроек
    val settings: StateFlow<SettingsEntity> = dao.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsEntity() // Значения по умолчанию, пока база не отдаст данные
        )

    init {
        // При запуске пытаемся вставить настройки по умолчанию (если их еще нет)
        viewModelScope.launch {
            dao.insertDefaultSettings(SettingsEntity())
        }
    }

    // Методы для обновления отдельных полей
    fun updateDateFormat(format: Int) {
        viewModelScope.launch { dao.updateDateFormat(format) }
    }

    fun updateNotificationDays(days: Int) {
        viewModelScope.launch { dao.updateNotificationDays(days) }
    }

    fun updateNotificationTime(time: String) {
        viewModelScope.launch { dao.updateNotificationTime(time) }
    }

    fun updateTheme(theme: Int) {
        viewModelScope.launch { dao.updateTheme(theme) }
    }

    fun updateGoogleUserId(userId: String?) {
        viewModelScope.launch { dao.updateGoogleUserId(userId) }
    }

    // Обновление состояния уведомлений (включено/выключено)
    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dao.updateNotificationsEnabled(enabled)
        }
    }
}