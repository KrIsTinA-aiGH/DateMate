package com.example.datemate.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.datemate.R
import com.example.datemate.data.ProductEntity
import com.example.datemate.viewmodel.ProductViewModel
import com.example.datemate.viewmodel.SettingsViewModel
import com.example.datemate.viewmodel.SortType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// Структура для категории с иконкой
data class CategoryItem(val name: String, val icon: ImageVector)

// Структура для места хранения с иконкой
data class StoragePlaceItem(val name: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    productViewModel: ProductViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToAddEdit: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToShoppingList: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val products by productViewModel.products.collectAsState()
    val searchQuery by productViewModel.searchQuery.collectAsState()
    val selectedCategory by productViewModel.selectedCategory.collectAsState()
    val selectedStoragePlace by productViewModel.selectedStoragePlace.collectAsState()
    val currentSortType by productViewModel.sortType.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()

    var isCategoryExpanded by remember { mutableStateOf(false) }
    var isStorageExpanded by remember { mutableStateOf(false) } // <-- Для места хранения
    var isSortExpanded by remember { mutableStateOf(false) }
    var isSearchVisible by remember { mutableStateOf(false) }

    val categories = listOf(
        CategoryItem("Все", Icons.Default.List),
        CategoryItem("Молочные продукты", Icons.Default.LocalDrink),
        CategoryItem("Овощи", Icons.Default.Eco),
        CategoryItem("Фрукты", Icons.Default.Grass),
        CategoryItem("Мясо", Icons.Default.KebabDining),
        CategoryItem("Рыба", Icons.Default.SetMeal),
        CategoryItem("Бакалея", Icons.Default.LocalGroceryStore),
        CategoryItem("Напитки", Icons.Default.LocalBar),
        CategoryItem("Заморозка", Icons.Default.AcUnit),
        CategoryItem("Сладости", Icons.Default.Cake),
        CategoryItem("Хлеб", Icons.Default.LunchDining),
        CategoryItem("Соусы", Icons.Default.Water),
        CategoryItem("Другое", Icons.Default.MoreHoriz)
    )

    // Сокращенные названия для компактности в верхней строке
    val storagePlaces = listOf(
        StoragePlaceItem("Все", Icons.Default.List),
        StoragePlaceItem("Холодильник", Icons.Default.Kitchen),
        StoragePlaceItem("Морозильная камера", Icons.Default.AcUnit),
        StoragePlaceItem("Шкаф", Icons.Default.ViewSidebar),
        StoragePlaceItem("Кладовая", Icons.Default.RoomPreferences),
        StoragePlaceItem("Полка", Icons.Default.ViewDay),
        StoragePlaceItem("Подоконник", Icons.Default.Window),
        StoragePlaceItem("Стол", Icons.Default.Desk),
        StoragePlaceItem("Другое", Icons.Default.MoreHoriz)

    )

    Scaffold(
        topBar = {
            Surface(
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon), // Убедись, что имя файла совпадает
                                contentDescription = "Иконка приложения",
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)),
                                tint = Color.Unspecified
                            )
                            Text(text = "Сроки годности", style = MaterialTheme.typography.titleLarge)
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Настройки")
                        }
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            Icon(Icons.Default.Search, contentDescription = "Поиск")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onNavigateToShoppingList, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.List, contentDescription = "Список покупок", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                }
                IconButton(onClick = onNavigateToAddEdit, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить продукт", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            if (isSearchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { productViewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(top = 13.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    placeholder = {
                        Text(
                            "Поиск продуктов...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Цвет placeholder
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant // Цвет иконки лупы
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface // <-- Явный цвет текста
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary // Цвет курсора
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // === ТРИ БЛОКА В ОДНУ ЛИНИЮ ===
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp) // Уменьшенный отступ для 3 блоков
            ) {
                // 1. Блок Категорий
                Box(modifier = Modifier.weight(1f)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        border = BorderStroke(width = 1.dp, color = Color.Black.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { isCategoryExpanded = true }.padding(horizontal = 8.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedCategory == null) "Кат.: Все" else "Кат.: ${selectedCategory?.take(7)}..",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1
                            )
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                    DropdownMenu(expanded = isCategoryExpanded, onDismissRequest = { isCategoryExpanded = false }, modifier = Modifier.width(200.dp)) {
                        categories.forEach { category ->
                            val isSelected = (category.name == "Все" && selectedCategory == null) || (category.name == selectedCategory)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(imageVector = category.icon, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                        Text(text = category.name, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                                    }
                                },
                                onClick = {
                                    if (category.name == "Все") productViewModel.selectCategory(null) else productViewModel.selectCategory(category.name)
                                    isCategoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // 2. Блок Места хранения (НОВЫЙ)
                Box(modifier = Modifier.weight(1f)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        border = BorderStroke(width = 1.dp, color = Color.Black.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { isStorageExpanded = true }.padding(horizontal = 8.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Умное формирование текста без ручного обрезания
                            val displayText = if (selectedStoragePlace == null) "Место: Все" else "Место: $selectedStoragePlace"

                            Text(
                                text = displayText,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,

                            )
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                    DropdownMenu(expanded = isStorageExpanded, onDismissRequest = { isStorageExpanded = false }, modifier = Modifier.width(200.dp)) {
                        storagePlaces.forEach { place ->
                            val isSelected = (place.name == "Все" && selectedStoragePlace == null) || (place.name == selectedStoragePlace)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(imageVector = place.icon, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                        Text(text = place.name, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                                    }
                                },
                                onClick = {
                                    if (place.name == "Все") productViewModel.selectStoragePlace(null)
                                    else productViewModel.selectStoragePlace(place.name)
                                    isStorageExpanded = false
                                }
                            )
                        }
                    }
                }

                // 3. Блок Сортировки
                Box(modifier = Modifier.weight(1f)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        border = BorderStroke(width = 1.dp, color = Color.Black.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { isSortExpanded = true }.padding(horizontal = 8.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val sortText = when(currentSortType) {
                                SortType.BY_DATE -> "По дате"
                                SortType.BY_NAME -> "По имени"
                            }
                            Text(text = sortText, style = MaterialTheme.typography.bodyMedium, maxLines = 1, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.wrapContentWidth())
                        }
                    }
                    DropdownMenu(expanded = isSortExpanded, onDismissRequest = { isSortExpanded = false }, modifier = Modifier.width(200.dp)) {
                        DropdownMenuItem(
                            text = { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp)); Text("Сначала истекающие", style = MaterialTheme.typography.bodyMedium) } },
                            onClick = { productViewModel.setSortType(SortType.BY_DATE); isSortExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { Icon(Icons.Default.SortByAlpha, contentDescription = null, modifier = Modifier.size(18.dp)); Text("По алфавиту", style = MaterialTheme.typography.bodyMedium) } },
                            onClick = { productViewModel.setSortType(SortType.BY_NAME); isSortExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Список продуктов пуст", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(products, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            dateFormat = settings.dateFormat,
                            onEditClick = { onNavigateToEdit(product.id) },
                            onDelete = { productViewModel.deleteProduct(product) }
                        )
                    }
                }
            }
        }
    }
}

