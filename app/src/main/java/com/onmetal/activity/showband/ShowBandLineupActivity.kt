package com.onmetal.activity.showband

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.onmetal.R
import com.onmetal.activity.BaseActivity
import com.onmetal.activity.main.MainActivity
import com.onmetal.task.GetCurrentBandLineupTask
import com.onmetal.util.ItemOffsetDecoration
import com.onmetal.web.model.Band
import com.onmetal.web.model.FullPerson

class ShowBandLineupActivity : BaseActivity(), LineUpActivityListener {

    private lateinit var pb: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_band_lineup)

        setUpToolbar()

        val band = intent.getParcelableExtra<Band>(MainActivity.bandExtra)
        pb = findViewById(R.id.progressBar)
        showProgressBar(true)
        GetCurrentBandLineupTask(this).execute(band.id)

        setUpAds()
    }

    private fun setUpAds() {
        val mAdView = findViewById<AdView>(R.id.lineupAdView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun finish(lineup: List<FullPerson>?) {
        showProgressBar(false)

        if (lineup != null) {
            val llm = GridLayoutManager(this, 2)
            val adapter = ShowBandLineupAdapter(lineup)
            val itemDec = ItemOffsetDecoration(5, 5, 10, 5)

            val rv = findViewById<RecyclerView>(R.id.rv)
            rv.setHasFixedSize(true)
            rv.layoutManager = llm
            rv.adapter = adapter
            rv.addItemDecoration(itemDec)
        }
    }

    private fun showProgressBar(show: Boolean) {
        pb.visibility = if (show) View.VISIBLE else View.GONE
    }
}
