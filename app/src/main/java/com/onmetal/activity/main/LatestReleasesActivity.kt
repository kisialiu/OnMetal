package com.onmetal.activity.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.beust.klaxon.*
import com.onmetal.R
import com.onmetal.activity.main.MainActivity
import com.onmetal.constant.buildGetDiscs
import com.onmetal.constant.buildGetLatestReleasesPaged
import com.onmetal.util.GridItemOffsetDecoration
import com.onmetal.util.ItemOffsetDecoration
import com.onmetal.web.model.Disc
import com.onmetal.web.model.DiscsSearchResults
import com.onmetal.web.okGet
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class LatestReleasesActivity : AppCompatActivity() {

    private val startPage = 0
    private var isLoading = false
    private var isLastPage = false
    private var totalPages = 0
    private var currentPage = startPage
    private lateinit var adapter: LatestReleasesViewAdapter

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_albums_results)

        val results: DiscsSearchResults = intent.getParcelableExtra(MainActivity.searchAlbumsExtra)

        val llm = GridLayoutManager(this, 2)
        adapter = LatestReleasesViewAdapter()
        val itemDec = GridItemOffsetDecoration(10, 5, 10, 5)

        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.setHasFixedSize(true)
        rv.layoutManager = llm
        rv.adapter = adapter
        rv.addItemDecoration(itemDec)
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = llm.itemCount
                val lastVisible = llm.findLastCompletelyVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if (lastVisible + 1 == totalItemCount) {
                        loadMoreItems()
                    }
                }
            }

            fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                loadNextPage(results)
            }
        })

        loadFirstPage(results)
    }

    private fun loadFirstPage(results: DiscsSearchResults) {
        totalPages = results.totalPages - 1
        adapter.addAll(results.results)

        if (currentPage == totalPages) {
            adapter.setLastPage(true)
            isLastPage = true
        }
    }

    private fun loadNextPage(res: DiscsSearchResults) {
        disposable = Single
                .fromCallable(Callable<DiscsSearchResults> {
                    val url = buildGetLatestReleasesPaged(currentPage.toString())
                    val response = url.okGet()
                    val result = Parser().parse(StringBuilder(response)) as JsonObject
                    val results: List<Disc>? = result.array<JsonObject>("content")?.map {
                        Disc(
                                it.string("name") ?: "",
                                it.string("id") ?: "",
                                it.string("year") ?: "",
                                it.string("type") ?: "")
                    }
                    return@Callable if (results != null) DiscsSearchResults(
                            results,
                            result.int("totalPages") ?: 0,
                            "") else null
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    isLoading = false
                    adapter.addAll(data.results)
                }, { e ->

                })

        if (currentPage == totalPages){
            adapter.setLastPage(true)
            isLastPage = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
