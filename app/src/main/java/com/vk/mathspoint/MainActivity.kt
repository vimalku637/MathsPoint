package com.vk.mathspoint

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.*
import com.vk.mathspoint.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var adRequest: AdRequest?=null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.viewmodel=this

        /** ads initialization */
        adsInitialization()

        if(isInternetAvailable(this@MainActivity)) {
            binding.webView?.webViewClient = MyWebViewClient()
            binding.webView?.loadUrl("https://mathspoint.info")
            binding.webView?.settings?.javaScriptEnabled = true
            binding.webView?.settings?.loadsImagesAutomatically = true
            binding.webView?.addJavascriptInterface(WebAppInterface(this), "Android")
        }else{
            AlertDialog.Builder(this@MainActivity)
                .setTitle("No internet connection found!!")
                .setMessage("Please make sure you have internet connection before use the application.")
                .setPositiveButton(
                    "Ok"
                ) { _, _ -> finishAffinity() }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
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

    private class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            if (Uri.parse(url).host == "mathspoint.info") {
                // This is my web site, so do not override; let my WebView load the page
                return false
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                view.context.startActivity(this)
            }
            return true
        }
    }

    /** Instantiate the interface and set the context  */
    class WebAppInterface(private val mContext: Context) {

        /** Show a toast from the web page  */
        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView?.canGoBack()!!) {
            binding.webView?.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
    }
}

