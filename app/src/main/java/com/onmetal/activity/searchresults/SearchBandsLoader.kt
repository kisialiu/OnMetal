package com.onmetal.activity.searchresults

import com.onmetal.web.model.SearchResults

/**
 * Created by UladzimirKisialiou on 10/11/17.
 */
interface SearchBandsLoader {
    fun loadSearchResultsActivity(results: SearchResults?, isGenre: Boolean)
}