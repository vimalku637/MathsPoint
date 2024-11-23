package com.vk.mathspoint

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.*
import com.vk.mathspoint.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var adRequest: AdRequest?=null
    // Declaring Constants
    private val SAVED_OPERATION = "pendingOp"
    private val SAVED_OPERAND = "op1"

    // Declaring Variables
    private lateinit var newNumber: EditText
    private lateinit var result: EditText
    private lateinit var displayOperation: TextView

    private var op1: Double? = null
    private var pendingOp: String = "="

    @SuppressLint("SetTextI18n")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        /** ads initialization */
        adsInitialization()

        // Initialize Views
        newNumber = findViewById(R.id.newNumber)
        result = findViewById(R.id.result)
        displayOperation = findViewById(R.id.operation)

        // Number Buttons
        val numberButtons = listOf(
            findViewById<Button>(R.id.button0),
            findViewById<Button>(R.id.button1),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6),
            findViewById<Button>(R.id.button7),
            findViewById<Button>(R.id.button8),
            findViewById<Button>(R.id.button9),
            findViewById<Button>(R.id.buttonDot)
        )

        // Operation Buttons
        val buttonEquals = findViewById<Button>(R.id.buttonEquals)
        val buttonDevide = findViewById<Button>(R.id.buttonDevide)
        val buttonMultiply = findViewById<Button>(R.id.buttonMultiply)
        val buttonMinus = findViewById<Button>(R.id.buttonMinus)
        val buttonPlus = findViewById<Button>(R.id.buttonAdd)
        val percentageButton = findViewById<Button>(R.id.percentage)
        val brackets = findViewById<Button>(R.id.buttonBracket)
        val buttonNeg = findViewById<Button>(R.id.negSymobol)
        val clearText = findViewById<Button>(R.id.clearText)

        // Scientific Operations (Landscape Orientation)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val buttonSin = findViewById<Button>(R.id.buttonSin)
            val buttonCos = findViewById<Button>(R.id.buttonCos)
            val buttonTan = findViewById<Button>(R.id.buttonTan)
            val buttonRoot = findViewById<Button>(R.id.buttonRoot)
            val buttonFactor = findViewById<Button>(R.id.buttonFactor)
            val buttonPie = findViewById<Button>(R.id.buttonPie)
            val buttonSquare = findViewById<Button>(R.id.buttonSquare)
            val buttonLog = findViewById<Button>(R.id.buttonLog)

            // Scientific Operations Listeners
            buttonSquare.setOnClickListener {
                performUnaryOperation("square")
            }

            buttonPie.setOnClickListener {
                performUnaryOperation("pi")
            }

            buttonRoot.setOnClickListener {
                performUnaryOperation("root")
            }

            buttonSin.setOnClickListener {
                performUnaryOperation("sin")
            }

            buttonCos.setOnClickListener {
                performUnaryOperation("cos")
            }

            buttonTan.setOnClickListener {
                performUnaryOperation("tan")
            }

            buttonLog.setOnClickListener {
                performUnaryOperation("log")
            }

            buttonFactor.setOnClickListener {
                performUnaryOperation("factorial")
            }
        }

        // Number Button Listeners
        val onClickNumber = { v: Button -> result.append(v.text.toString()) }
        numberButtons.forEach { it.setOnClickListener { onClickNumber(it as Button) } }

        // Operation Button Listeners
        val onClickOperation = { v: Button ->
            val operation = v.text.toString()
            val value = result.text.toString()
            try {
                performOperation(value.toDouble(), operation)
            } catch (e: NumberFormatException) {
                result.setText("")
            }
            pendingOp = operation
            displayOperation.text = pendingOp
        }

        buttonEquals.setOnClickListener { onClickOperation(it as Button) }
        buttonDevide.setOnClickListener { onClickOperation(it as Button) }
        buttonMultiply.setOnClickListener { onClickOperation(it as Button) }
        buttonMinus.setOnClickListener { onClickOperation(it as Button) }
        buttonPlus.setOnClickListener { onClickOperation(it as Button) }
        percentageButton.setOnClickListener { calculatePercentage() }

        // Clear Button
        clearText.setOnClickListener {
            clearAll()
        }

        // Negative Symbol Button
        buttonNeg.setOnClickListener {
            toggleNegative()
        }

        // Brackets Button
        brackets.setOnClickListener {
            removeLastCharacter()
        }

        // Check First Run
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val isFirstRun = sharedPreferences.getBoolean("IS_FIRST_RUN", true)
        if (isFirstRun) {
            showUpdateInfo()
            sharedPreferences.edit().putBoolean("IS_FIRST_RUN", false).apply()
        }
    }
    private fun performUnaryOperation(operation: String) {
        try {
            val value = result.text.toString().toDouble()
            val resultValue = when (operation) {
                "square" -> value * value
                "pi" -> value * Math.PI
                "root" -> Math.sqrt(value)
                "sin" -> Math.sin(Math.toRadians(value))
                "cos" -> Math.cos(Math.toRadians(value))
                "tan" -> Math.tan(Math.toRadians(value))
                "log" -> Math.log(value)
                "factorial" -> factorial(value.toInt())
                else -> throw IllegalArgumentException("Unknown operation")
            }
            newNumber.setText(resultValue.toString())
            result.setText("")
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performOperation(value: Double, operation: String) {
        if (op1 == null) {
            op1 = value
        } else {
            when (pendingOp) {
                "=" -> op1 = value
                "÷" -> op1 = if (value == 0.0) 0.0 else op1!! / value
                "×" -> op1 = op1!! * value
                "-" -> op1 = op1!! - value
                "+" -> op1 = op1!! + value
            }
        }
        newNumber.setText(op1.toString())
        result.setText("")
    }

    private fun factorial(n: Int): Double {
        return if (n == 0) 1.0 else n * factorial(n - 1)
    }

    private fun calculatePercentage() {
        try {
            val value1 = result.text.toString().toDouble()
            val value2 = newNumber.text.toString().toDouble()
            val percentage = value1 * value2 / 100
            newNumber.setText(percentage.toString())
            result.setText("")
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearAll() {
        newNumber.setText("")
        result.setText("")
        displayOperation.text = ""
        op1 = null
    }

    private fun toggleNegative() {
        val value = result.text.toString()
        result.setText(if (value.startsWith("-")) value.drop(1) else "-$value")
    }

    private fun removeLastCharacter() {
        val text = result.text.toString()
        if (text.isNotEmpty()) result.setText(text.dropLast(1))
    }

    private fun showUpdateInfo() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Update Info!")
            .setMessage(
                """
                → New Material UI Introduced.
                → Minor Bugs Fixed.
                → Added New Functions.
                → With New Refreshing Look.
                → Dev CraazY.
                """.trimIndent()
            )
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_OPERATION, pendingOp)
        op1?.let { outState.putDouble(SAVED_OPERAND, it) }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        pendingOp = savedInstanceState.getString(SAVED_OPERATION, "=")
        op1 = savedInstanceState.getDouble(SAVED_OPERAND)
        displayOperation.text = if (pendingOp == "=") "" else pendingOp
    }

    private fun adsInitialization() {
        /**ad-mob ads*/
        MobileAds.initialize(this@MainActivity)

        adRequest = AdRequest.Builder().build()
        binding.adView?.loadAd(adRequest!!)

        binding.adView?.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }
}

