package com.onmetal.task

import android.os.AsyncTask
import com.beust.klaxon.*
import com.onmetal.activity.searchalbums.SearchAlbumsLoader
import com.onmetal.constant.buildGetDiscs
import com.onmetal.web.model.Disc
import com.onmetal.web.model.DiscsSearchResults
import com.onmetal.web.okGet

class GetDiscsByTitleTask(private val searchResultsLoader: SearchAlbumsLoader) : AsyncTask<String, Unit, DiscsSearchResults?>() {

    override fun doInBackground(vararg params: String?): DiscsSearchResults? {
        val url = buildGetDiscs(params[0], params[1])
        val response = url.okGet()
        val result = Parser().parse(StringBuilder(response)) as JsonObject
        val results: List<Disc>? = result.array<JsonObject>("content")?.map {
            Disc(
                    it.string("name") ?: "",
                    it.string("id") ?: "",
                    it.string("year") ?: "",
                    it.string("type") ?: "")
        }
        return if (results != null) DiscsSearchResults(
                results,
                result.int("totalPages") ?: 0,
                params[0]!!) else null
    }

    override fun onPostExecute(result: DiscsSearchResults?) {
        super.onPostExecute(result)
        searchResultsLoader.loadSearchResultsActivity(result)
    }
}
