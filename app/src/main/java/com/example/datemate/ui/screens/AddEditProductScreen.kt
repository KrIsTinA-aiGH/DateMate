package com.example.datemate.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.datemate.viewmodel.ProductViewModel
import java.util.Calendar

// Структура для категории с иконкой
data class CategoryOption(val name: String, val icon: ImageVector)

// Структура для места хранения с иконкой
data class StoragePlaceOption(val name: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    viewModel: ProductViewModel,
    productId: Long? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()

    // Ищем продукт в базе, если открыли экран редактирования
    val existingProduct = remember(productId, products) {
        products.find { it.id == productId }
    }

    var name by remember { mutableStateOf(existingProduct?.name ?: "") }
    var category by remember { mutableStateOf(existingProduct?.category ?: "Молочные продукты") }
    var storagePlace by remember { mutableStateOf(existingProduct?.storagePlace ?: "Холодильник") }
    var expiryDate by remember { mutableStateOf(existingProduct?.expiryDate ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Расширенный список категорий с иконками
    // Расширенный список категорий с иконками
    val categories = listOf(
        CategoryOption("Молочные продукты", Icons.Default.LocalDrink),
        CategoryOption("Овощи", Icons.Default.Eco),
        CategoryOption("Фрукты", Icons.Default.Grass),
        CategoryOption("Мясо", Icons.Default.KebabDining),
        CategoryOption("Рыба", Icons.Default.SetMeal),
        CategoryOption("Бакалея", Icons.Default.Grass),
        CategoryOption("Напитки", Icons.Default.LocalBar),
        CategoryOption("Заморозка", Icons.Default.AcUnit),
        CategoryOption("Сладости", Icons.Default.Cake),
        CategoryOption("Хлеб", Icons.Default.LunchDining),
        CategoryOption("Соусы", Icons.Default.Water),
        CategoryOption("Другое", Icons.Default.MoreHoriz)
    )

    // Расширенный список мест хранения с иконками
    val storagePlaces = listOf(
        StoragePlaceOption("Холодильник", Icons.Default.Kitchen),
        StoragePlaceOption("Морозильная камера", Icons.Default.AcUnit),
        StoragePlaceOption("Шкаф", Icons.Default.ViewSidebar),
        StoragePlaceOption("Кладовая", Icons.Default.RoomPreferences),
        StoragePlaceOption("Полка", Icons.Default.ViewDay),
        StoragePlaceOption("Подоконник", Icons.Default.Window),
        StoragePlaceOption("Стол", Icons.Default.Desk),
        StoragePlaceOption("Другое", Icons.Default.MoreHoriz)
    )

    var categoryExpanded by remember { mutableStateOf(false) }
    var storageExpanded by remember { mutableStateOf(false) }

    val showDatePicker = {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)
                expiryDate = "$selectedYear-$formattedMonth-$formattedDay"
                errorMessage = null
            },
            year,
            month,
            day
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingProduct != null) "Редактировать продукт" else "Добавить продукт") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Название продукта
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = null
                },
                label = { Text("Название продукта*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // Выбор категории с иконками
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = if (item.name == category) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = item.name,
                                        color = if (item.name == category) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            },
                            onClick = {
                                category = item.name
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Выбор места хранения с иконками
            ExposedDropdownMenuBox(
                expanded = storageExpanded,
                onExpandedChange = { storageExpanded = !storageExpanded }
            ) {
                OutlinedTextField(
                    value = storagePlace,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Место хранения") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = storageExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(
                    expanded = storageExpanded,
                    onDismissRequest = { storageExpanded = false }
                ) {
                    storagePlaces.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = if (item.name == storagePlace) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = item.name,
                                        color = if (item.name == storagePlace) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            },
                            onClick = {
                                storagePlace = item.name
                                storageExpanded = false
                            }
                        )
                    }
                }
            }

            // Срок годности с календарем
            OutlinedTextField(
                value = expiryDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Срок годности*") },
                placeholder = { Text("Нажмите, чтобы выбрать дату") },
                trailingIcon = {
                    IconButton(onClick = showDatePicker) {
                        Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату из календаря")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showDatePicker() },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка сохранения
            Button(
                onClick = {
                    if (name.isBlank() || expiryDate.isBlank()) {
                        errorMessage = "Пожалуйста, введите название и выберите срок годности"
                    } else {
                        if (existingProduct != null) {
                            // Редактирование
                            val updated = existingProduct.copy(
                                name = name.trim(),
                                category = category,
                                storagePlace = storagePlace,
                                expiryDate = expiryDate
                            )
                            viewModel.updateProduct(updated)
                        } else {
                            // Создание нового
                            viewModel.addProduct(
                                name = name.trim(),
                                category = category,
                                storagePlace = storagePlace,
                                expiryDate = expiryDate
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    if (existingProduct != null) "Сохранить изменения" else "Сохранить продукт",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}