package com.onmetal.activity.showband

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.onmetal.R
import com.onmetal.activity.BaseActivity
import com.onmetal.activity.main.MainActivity
import com.onmetal.util.ItemOffsetDecoration
import com.onmetal.web.model.Band

class ShowBandAlbumsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_band_albums)

        setUpToolbar()

        val band = intent.getParcelableExtra<Band>(MainActivity.bandExtra)

        val discs = findViewById<RecyclerView>(R.id.rv)
        val llm = LinearLayoutManager(this)
        val adapter = ShowBandDiscsAdapter(band.discs)
        val itemDec = ItemOffsetDecoration(3)
        discs.setHasFixedSize(true)
        discs.layoutManager = llm
        discs.adapter = adapter
        discs.addItemDecoration(itemDec)

        setUpAds()
    }

    private fun setUpAds() {
        val mAdView = findViewById<AdView>(R.id.albumsAdView)
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        mAdView.loadAd(adRequest)
    }
}