// ... (остальная часть файла с ProductItemCard остается без изменений, как в предыдущей версии)
@Composable
fun ProductItemCard(
    product: ProductEntity,
    dateFormat: Int,
    onEditClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val today = LocalDate.now()

    val expiryLocalDate = try {
        LocalDate.parse(product.expiryDate, dbFormatter)
    } catch (e: Exception) {
        try { LocalDate.parse(product.expiryDate, displayFormatter) }
        catch (e2: Exception) { null }
    }

    fun formatDaysPhrase(days: Int): String {
        val absDays = Math.abs(days)
        val lastTwo = absDays % 100
        val lastOne = absDays % 10
        val dayWord = when {
            lastTwo in 11..19 -> "дней"
            lastOne == 1 -> "день"
            lastOne in 2..4 -> "дня"
            else -> "дней"
        }
        val verb = if (lastTwo in 11..19 || lastOne != 1) "Осталось" else "Остался"
        return "$verb $absDays $dayWord"
    }

    val statusText: String
    val statusIcon: String
    val dateColor: Color
    val borderColor: Color

    if (expiryLocalDate == null) {
        statusText = "Годен до: ${product.expiryDate}"
        statusIcon = "❓"
        dateColor = MaterialTheme.colorScheme.onSurface
        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    } else {
        val daysLeft = ChronoUnit.DAYS.between(today, expiryLocalDate).toInt()
        val displayDate = expiryLocalDate.format(displayFormatter)

        when {
            daysLeft < 0 -> {
                val absDays = Math.abs(daysLeft)
                val lastTwo = absDays % 100
                val lastOne = absDays % 10
                val dayWord = when {
                    lastTwo in 11..19 -> "дней"
                    lastOne == 1 -> "день"
                    lastOne in 2..4 -> "дня"
                    else -> "дней"
                }
                statusText = "Просрочено на $absDays $dayWord"
                statusIcon = "🔴"
                dateColor = MaterialTheme.colorScheme.error
                borderColor = MaterialTheme.colorScheme.error
            }
            daysLeft == 0 -> {
                statusText = "Истекает сегодня"
                statusIcon = "⚠️"
                dateColor = Color(0xFFFF9800)
                borderColor = Color(0xFFFF9800)
            }
            else -> {
                when (dateFormat) {
                    0 -> statusText = "Годен до: $displayDate"
                    1 -> statusText = "Годен до: $displayDate (${formatDaysPhrase(daysLeft)})"
                    2 -> statusText = formatDaysPhrase(daysLeft)
                    else -> statusText = "Годен до: $displayDate (${formatDaysPhrase(daysLeft)})"
                }
                if (daysLeft <= 3) {
                    statusIcon = "⚠️"
                    dateColor = Color(0xFFFF9800)
                    borderColor = Color(0xFFFF9800)
                } else {
                    statusIcon = "✅"
                    dateColor = Color(0xFF4CAF50)
                    borderColor = Color(0xFF4CAF50)
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEditClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(width = 1.5.dp, color = borderColor)
    ){
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Категория: ${product.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Место: ${product.storagePlace}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = statusIcon, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(end = 6.dp))
                    Text(text = statusText, style = MaterialTheme.typography.bodyMedium, color = dateColor)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
            }
        }
    }
}