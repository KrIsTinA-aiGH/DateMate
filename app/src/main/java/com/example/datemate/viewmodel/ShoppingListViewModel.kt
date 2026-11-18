package com.example.datemate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.datemate.data.AppDatabase
import com.example.datemate.data.ShoppingItem
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).shoppingListDao()

    // Получение списка покупок
    val shoppingItems: StateFlow<List<ShoppingItem>> = dao.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Добавление нового товара в список
    fun addItem(name: String) {
        viewModelScope.launch {
            dao.insertItem(ShoppingItem(name = name.trim(), isChecked = false))
        }
    }

    // Переключение статуса (куплено/не куплено)
    fun toggleItem(item: ShoppingItem) {
        viewModelScope.launch {
            dao.updateItem(item.copy(isChecked = !item.isChecked))
        }
    }

    // Удаление товара из списка
    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            dao.deleteItem(item)
        }
    }
}