package com.onmetal.activity.showband

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.onmetal.R
import com.onmetal.activity.BaseActivity
import com.onmetal.activity.main.MainActivity

class ShowBandBioActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_band_bio)

        setUpToolbar()

        val bioText = intent.getStringExtra(MainActivity.bio)
        val bio = findViewById<TextView>(R.id.band_bio)
        bio.movementMethod = ScrollingMovementMethod()
        bio.text = bioText

        setUpAds()
    }

    private fun setUpAds() {
        val mAdView = findViewById<AdView>(R.id.bioAdView)
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        mAdView.loadAd(adRequest)
    }
}
