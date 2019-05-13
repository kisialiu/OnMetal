package com.onmetal.task

import android.os.AsyncTask
import com.beust.klaxon.*
import com.onmetal.constant.buildGetDiscByIdUrl
import com.onmetal.web.model.Album
import com.onmetal.web.model.Song
import com.onmetal.web.okGet

class GetAlbumByIdTask : AsyncTask<String, Unit, Album?>() {

    override fun doInBackground(vararg params: String?): Album? {
        val url = buildGetDiscByIdUrl(params.get(0))
        val response = url.okGet()
        val result = Parser().parse(StringBuilder(response)) as JsonObject
        val discs: List<Song>? = result.array<JsonObject>("songs")?.map {
            Song(
                    it.string("title") ?: "",
                    it.string("length") ?: ""
            )
        }
        return Album(
                result.string("id") ?: "",
                result.string("title") ?: "",
                result.obj("band")?.string("_id") ?: "",
                result.obj("band")?.string("name") ?: "",
                result.string("album_cover") ?: "",
                result.string("type") ?: "",
                result.string("releaseDate") ?: "",
                result.string("label") ?: "",
                result.string("format") ?: "",
                discs ?: emptyList())
    }
}
