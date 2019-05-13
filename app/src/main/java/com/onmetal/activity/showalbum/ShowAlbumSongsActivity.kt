package com.onmetal.activity.showalbum

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
import com.onmetal.web.model.Album

class ShowAlbumSongsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_album_songs)

        setUpToolbar()

        val results: Album = intent.getParcelableExtra(MainActivity.albumExtra)

        val llm = LinearLayoutManager(this)
        val adapter = ShowAlbumSongsAdapter(results.songs)
        val itemDec = ItemOffsetDecoration(5)

        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.setHasFixedSize(true)
        rv.layoutManager = llm
        rv.adapter = adapter
        rv.addItemDecoration(itemDec)

        setUpAds()
    }

    private fun setUpAds() {
        val mAdView = findViewById<AdView>(R.id.songsAdView)
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        mAdView.loadAd(adRequest)
    }
}
