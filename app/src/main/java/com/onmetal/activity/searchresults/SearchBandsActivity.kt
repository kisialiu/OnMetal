package com.onmetal.activity.searchresults

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.beust.klaxon.*
import com.onmetal.R
import com.onmetal.activity.BaseActivity
import com.onmetal.activity.main.MainActivity
import com.onmetal.constant.buildGetSearchBandsByGenreUrl
import com.onmetal.constant.buildGetSearchUrl
import com.onmetal.util.ItemOffsetDecoration
import com.onmetal.web.model.SearchResult
import com.onmetal.web.model.SearchResults
import com.onmetal.web.okGet
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class SearchBandsActivity : BaseActivity() {

    private val startPage = 0
    private var isLoading = false
    private var isLastPage = false
    private var totalPages = 0
    private var currentPage = startPage
    private lateinit var adapter: SearchBandsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        setUpToolbar()

        val results: SearchResults = intent.getParcelableExtra(MainActivity.searchBandsExtra)
        val isGenre: Boolean = intent.getBooleanExtra(MainActivity.searchBandsIsGenre, false)

        val llm = LinearLayoutManager(this)
        adapter = SearchBandsAdapter()
        val itemDec = ItemOffsetDecoration(5)

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
                loadNextPage(results, isGenre)
            }
        })

        loadFirstPage(results)
    }

    private fun loadFirstPage(results: SearchResults) {
        totalPages = results.totalPages - 1
        adapter.addAll(results.results)

        if (currentPage == totalPages) {
            adapter.setLastPage(true)
            isLastPage = true
        }
    }

    private fun loadNextPage(res: SearchResults, isGenre: Boolean) {
        Single
                .fromCallable(Callable<SearchResults> {
                    val url = if (isGenre) buildGetSearchBandsByGenreUrl(res.searchField, currentPage.toString())
                    else buildGetSearchUrl(res.searchField, currentPage.toString())

                    val response = url.okGet()
                    val result = Parser().parse(StringBuilder(response)) as JsonObject
                    val results: List<SearchResult>? = result.array<JsonObject>("content")?.map {
                        SearchResult(
                                it.string("name") ?: "",
                                it.string("id") ?: "",
                                it.string("genre") ?: "",
                                it.string("country") ?: "")
                    }
                    return@Callable if (results != null) SearchResults(
                            results,
                            result.int("totalPages") ?: 0,
                            res.searchField) else null
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    isLoading = false
                    adapter.addAll(data.results)
                }, { e ->

                })

        if (currentPage == totalPages) {
            adapter.setLastPage(true)
            isLastPage = true
        }
    }
}
