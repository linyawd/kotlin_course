package com.example.dice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DiceViewModel : ViewModel() {

    private val diceModel = DiceModel()

    private val _diceValues = MutableLiveData<List<Int>>(listOf(1, 1, 1, 1, 1))
    val diceValues: LiveData<List<Int>> get() = _diceValues

    private val _isRolling = MutableLiveData<Boolean>(false)
    val isRolling: LiveData<Boolean> get() = _isRolling

    private val _buttonText = MutableLiveData<String>("ROLL")
    val buttonText: LiveData<String> get() = _buttonText

    private var rollJob: Job? = null

    fun onRollButtonClicked() {
        if (rollJob?.isActive == true) {
            rollJob?.cancel()
        }

        startRolling()
    }

    private fun startRolling() {
        _isRolling.value = true
        _buttonText.value = "RESTART"

        rollJob = viewModelScope.launch {
            try {
                val currentList = _diceValues.value?.toMutableList() ?: mutableListOf(1, 1, 1, 1, 1)

                diceModel.rollAllDice { index, newValue ->
                    synchronized(currentList) {
                        currentList[index] = newValue
                        _diceValues.value = currentList.toList()
                    }
                }

                _isRolling.value = false
                _buttonText.value = "ROLL"
            } catch (e: Exception) {
                // Викликається у разі скасування через job?.cancel() або помилки
                // Блок не змінює buttonText/isRolling на фінал, бо буде запуск нового startRolling()
            }
        }
    }
}