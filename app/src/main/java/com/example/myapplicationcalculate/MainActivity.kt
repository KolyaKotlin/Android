package com.example.myapplicationcalculate

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var tvInput: TextView
    private var lastNumeric: Boolean = false
    private var lastDot: Boolean = false
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvInput = findViewById(R.id.tvInput)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val buttonIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnDot, R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide,
            R.id.btnC, R.id.btnEquals, R.id.btnPM, R.id.btnPercent
        )

        for (id in buttonIds) {
            findViewById<Button>(id).setOnClickListener {
                vibrate()
                onButtonClick(it as Button)
            }
        }
    }

    private fun vibrate() {
        if (vibrator.hasVibrator()) {
            val effect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        }
    }

    private fun onButtonClick(button: Button) {
        when (button.id) {
            R.id.btnC -> {
                tvInput.text = ""
                lastNumeric = false
                lastDot = false
            }
            R.id.btnEquals -> {
                if (lastNumeric) {
                    calculateResult()
                }
            }
            R.id.btnDot -> {
                if (lastNumeric && !lastDot) {
                    tvInput.append(".")
                    lastNumeric = false
                    lastDot = true
                }
            }
            R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide -> {
                if (lastNumeric && !isOperatorAdded(tvInput.text.toString())) {
                    tvInput.append(button.text)
                    lastNumeric = false
                    lastDot = false
                }
            }
            R.id.btnPM -> {
                togglePlusMinus()
            }
            R.id.btnPercent -> {
                calculatePercent()
            }
            else -> {
                tvInput.append(button.text)
                lastNumeric = true
            }
        }
    }

    private fun isOperatorAdded(value: String): Boolean {
        val operators = listOf("+", "×", "÷", "*", "/")
        val checkValue = if (value.startsWith("-")) value.substring(1) else value
        return operators.any { checkValue.contains(it) } || (checkValue.contains("-") && checkValue.indexOf("-") != 0)
    }

    private fun calculateResult() {
        try {
            var expression = tvInput.text.toString()
            expression = expression.replace("×", "*").replace("÷", "/")
            val result = ExpressionBuilder(expression).build().evaluate()
            tvInput.text = if (result % 1 == 0.0) result.toInt().toString() else result.toString()
        } catch (e: Exception) {
            tvInput.text = "Ошибка"
        }
    }

    private fun togglePlusMinus() {
        val text = tvInput.text.toString()
        if (text.isEmpty()) return

        tvInput.text = if (text.startsWith("-")) {
            text.substring(1)
        } else {
            "-$text"
        }
    }

    private fun calculatePercent() {
        val currentText = tvInput.text.toString()
        if (currentText.isEmpty()) return

        try {
            var expression = currentText
            expression = expression.replace("×", "*").replace("÷", "/")
            val value = ExpressionBuilder(expression).build().evaluate()
            val percentValue = value / 100.0
            tvInput.text = if (percentValue % 1 == 0.0) percentValue.toInt().toString() else percentValue.toString()
        } catch (e: Exception) {
            tvInput.text = "Ошибка"
        }
    }
}
