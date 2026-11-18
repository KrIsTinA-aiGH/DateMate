package com.example.datemate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.datemate.ui.screens.AddEditProductScreen
import com.example.datemate.ui.screens.MainScreen
import com.example.datemate.ui.screens.SettingsScreen
import com.example.datemate.ui.screens.ShoppingListScreen
import com.example.datemate.ui.theme.DateMateTheme
import com.example.datemate.viewmodel.ProductViewModel
import com.example.datemate.viewmodel.SettingsViewModel
import com.example.datemate.viewmodel.ShoppingListViewModel
import com.example.datemate.worker.NotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val productViewModel: ProductViewModel by viewModels()
    private val shoppingListViewModel: ShoppingListViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val settings by settingsViewModel.settings.collectAsState()

            LaunchedEffect(settings.notificationTime, settings.notificationsEnabled) {
                scheduleNotificationWorker(
                    settings.notificationTime,
                    settings.notificationsEnabled
                )
            }

            DateMateTheme(themeType = settings.theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        productViewModel = productViewModel,
                        shoppingListViewModel = shoppingListViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }

    private fun scheduleNotificationWorker(notificationTime: String, enabled: Boolean) {
        val workManager = WorkManager.getInstance(applicationContext)

        if (!enabled) {
            workManager.cancelUniqueWork("ExpiryNotificationWork")
            return
        }

        val timeParts = notificationTime.split(":")
        val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 9
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "ExpiryNotificationWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}

@Composable
fun AppNavigation(
    productViewModel: ProductViewModel,
    shoppingListViewModel: ShoppingListViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(
                productViewModel = productViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToAddEdit = { navController.navigate("add_edit_screen") },
                onNavigateToEdit = { productId -> navController.navigate("add_edit_screen/$productId") },
                onNavigateToShoppingList = { navController.navigate("shopping_list_screen") },
                onNavigateToSettings = { navController.navigate("settings_screen") }
            )
        }
        composable("add_edit_screen") {
            AddEditProductScreen(
                viewModel = productViewModel,
                productId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("add_edit_screen/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            AddEditProductScreen(
                viewModel = productViewModel,
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("shopping_list_screen") {
            ShoppingListScreen(
                viewModel = shoppingListViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("settings_screen") {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}