package com.example.datemate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.datemate.data.AppDatabase
import com.example.datemate.data.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortType {
    BY_DATE,            // Сначала истекающие
    BY_NAME            // По алфавиту

}

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).productDao()

    // Состояния фильтров и сортировки
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>(null)
    val selectedStoragePlace = MutableStateFlow<String?>(null)
    val sortType = MutableStateFlow(SortType.BY_DATE)

    // Основной поток: берём все продукты из БД и применяем ВСЕ фильтры + сортировку
    val products: StateFlow<List<ProductEntity>> = combine(
        dao.getAllProducts(),
        searchQuery,
        selectedCategory,
        selectedStoragePlace,
        sortType
    ) { allProducts, query, category, place, sort ->
        var filtered = allProducts

        // Поиск по названию
        if (query.isNotBlank()) {
            filtered = filtered.filter { it.name.contains(query, ignoreCase = true) }
        }

        // Фильтр по категории
        if (!category.isNullOrEmpty()) {
            filtered = filtered.filter { it.category == category }
        }

        // Фильтр по месту хранения
        if (!place.isNullOrEmpty()) {
            filtered = filtered.filter { it.storagePlace == place }
        }

        // Сортировка
        when (sort) {
            SortType.BY_DATE -> filtered.sortedBy { it.expiryDate }
            SortType.BY_NAME -> filtered.sortedBy { it.name.lowercase() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Добавление нового продукта
    fun addProduct(
        name: String,
        category: String,
        storagePlace: String,
        expiryDate: String
    ) {
        viewModelScope.launch {
            val newProduct = ProductEntity(
                name = name,
                category = category,
                storagePlace = storagePlace,
                expiryDate = expiryDate
            )
            dao.insertProduct(newProduct)
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch { dao.updateProduct(product) }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch { dao.deleteProduct(product) }
    }

    fun selectCategory(category: String?) {
        selectedCategory.value = category
    }

    fun selectStoragePlace(place: String?) {
        selectedStoragePlace.value = place
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun setSortType(type: SortType) {
        sortType.value = type
    }
}