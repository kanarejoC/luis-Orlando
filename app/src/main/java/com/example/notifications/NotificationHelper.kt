package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.NotificationType
import com.example.data.model.UserRole

object NotificationHelper {

    private const val CHANNEL_PAYMENTS_ID = "channel_rental_payments"
    private const val CHANNEL_PAYMENTS_NAME = "Cobros y Pagos de Arriendo"

    private const val CHANNEL_CONTRACTS_ID = "channel_rental_contracts"
    private const val CHANNEL_CONTRACTS_NAME = "Vigencia y Renovación de Contratos"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val paymentsChannel = NotificationChannel(
                CHANNEL_PAYMENTS_ID,
                CHANNEL_PAYMENTS_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones y recordatorios mensuales de pago de canon para arrendador y arrendatario"
                enableVibration(true)
            }

            val contractsChannel = NotificationChannel(
                CHANNEL_CONTRACTS_ID,
                CHANNEL_CONTRACTS_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas de vigencia, plazos y renovación de contratos de arriendo"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(paymentsChannel)
            notificationManager.createNotificationChannel(contractsChannel)
        }
    }

    fun showSystemNotification(
        context: Context,
        title: String,
        message: String,
        type: NotificationType,
        targetRole: UserRole,
        notificationId: Int = (System.currentTimeMillis() % 100000).toInt()
    ) {
        val channelId = when (type) {
            NotificationType.PAGO_PENDIENTE, NotificationType.PAGO_RECIBIDO -> CHANNEL_PAYMENTS_ID
            NotificationType.CONTRATO_VIGENCIA, NotificationType.RENOVACION_AVISO, NotificationType.FIRMA_SOLICITADA -> CHANNEL_CONTRACTS_ID
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val rolePrefix = when (targetRole) {
            UserRole.ARRENDATARIO -> "📱 [Aviso Inquilino] "
            UserRole.ARRENDADOR -> "🏢 [Aviso Propietario] "
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.img_app_icon)
            .setContentTitle(rolePrefix + title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled when user didn't grant POST_NOTIFICATIONS permission
        }
    }
}
