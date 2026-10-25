package com.example.dice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.random.Random

class DiceViewModel : ViewModel() {

    private val _diceValues = MutableLiveData<List<Int>>(listOf(1, 1, 1, 1, 1))
    val diceValues: LiveData<List<Int>> get() = _diceValues

    // LiveData для стану обертання (true - обертаються, false - зупинені)
    private val _isRolling = MutableLiveData<Boolean>(false)
    val isRolling: LiveData<Boolean> get() = _isRolling

    @Volatile
    private var isCancelled = false

    fun rollDice() {
        if (_isRolling.value == true) return

        _isRolling.value = true
        isCancelled = false

        val currentValues = _diceValues.value?.toMutableList() ?: mutableListOf(1, 1, 1, 1, 1)
        val finishedThreadsCount = AtomicInteger(0)
        val totalDice = 5

        for (i in 0 until totalDice) {
            thread {
                val iterations = Random.nextInt(15, 26)

                for (j in 0 until iterations) {
                    if (isCancelled) break

                    val newRandomValue = Random.nextInt(1, 7)

                    synchronized(currentValues) {
                        currentValues[i] = newRandomValue
                        _diceValues.postValue(currentValues.toList())
                    }

                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        break
                    }
                }

                // лічильник завершених потоків
                if (finishedThreadsCount.incrementAndGet() == totalDice) {
                    _isRolling.postValue(false)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        isCancelled = true
    }
}