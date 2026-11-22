package com.example.privatbankcurrency

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

class ExchangeRatesSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val strictFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
        .withResolverStyle(ResolverStyle.STRICT)

    private val displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override suspend fun doWork(): Result {
        val requestedDate = inputData.getString(ExchangeSyncScheduler.KEY_DATE)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: LocalDate.now().format(displayFormatter)

        val parsedDate = try {
            LocalDate.parse(requestedDate, strictFormatter)
        } catch (_: Exception) {
            return Result.failure(
                workDataOf(ExchangeSyncScheduler.KEY_MESSAGE to "Некоректна дата запиту")
            )
        }

        if (parsedDate.isAfter(LocalDate.now()) || parsedDate.isBefore(LocalDate.now().minusYears(4))) {
            return Result.failure(
                workDataOf(ExchangeSyncScheduler.KEY_MESSAGE to "Дата поза доступним діапазоном архіву")
            )
        }

        return try {
            val response = RetrofitClient.api.getExchangeRates(requestedDate)
            if (!response.isSuccessful) {
                return if (response.code() in 500..599 || response.code() == 429) {
                    Result.retry()
                } else {
                    Result.failure(
                        workDataOf(ExchangeSyncScheduler.KEY_MESSAGE to "Помилка сервера: HTTP ${response.code()}")
                    )
                }
            }

            val body = response.body()
                ?: return Result.failure(
                    workDataOf(ExchangeSyncScheduler.KEY_MESSAGE to "Порожня відповідь сервера")
                )

            val responseDate = body.date?.trim().orEmpty()
            if (responseDate.isNotEmpty() && responseDate != requestedDate) {
                return Result.failure(
                    workDataOf(ExchangeSyncScheduler.KEY_MESSAGE to "Дата відповіді не збігається із запитаною")
                )
            }

            val effectiveDate = responseDate.ifEmpty { requestedDate }
            val entities = body.exchangeRate
                .filter { !it.currency.isNullOrBlank() }
                .map { item ->
                    ExchangeRateEntity(
                        date = effectiveDate,
                        currency = item.currency!!.trim(),
                        baseCurrency = item.baseCurrency?.trim()?.takeIf { it.isNotEmpty() } ?: "UAH",
                        purchaseRate = item.purchaseRate,
                        saleRate = item.saleRate
                    )
                }

            val dao = ExchangeDatabase.getInstance(applicationContext).exchangeRateDao()
            dao.upsertRates(entities)

            if (entities.isEmpty()) {
                Result.success(
                    workDataOf(ExchangeSyncScheduler.KEY_MESSAGE to "Сервер не повернув курсів")
                )
            } else {
                Result.success(
                    workDataOf(ExchangeSyncScheduler.KEY_MESSAGE to "Курси за $effectiveDate оновлено")
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: IOException) {
            Result.retry()
        } catch (_: Exception) {
            Result.failure(
                workDataOf(ExchangeSyncScheduler.KEY_MESSAGE to "Непередбачена помилка синхронізації")
            )
        }
    }
}