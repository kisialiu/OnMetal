package com.onmetal.activity.main

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.kobakei.ratethisapp.RateThisApp
import com.onmetal.R
import com.onmetal.activity.BaseActivity
import com.onmetal.activity.alarm.AlarmReceiver
import com.onmetal.activity.searchalbums.SearchAlbumsActivity
import com.onmetal.activity.searchalbums.SearchAlbumsLoader
import com.onmetal.activity.searchresults.SearchBandsActivity
import com.onmetal.activity.searchresults.SearchBandsLoader
import com.onmetal.constant.buildGetLatestReleases
import com.onmetal.constant.serviceTime
import com.onmetal.task.GetBandsByGenreTask
import com.onmetal.task.GetBandsByNameTask
import com.onmetal.task.GetDiscsByTitleTask
import com.onmetal.util.ItemOffsetDecoration
import com.onmetal.web.isNetworkConnected
import com.onmetal.web.model.Disc
import com.onmetal.web.model.DiscsSearchResults
import com.onmetal.web.model.SearchResults
import com.onmetal.web.okGet
import com.onmetal.web.showNoNetworkAlert
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class MainActivity : BaseActivity(), SearchBandsLoader, SearchAlbumsLoader {

    companion object {
        const val bandExtra = "BandExtra"
        const val albumExtra = "AlbumExtra"
        const val searchBandsExtra = "SearchBandsExtra"
        const val searchBandsIsGenre = "SearchBandsIsGenre"
        const val searchAlbumsExtra = "SearchAlbumsExtra"
        const val latestReleasesCount = "10"
        const val bio = "bio"
    }

    private lateinit var pendingIntent: PendingIntent

    private lateinit var pb: ProgressBar
    private lateinit var searchAlbumButton: Button
    private lateinit var searchBandButton: Button
    private lateinit var searchGenreButton: Button
    private lateinit var searchBtn: ImageView
    private var isSearchBandEnabled = true
    private var isSearchAlbumEnabled = false
    private var isSearchGenreEnabled = false

    private var disposable: Disposable? = null

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var alarmIntent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
        startService()

        toolbar = findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val rateConfig = RateThisApp.Config(1, 3)
        RateThisApp.init(rateConfig)

        pb = findViewById(R.id.progressBar)
        showProgressBar(false)
        val releasesPb = findViewById<ProgressBar>(R.id.latestReleasesProgressBar)
        releasesPb.visibility = View.VISIBLE
        val searchField = findViewById<EditText>(R.id.editText)
        searchAlbumButton = findViewById(R.id.searchAlbumButton)
        searchBandButton = findViewById(R.id.searchBandButton)
        searchGenreButton = findViewById(R.id.searchGenreButton)
        setSearchToogleStatus()
        searchBtn = findViewById(R.id.search_button)
        searchBtn.visibility = View.VISIBLE

        searchBandButton.setOnClickListener {
            isSearchGenreEnabled = false
            isSearchAlbumEnabled = false
            isSearchBandEnabled = true
            setSearchToogleStatus()
        }
        searchAlbumButton.setOnClickListener {
            isSearchGenreEnabled = false
            isSearchAlbumEnabled = true
            isSearchBandEnabled = false
            setSearchToogleStatus()
        }
        searchGenreButton.setOnClickListener {
            isSearchGenreEnabled = true
            isSearchAlbumEnabled = false
            isSearchBandEnabled = false
            setSearchToogleStatus()
        }

        searchBtn.setOnClickListener {
            searchBtn.visibility = View.INVISIBLE
            showProgressBar(true)
            when {
                isSearchBandEnabled -> getOnCLickListener(GetBandsByNameTask(this).execute(searchField.text.toString(), "0"))
                isSearchAlbumEnabled -> getOnCLickListener(GetDiscsByTitleTask(this).execute(searchField.text.toString(), "0"))
                isSearchGenreEnabled -> getOnCLickListener(GetBandsByGenreTask(this).execute(searchField.text.toString(), "0"))
            }
        }

        disposable = Single
                .fromCallable(Callable<List<Disc>> {
                    val url = buildGetLatestReleases(latestReleasesCount)
                    val response = url.okGet()
                    val result = Parser().parse(StringBuilder(response)) as JsonArray<JsonObject>
                    return@Callable result.map {
                        Disc(
                                it.string("name") ?: "",
                                it.string("id") ?: "",
                                it.string("year") ?: "",
                                it.string("type") ?: ""
                        )
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    val llm = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    val adapter = LatestReleasesAdapter2(data ?: emptyList(), R.layout.release_item)
                    val itemDec = ItemOffsetDecoration(0, 0, 20, 0)

                    val rv = findViewById<RecyclerView>(R.id.releases)
                    rv.setHasFixedSize(true)
                    rv.layoutManager = llm
                    rv.adapter = adapter
                    rv.addItemDecoration(itemDec)

                    releasesPb.visibility = View.GONE
                }, { e ->

                })

        setUpAds()

        RateThisApp.showRateDialogIfNeeded(this)
    }

    private fun startService() {
        var manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), serviceTime, pendingIntent)
    }

    private fun setUpAds() {
        val mAdView = findViewById<AdView>(R.id.mainScreenAdView)
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6086306DEFE3A86E95C1FD91B594ED03")
                .build()
        mAdView.loadAd(adRequest)
    }

    private fun setSearchToogleStatus() {
        when {
            isSearchBandEnabled -> {
                searchBandButton.setBackgroundResource(R.drawable.switch_button_middle_chosen)
                searchAlbumButton.setBackgroundResource(R.drawable.switch_button_left)
                searchGenreButton.setBackgroundResource(R.drawable.switch_button_right)
            }
            isSearchAlbumEnabled -> {
                searchBandButton.setBackgroundResource(R.drawable.switch_button_middle)
                searchAlbumButton.setBackgroundResource(R.drawable.switch_button_left_chosen)
                searchGenreButton.setBackgroundResource(R.drawable.switch_button_right)
            }
            isSearchGenreEnabled -> {
                searchBandButton.setBackgroundResource(R.drawable.switch_button_middle)
                searchAlbumButton.setBackgroundResource(R.drawable.switch_button_left)
                searchGenreButton.setBackgroundResource(R.drawable.switch_button_right_chosen)
            }
        }
    }

    private fun getOnCLickListener(task: AsyncTask<*, *, *>): View.OnClickListener {
        return View.OnClickListener {
            hideKeyboard()
            if (isNetworkConnected(this)) {
                showProgressBar(true)
                task
            } else {
                showNoNetworkAlert(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable?.dispose()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun showProgressBar(show: Boolean) {
        pb.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun loadSearchResultsActivity(results: SearchResults?, isGenre: Boolean) {
        showProgressBar(false)
        searchBtn.visibility = View.VISIBLE

        if (results == null || results.results.isEmpty()) {
            AlertDialog.Builder(this)
                    .setMessage("No bands found using search text: " + results?.searchField)
                    .setCancelable(true)
                    .setPositiveButton("OK", { dialog, _ ->
                        dialog.dismiss()
                    })
                    .show()
        } else {
            val intent = Intent(this, SearchBandsActivity::class.java)
            intent.putExtra(searchBandsExtra, results)
            intent.putExtra(searchBandsIsGenre, isGenre)
            startActivity(intent)
        }
    }

    override fun loadSearchResultsActivity(results: DiscsSearchResults?) {
        showProgressBar(false)
        searchBtn.visibility = View.VISIBLE

        if (results == null || results.results.isEmpty()) {
            AlertDialog.Builder(this)
                    .setMessage("No albums found using search text: " + results?.searchField)
                    .setCancelable(true)
                    .setPositiveButton("OK", { dialog, _ ->
                        dialog.dismiss()
                    })
                    .show()
        } else {
            val intent = Intent(this, SearchAlbumsActivity::class.java)
            intent.putExtra(searchAlbumsExtra, results)
            startActivity(intent)
        }
    }
}
