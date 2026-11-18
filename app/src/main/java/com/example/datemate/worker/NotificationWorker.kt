package com.example.datemate.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.datemate.data.AppDatabase
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val productDao = db.productDao()
        val settingsDao = db.settingsDao()

        // 1. Проверяем, включены ли уведомления в настройках
        val settings = try {
            settingsDao.getSettings().first()
        } catch (e: Exception) {
            null
        }

        // Если настройки не найдены или уведомления выключены — выходим
        if (settings == null || !settings.notificationsEnabled) {
            return Result.success()
        }

        // 2. Получаем количество дней из настроек (за сколько дней напоминать)
        val reminderDays = settings.notificationDays

        // 3. Получаем список всех продуктов
        val products = productDao.getAllProducts().first()
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val expiringProducts = mutableListOf<String>()

        for (product in products) {
            try {
                val expiryDate = LocalDate.parse(product.expiryDate, formatter)
                val daysLeft = ChronoUnit.DAYS.between(today, expiryDate).toInt()

                // Если срок годности истекает в течение выбранного количества дней (включая сегодня)
                if (daysLeft in 0..reminderDays) {
                    expiringProducts.add(product.name)
                }
            } catch (e: Exception) {
                // Игнорируем ошибки, если дата записана в неверном формате
            }
        }

        // 4. Если есть продукты, которые скоро испортятся, отправляем уведомление
        if (expiringProducts.isNotEmpty()) {
            sendNotification(expiringProducts)
        }

        return Result.success()
    }

    private fun sendNotification(products: List<String>) {
        val channelId = "expiry_channel"
        val channelName = "Напоминания о сроках годности"

        // Создание канала уведомлений (обязательно для Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val message = "Скоро истекает срок годности: ${products.joinToString(", ")}"

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("DateMate")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Уведомление исчезнет после нажатия
            .build()

        try {
            NotificationManagerCompat.from(applicationContext).notify(1, notification)
        } catch (e: SecurityException) {
            // Если на Android 13+ не выдано разрешение на уведомления
        }
    }
}