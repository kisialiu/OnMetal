package com.onmetal.task

import android.os.AsyncTask
import com.beust.klaxon.*
import com.onmetal.constant.buildGetBandByIdUrl
import com.onmetal.web.model.Band
import com.onmetal.web.model.Disc
import com.onmetal.web.model.Person
import com.onmetal.web.okGet

class GetBandByIdTask : AsyncTask<String, Unit, Band?>() {

    override fun doInBackground(vararg params: String?): Band? {
        val url = buildGetBandByIdUrl(params.get(0))
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
        return Band(
                result.string("id") ?: "",
                result.string("bandName") ?: "",
                result.string("logo") ?: "",
                result.obj("details")?.string("country") ?: "",
                result.obj("details")?.string("genre") ?: "",
                result.obj("details")?.string("status") ?: "",
                result.string("bio") ?: "",
                discs ?: emptyList(),
                lineup ?: emptyList())
    }
}
