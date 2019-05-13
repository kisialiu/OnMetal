package com.onmetal.task

import android.os.AsyncTask
import com.beust.klaxon.*
import com.onmetal.activity.searchresults.SearchBandsLoader
import com.onmetal.constant.buildGetSearchUrl
import com.onmetal.web.model.SearchResult
import com.onmetal.web.model.SearchResults
import com.onmetal.web.okGet

/**
 * Created by UladzimirKisialiou on 10/11/17.
 */

class GetBandsByNameTask(private val searchBandsLoader: SearchBandsLoader) : AsyncTask<String, Unit, SearchResults?>() {

    override fun doInBackground(vararg params: String?): SearchResults? {
        val url = buildGetSearchUrl(params[0], params[1])
        val response = url.okGet()
        val result = Parser().parse(StringBuilder(response)) as JsonObject
        val results: List<SearchResult>? = result.array<JsonObject>("content")?.map {
            SearchResult(
                    it.string("name") ?: "",
                    it.string("id") ?: "",
                    it.string("genre") ?: "",
                    it.string("country") ?: "")
        }
        return if (results != null) SearchResults(
                results,
                result.int("totalPages") ?: 0,
                params[0]!!) else null
    }

    override fun onPostExecute(result: SearchResults?) {
        super.onPostExecute(result)
        searchBandsLoader.loadSearchResultsActivity(result, false)
    }
}
