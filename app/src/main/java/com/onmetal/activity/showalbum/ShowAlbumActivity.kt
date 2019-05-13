package com.onmetal.activity.showalbum

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.beust.klaxon.*
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.onmetal.R
import com.onmetal.activity.BaseActivity
import com.onmetal.activity.account.AccountActivity
import com.onmetal.activity.main.LatestReleasesAdapter
import com.onmetal.activity.main.MainActivity
import com.onmetal.activity.searchresults.SearchBandsItemClickListener
import com.onmetal.constant.buildGetBandByIdUrl
import com.onmetal.util.ItemOffsetDecoration
import com.onmetal.util.UserManager
import com.onmetal.web.model.Album
import com.onmetal.web.model.Band
import com.onmetal.web.model.Disc
import com.onmetal.web.model.Person
import com.onmetal.web.okGet
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class ShowAlbumActivity : BaseActivity() {

    private var isAlbumLiked: Boolean = false
    private var disposable: Disposable? = null

    private lateinit var album: Album

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_album)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setUpToolbar()

        album = intent.getParcelableExtra(MainActivity.albumExtra)

        val logoView = findViewById<ImageView>(R.id.logo)
        val nameView = findViewById<TextView>(R.id.bandName)
        val songsLabel = findViewById<TextView>(R.id.detailsLabel2)
        val otherReleases = findViewById<RecyclerView>(R.id.other_releases)
        isAlbumLiked = UserManager.isAlbumLiked(this, album.id)

        songsLabel.setOnClickListener {
            val intent = Intent(this, ShowAlbumSongsActivity::class.java)
            intent.putExtra(MainActivity.albumExtra, album)
            startActivity(intent)
        }

        if (!album.album_cover.isEmpty()) {
            Glide.with(logoView.context)
                    .load(album.album_cover)
                    .into(logoView)
        }

        nameView.text = album.title

        disposable = Single
                .fromCallable(Callable<Band> {
                    val url = buildGetBandByIdUrl(album.bandId)
                    val response = url.okGet()
                    val result = Parser().parse(StringBuilder(response)) as JsonObject
                    val discs: List<Disc>? = result.array<JsonObject>("discography")?.map {
                        Disc(
                                it.string("name") ?: "",
                                it.string("id") ?: "",
                                it.string("year") ?: "",
                                it.string("type") ?: ""
                        )
                    }
                    val lineup: List<Person>? = result.array<JsonObject>("currentLineup")?.map {
                        Person(
                                it.string("name") ?: "",
                                it.string("id") ?: "",
                                it.array<String>("instruments").toString(),
                                it.boolean("current") ?: false
                        )
                    }
                    return@Callable Band(
                            result.string("id") ?: "",
                            result.string("bandName") ?: "",
                            result.string("logo") ?: "",
                            result.obj("details")?.string("country") ?: "",
                            result.obj("details")?.string("genre") ?: "",
                            result.obj("details")?.string("status") ?: "",
                            result.string("bio") ?: "",
                            discs ?: emptyList(),
                            lineup ?: emptyList())
                })
                .map { data -> data.discs.toMutableList() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    val llm = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    val adapter = LatestReleasesAdapter(data.filter { n -> n.id != album.id }, R.layout.other_release_item)
                    val itemDec = ItemOffsetDecoration(0, 0, 20, 0)
                    otherReleases.setHasFixedSize(true)
                    otherReleases.layoutManager = llm
                    otherReleases.adapter = adapter
                    otherReleases.addItemDecoration(itemDec)
                }, { _ ->
                })

        finishDetailsTable(album)
        setUpAds()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isAlbumLiked) {
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
                return if (isAlbumLiked) {
                    UserManager.unlikeAlbum(this, album.id)
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_add_white_48px)
                    isAlbumLiked = false
                    Toast.makeText(this, "Album was removed from liked list", Toast.LENGTH_SHORT).show()
                    true
                } else {
                    UserManager.likeAlbum(this, album.id)
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_clear_white_48px)
                    isAlbumLiked = true
                    Toast.makeText(this, "Album was added to liked list", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable?.dispose()
    }

    private fun setUpAds() {
        val mAdView = findViewById<AdView>(R.id.showAlbumAdView)
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        mAdView.loadAd(adRequest)
    }

    private fun finishDetailsTable(album: Album) {
        val albumBandName = findViewById<TextView>(R.id.album_details_band)
        val albumReleaseDate = findViewById<TextView>(R.id.album_details_release)
        val albumType = findViewById<TextView>(R.id.album_details_type)
        val albumLabel = findViewById<TextView>(R.id.album_details_label)
        val albumFormat = findViewById<TextView>(R.id.album_details_format)

        albumBandName.text = "Band: ${album.bandName}"
        albumReleaseDate.text = "Release Date: ${album.releaseDate}"
        albumType.text = "Type: ${album.type}"
        albumLabel.text = "Label: ${album.label}"
        albumFormat.text = "Format: ${album.format}"
        albumBandName.setOnClickListener(SearchBandsItemClickListener(album.bandId))
    }
}