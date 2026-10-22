package com.example.apps

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Тег для логування у Logcat
    private val TAG = "CalculatorLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etNumber1 = findViewById<EditText>(R.id.etNumber1)
        val etNumber2 = findViewById<EditText>(R.id.etNumber2)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnSubtract = findViewById<Button>(R.id.btnSubtract)
        val btnMultiply = findViewById<Button>(R.id.btnMultiply)
        val btnDivide = findViewById<Button>(R.id.btnDivide)
        val btnClear = findViewById<Button>(R.id.btnClear)

        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnAdd.setOnClickListener {
            calculate('+', etNumber1, etNumber2, tvResult)
        }

        btnSubtract.setOnClickListener {
            calculate('-', etNumber1, etNumber2, tvResult)
        }

        btnMultiply.setOnClickListener {
            calculate('*', etNumber1, etNumber2, tvResult)
        }

        btnDivide.setOnClickListener {
            calculate('/', etNumber1, etNumber2, tvResult)
        }

        btnClear.setOnClickListener {
            etNumber1.text.clear()
            etNumber2.text.clear()
            tvResult.text = "Результат: "
            Log.d(TAG, "Поля та результат очищено")
        }
    }

    private fun calculate(
        operation: Char,
        etNumber1: EditText,
        etNumber2: EditText,
        tvResult: TextView
    ) {
        val str1 = etNumber1.text.toString().trim()
        val str2 = etNumber2.text.toString().trim()

        if (str1.isEmpty() || str2.isEmpty()) {
            tvResult.text = "Будь ласка, введіть обидва числа"
            Log.w(TAG, "Спроба обчислення з порожніми полями")
            return
        }

        val num1 = str1.toDouble()
        val num2 = str2.toDouble()

        Log.d(TAG, "Виконується операція '$operation' з числами: $num1 та $num2")

        if (operation == '/' && num2 == 0.0) {
            tvResult.text = "Помилка: ділення на нуль неможливе!"
            Log.w(TAG, "Помилка: Спроба ділення числа $num1 на нуль!")
            return
        }

        val result = when (operation) {
            '+' -> num1 + num2
            '-' -> num1 - num2
            '*' -> num1 * num2
            '/' -> num1 / num2
            else -> 0.0
        }

        val formattedResult = formatResult(result)

        tvResult.text = "Результат: $formattedResult"
        Log.d(TAG, "Результат обчислення: $formattedResult")
    }

    private fun formatResult(value: Double): String {
        return if (value % 1 == 0.0) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }
}