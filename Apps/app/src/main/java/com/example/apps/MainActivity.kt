package com.example.apps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.apps.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val TAG = "FullCalculatorLog"

    private var firstOperand: Double? = null
    private var currentOperation: String? = null
    private var isNewInput: Boolean = true // чи починається новий ввід числа

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()

        val audioPlayer = AudioPlayer()
        lifecycle.addObserver(audioPlayer)

        val analyticsTracker = AnalyticsTracker()
        lifecycle.addObserver(analyticsTracker)

        UserTracker.register(this)
        Log.d("MemoryLeakTest", "Зареєстровано слухачів у UserTracker: ${UserTracker.getListenersCount()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        UserTracker.unregister(this)
        Log.d("MemoryLeakTest", "Видалено слухача. Залишилось: ${UserTracker.getListenersCount()}")
    }

    private fun setupClickListeners() {
        val digitButtons = mapOf(
            binding.btn0 to "0", binding.btn1 to "1", binding.btn2 to "2",
            binding.btn3 to "3", binding.btn4 to "4", binding.btn5 to "5",
            binding.btn6 to "6", binding.btn7 to "7", binding.btn8 to "8",
            binding.btn9 to "9"
        )

        for ((button, digit) in digitButtons) {
            button.setOnClickListener { onDigitClick(digit) }
        }

        binding.btnDot.setOnClickListener { onDotClick() }

        binding.btnAdd.setOnClickListener { onOperationClick("+") }
        binding.btnSubtract.setOnClickListener { onOperationClick("-") }
        binding.btnMultiply.setOnClickListener { onOperationClick("*") }
        binding.btnDivide.setOnClickListener { onOperationClick("/") }

        binding.btnEquals.setOnClickListener { onEqualsClick() }

        // Очищення
        binding.btnClear.setOnClickListener { clearAll() }

        // Стирання останньої цифри
        binding.btnBackspace.setOnClickListener { onBackspaceClick() }

        binding.btnBracket.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onDigitClick(digit: String) {
        val currentText = binding.tvDisplay.text.toString()

        if (isNewInput || currentText == "0" || currentText == "Помилка") {
            binding.tvDisplay.text = digit
            isNewInput = false
        } else {
            binding.tvDisplay.text = currentText + digit
        }
        Log.d(TAG, "Введено цифру: $digit. На екрані: ${binding.tvDisplay.text}")
    }

    private fun onDotClick() {
        val currentText = binding.tvDisplay.text.toString()

        if (isNewInput) {
            binding.tvDisplay.text = "0."
            isNewInput = false
        } else if (!currentText.contains(".")) {
            binding.tvDisplay.text = "$currentText."
        }
    }

    private fun onOperationClick(operation: String) {
        val currentText = binding.tvDisplay.text.toString()

        if (currentText == "Помилка") return

        firstOperand = currentText.toDoubleOrNull()
        currentOperation = operation
        isNewInput = true

        Log.d(TAG, "Обрано операцію '$operation' для числа $firstOperand")
    }

    private fun onEqualsClick() {
        val operation = currentOperation ?: return
        val first = firstOperand ?: return
        val secondText = binding.tvDisplay.text.toString()

        if (secondText == "Помилка") return

        val second = secondText.toDoubleOrNull() ?: return

        Log.d(TAG, "Обчислення: $first $operation $second")

        if (operation == "/" && second == 0.0) {
            binding.tvDisplay.text = "Помилка"
            Log.w(TAG, "Помилка: ділення на нуль!")
            resetState()
            return
        }

        val result = when (operation) {
            "+" -> first + second
            "-" -> first - second
            "*" -> first * second
            "/" -> first / second
            else -> 0.0
        }

        binding.tvDisplay.text = formatResult(result)
        Log.d(TAG, "Результат: ${binding.tvDisplay.text}")

        resetState()
        isNewInput = true
    }

    private fun onBackspaceClick() {
        val currentText = binding.tvDisplay.text.toString()
        if (isNewInput || currentText == "Помилка") return

        if (currentText.length > 1) {
            binding.tvDisplay.text = currentText.dropLast(1)
        } else {
            binding.tvDisplay.text = "0"
            isNewInput = true
        }
    }

    private fun clearAll() {
        binding.tvDisplay.text = "0"
        resetState()
        isNewInput = true
        Log.d(TAG, "Калькулятор повністю скинуто")
    }

    private fun resetState() {
        firstOperand = null
        currentOperation = null
    }

    private fun formatResult(value: Double): String {
        return if (value % 1 == 0.0) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }
}