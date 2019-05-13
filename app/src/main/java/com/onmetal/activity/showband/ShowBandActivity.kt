package com.onmetal.activity.showband

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.CardView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.onmetal.R
import com.onmetal.activity.BaseActivity
import com.onmetal.activity.account.AccountActivity
import com.onmetal.activity.main.MainActivity
import com.onmetal.util.UserManager
import com.onmetal.web.model.Band

class ShowBandActivity : BaseActivity() {

    private var isBandLiked = false

    private lateinit var band: Band

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_band)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setUpToolbar()

        val neededElWidth = (getDisplaySize().x - 100) / 3

        band = intent.getParcelableExtra(MainActivity.bandExtra)

        val nameView = findViewById<TextView>(R.id.albumName)
        nameView.text = band.name

        isBandLiked = UserManager.isBandLiked(this, band.id)
        setUpLogo(band)
        finishDetailsTable(band)
        setUpAds()
        setUpBio(band, neededElWidth)
        setUpAlbums(band, neededElWidth)
        setUpLineup(band, neededElWidth)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isBandLiked) {
            menuInflater.inflate(R.menu.navigation_items_remove, menu)
        } else {
            menuInflater.inflate(R.menu.navigation_items_add, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this, MainActivity::class.java))
            }

            R.id.account -> {
                startActivity(Intent(this, AccountActivity::class.java))
            }
            R.id.action -> {
                return if (isBandLiked) {
                    UserManager.unlikeBand(this, band.id)
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_add_white_48px)
                    isBandLiked = false
                    Toast.makeText(this, "Band was removed from liked list", Toast.LENGTH_SHORT).show()
                    true
                } else {
                    UserManager.likeBand(this, band.id)
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_clear_white_48px)
                    isBandLiked = true
                    Toast.makeText(this, "Band was added to liked list", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }
        return false
    }

    private fun setUpLogo(band: Band) {
        val logoView = findViewById<ImageView>(R.id.logo)
        if (!band.logo.isEmpty()) {
            Glide.with(logoView.context)
                    .load(band.logo)
                    .into(logoView)
        }
    }

    private fun setUpAds() {
        val mAdView = findViewById<AdView>(R.id.showBandAdView)
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        mAdView.loadAd(adRequest)
    }

    private fun setUpLineup(band: Band, neededElWidth: Int) {
        val lineup = findViewById<CardView>(R.id.lineup_cv)
        setElSize(lineup, neededElWidth)
        lineup.setOnClickListener {
            val intent = Intent(this, ShowBandLineupActivity::class.java)
            intent.putExtra(MainActivity.bandExtra, band)
            startActivity(intent)
        }
    }

    private fun setUpAlbums(band: Band, neededElWidth: Int) {
        val view = findViewById<CardView>(R.id.albums_cv)
        setElSize(view, neededElWidth)
        view.setOnClickListener {
            val intent = Intent(this, ShowBandAlbumsActivity::class.java)
            intent.putExtra(MainActivity.bandExtra, band)
            startActivity(intent)
        }
    }

    private fun setElSize(imageView: View, size: Int) {
        val params = imageView.layoutParams
        params.height = size
        params.width = size
        imageView.layoutParams = params
    }

    private fun setUpBio(band: Band, neededElWidth: Int) {
        val view = findViewById<CardView>(R.id.bio_cv)
        setElSize(view, neededElWidth)
        val bio = band.bio
        if (!bio.isEmpty()) {
            view.setOnClickListener {
                val intent = Intent(this, ShowBandBioActivity::class.java)
                intent.putExtra(MainActivity.bio, bio)
                startActivity(intent)
            }
        } else {
            view.setOnClickListener {
                AlertDialog.Builder(this)
                        .setMessage("This band doesn't have bio")
                        .setCancelable(true)
                        .setPositiveButton("OK", { dialog, _ ->
                            dialog.dismiss()
                        })
                        .show()
            }
        }
    }

    private fun finishDetailsTable(band: Band) {
        val bandCountry = findViewById<TextView>(R.id.band_country)
        val bandStatus = findViewById<TextView>(R.id.band_status)
        val bandGenre = findViewById<TextView>(R.id.band_genre)

        bandCountry.text = "Country: ${band.country}"
        bandStatus.text = band.status
        bandGenre.text = "Genre: ${band.genre}"

        when (band.status) {
            "Active" -> bandStatus.setTextColor(ContextCompat.getColor(this, R.color.band_status_active))
            "Split-up" -> bandStatus.setTextColor(ContextCompat.getColor(this, R.color.band_status_not_active))
            "On hold" -> bandStatus.setTextColor(ContextCompat.getColor(this, R.color.band_status_not_active))
            "Changed name" -> bandStatus.setTextColor(ContextCompat.getColor(this, R.color.band_status_not_active))
            "Unknown" -> bandStatus.setTextColor(ContextCompat.getColor(this, R.color.band_status_other))
            else -> bandStatus.setTextColor(ContextCompat.getColor(this, R.color.band_status_other))
        }
    }
}

