package com.vk.mathspoint

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.button.MaterialButton
import com.vk.mathspoint.databinding.ActivityMainBinding
import org.mozilla.javascript.Scriptable

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private var adRequest: AdRequest?=null
    private var interstitialAd: InterstitialAd?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding=DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        // Configure AdMob to show test ads on this device
        MobileAds.initialize(this@MainActivity) { }
//        val configuration = RequestConfiguration.Builder()
//            .setTestDeviceIds(mutableListOf("5F113D5FD7F39144F9B6C130A19CFA3E")) // Replace with your actual test device ID
//            .build()
//        MobileAds.setRequestConfiguration(configuration)

        // Load the Interstitial Ad
        loadInterstitialAd()

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
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this@MainActivity,
            getString(R.string.INTERSTITIAL_AD_UNIT_ID), // Use test Ad Unit ID for testing
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad

                    showInterstitialAd()
//                    setupAdCallbacks()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    Log.d(TAG, "onAdFailedToLoad: "+"Ad failed to load")
                }
            }
        )
    }

    private fun setupAdCallbacks() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent: "+"Ad dismissed")
                // Reload the ad after it's closed
                loadInterstitialAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(TAG, "onAdFailedToShowFullScreenContent: "+"Ad failed to show")
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null // Ad is shown, so clear the reference
            }
        }
    }

    private fun showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd?.show(this@MainActivity)
        } else {
            Log.d(TAG, "showInterstitialAd: "+"Ad not ready yet, proceeding without ad")
        }
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}

