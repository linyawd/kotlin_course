package com.example.systemeventsjournal

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BroadcastRepository(
    private val context: Context
) {

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun observeBroadcastEvents(): Flow<BroadcastEvent> = callbackFlow {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_HEADSET_PLUG)
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent == null) return

                val action = intent.action ?: "UNKNOWN_ACTION"
                val event = BroadcastEvent(
                    id = UUID.randomUUID().toString(),
                    type = mapActionToTitle(action),
                    timestamp = formatTimestamp(System.currentTimeMillis()),
                    details = buildDetails(action, intent)
                )

                trySend(event)
            }
        }

        // Обов'язковий прапорець RECEIVER_NOT_EXPORTED для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, intentFilter)
        }

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    private fun mapActionToTitle(action: String): String = when (action) {
        Intent.ACTION_POWER_CONNECTED -> "Зарядний пристрій підключено"
        Intent.ACTION_POWER_DISCONNECTED -> "Зарядний пристрій відключено"
        Intent.ACTION_AIRPLANE_MODE_CHANGED -> "Зміна режиму \"У літаку\""
        Intent.ACTION_BATTERY_LOW -> "Критично низький рівень батареї"
        Intent.ACTION_HEADSET_PLUG -> "Стан аудіогарнітури змінено"
        else -> action.substringAfterLast('.')
    }

    private fun buildDetails(action: String, intent: Intent): String {
        val bundle = intent.extras
        val extrasList = mutableListOf<String>()

        if (bundle != null && !bundle.isEmpty) {
            for (key in bundle.keySet()) {
                @Suppress("DEPRECATION")
                val value = bundle.get(key)
                extrasList.add("• $key = $value")
            }
        }

        val extrasText = if (extrasList.isEmpty()) {
            "Extras: відсутні"
        } else {
            "Extras:\n${extrasList.joinToString("\n")}"
        }

        return "Action: $action\n$extrasText"
    }

    private fun formatTimestamp(millis: Long): String {
        val formatter = SimpleDateFormat("HH:mm:ss.SSS (dd.MM.yyyy)", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}