package com.example.dice

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.random.Random

class DiceModel {

    suspend fun rollSingleDice(onTick: (Int) -> Unit): Int {
        val iterations = Random.nextInt(15, 31) // від 1.5 до 3 секунд (15-30 кадра)
        var lastValue = 1

        for (i in 0 until iterations) {
            lastValue = Random.nextInt(1, 7)
            onTick(lastValue)
            delay(100)
        }

        return lastValue
    }


    suspend fun rollAllDice(onDiceUpdated: (index: Int, value: Int) -> Unit): List<Int> = coroutineScope {
        val deferreds = (0 until 5).map { index ->
            async {
                rollSingleDice { newValue ->
                    onDiceUpdated(index, newValue)
                }
            }
        }
        deferreds.awaitAll()
    }
}