package com.onmetal.activity.searchalbums

import com.onmetal.web.model.DiscsSearchResults

interface SearchAlbumsLoader {
    fun loadSearchResultsActivity(results: DiscsSearchResults?)
}