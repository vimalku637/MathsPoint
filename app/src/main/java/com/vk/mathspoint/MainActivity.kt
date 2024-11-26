package com.vk.mathspoint

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.button.MaterialButton
import com.vk.mathspoint.databinding.ActivityMainBinding
import org.mozilla.javascript.Scriptable

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private var adRequest: AdRequest?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        /** ads initialization */
        adsInitialization()

        assignId(binding.buttonC)
        assignId(binding.buttonOpenBracket)
        assignId(binding.buttonCloseBracket)
        assignId(binding.buttonDivide)
        assignId(binding.buttonMultiply)
        assignId(binding.buttonPlus)
        assignId(binding.buttonMinus)
        assignId(binding.buttonEquals)
        assignId(binding.button0)
        assignId(binding.button1)
        assignId(binding.button2)
        assignId(binding.button3)
        assignId(binding.button4)
        assignId(binding.button5)
        assignId(binding.button6)
        assignId(binding.button7)
        assignId(binding.button8)
        assignId(binding.button9)
        assignId(binding.buttonAc)
        assignId(binding.buttonDot)

    }
    private fun assignId(btn: MaterialButton?) {
        btn?.setOnClickListener(this)
    }


    override fun onClick(view: View) {
        val button = view as MaterialButton
        val buttonText = button.text.toString()
        var dataToCalculate: String = binding.solutionTv.text.toString()
        if (buttonText == "AC") {
            binding.solutionTv.text = ""
            binding.resultTv.text = "0"
            return
        }
        if (buttonText == "=") {
            binding.solutionTv.text = binding.resultTv.text
            return
        }
        dataToCalculate = if (buttonText == "C") {
            if (dataToCalculate.isNotEmpty()) {
                dataToCalculate.substring(0, dataToCalculate.length - 1)
            } else {
                dataToCalculate // Return as is if empty
            }
        } else {
            dataToCalculate + buttonText
        }
        binding.solutionTv.text = dataToCalculate
        val finalResult = getResult(dataToCalculate)
        if (finalResult != "Err"){
            if(finalResult.contains("Undefined@0")){
                binding.resultTv.text = "0"
                return
            }
            binding.resultTv.text = finalResult
        }
    }

    private fun getResult(data: String?): String {
        return try {
            val context: org.mozilla.javascript.Context = org.mozilla.javascript.Context.enter()
            context.optimizationLevel = -1
            val scriptable: Scriptable = context.initStandardObjects()
            var finalResult: String =
                context.evaluateString(scriptable, data, "Javascript", 1, null).toString()
            if (finalResult.endsWith(".0")) {
                finalResult = finalResult.replace(".0", "")
            }
            finalResult
        } catch (e: Exception) {
            "Err"
        }
    }

    private fun adsInitialization() {
        /**ad-mob ads*/
        MobileAds.initialize(this@MainActivity)

        adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest!!)

        binding.adView.adListener = object: AdListener() {
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

