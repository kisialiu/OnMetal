package com.onmetal.task

import android.os.AsyncTask
import com.beust.klaxon.*
import com.onmetal.activity.showband.LineUpActivityListener
import com.onmetal.constant.buildGetCurrentLineup
import com.onmetal.web.model.FullPerson
import com.onmetal.web.model.SmallBand
import com.onmetal.web.okGet

class GetCurrentBandLineupTask(private var listener: LineUpActivityListener) : AsyncTask<String, Unit, List<FullPerson>?>() {

    override fun doInBackground(vararg params: String?): List<FullPerson>? {
        val url = buildGetCurrentLineup(params[0])
        val response = url.okGet()
        val result = Parser().parse(StringBuilder(response)) as JsonArray<JsonObject>
        return result.map {
            FullPerson(
                    it.string("id") ?: "",
                    it.string("name") ?: "",
                    it.obj("details")?.string("bio") ?: "",
                    it.obj("details")?.string("photo") ?: "",
                    it.obj("details")?.array<JsonObject>("active")!!.map {
                        SmallBand(
                                it.string("_id") ?: "",
                                it.string("name") ?: ""
                        )
                    },
                    it.obj("details")?.array<JsonObject>("past")!!.map {
                        SmallBand(
                                it.string("_id") ?: "",
                                it.string("name") ?: ""
                        )
                    },
                    it.obj("details")?.array<JsonObject>("guest")!!.map {
                        SmallBand(
                                it.string("_id") ?: "",
                                it.string("name") ?: ""
                        )
                    },
                    it.obj("details")?.string("realName") ?: "",
                    it.obj("details")?.string("age") ?: "",
                    it.obj("details")?.string("placeOfOrigin") ?: "",
                    it.obj("details")?.string("gender") ?: ""
            )
        }
    }

    override fun onPostExecute(result: List<FullPerson>?) {
        super.onPostExecute(result)
        listener.finish(result)
    }
}
