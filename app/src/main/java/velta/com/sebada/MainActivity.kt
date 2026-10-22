package velta.com.sebada

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import velta.com.sebada.ui.theme.SebadaTheme

class MainActivity : ComponentActivity() {
    private var interstitialAd: InterstitialAd? = null
    private val adUnitId = "ca-app-pub-7931632875015648/6821129956"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this) {}
        loadInterstitialAd()

        setContent {
            SebadaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VeltaWebView(
                        modifier = Modifier.padding(innerPadding),
                        onShowAd = { showInterstitialAd() }
                    )
                }
            }
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    private fun showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd?.show(this)
            loadInterstitialAd()
        } else {
            loadInterstitialAd()
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VeltaWebView(modifier: Modifier = Modifier, onShowAd: () -> Unit) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                addJavascriptInterface(WebAppInterface(onShowAd), "Android")
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                }
                loadUrl("file:///android_asset/velta_la_ronda_sigue.html")
            }
        }
    )
}

class WebAppInterface(private val onShowAd: () -> Unit) {
    @JavascriptInterface
    fun showInterstitial() {
        onShowAd()
    }
}
