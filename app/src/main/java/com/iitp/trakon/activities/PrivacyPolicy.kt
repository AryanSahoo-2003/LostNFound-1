package com.iitp.trakon.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.iitp.trakon.R
import kotlinx.android.synthetic.main.activity_privacy_policy.*

class PrivacyPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        var alpha= privacy_web
        alpha.webViewClient= WebViewClient()
        alpha.loadUrl("https://stc.iitp.ac.in/TrackOn/Privacy.html")
        alpha.settings.javaScriptEnabled=true
        alpha.settings.builtInZoomControls=true;
        alpha.settings.displayZoomControls=true

    }
    override fun onBackPressed(){
        startActivity(Intent(this,Tabs::class.java))
    }
}