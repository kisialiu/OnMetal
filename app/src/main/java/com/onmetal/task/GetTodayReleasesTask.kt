package com.onmetal.task

import android.content.Context
import android.os.AsyncTask
import com.beust.klaxon.*
import com.onmetal.activity.alarm.AlarmReceiver
import com.onmetal.constant.buildTodayReleases
import com.onmetal.web.okGet

class GetTodayReleasesTask(var alarmReceiver: AlarmReceiver, var context: Context) : AsyncTask<String, Unit, List<String>>() {

    override fun doInBackground(vararg params: String?): List<String> {
        val url = buildTodayReleases()
        val response = url.okGet()
        val result = Parser().parse(StringBuilder(response)) as JsonArray<JsonObject>
        return result.map { it.obj("band")?.string("_id") ?: "" }
    }

    override fun onPostExecute(result: List<String>) {
        super.onPostExecute(result)
        alarmReceiver.sendNotification(context, result)
    }
}
