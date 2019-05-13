package com.onmetal.task

import android.os.AsyncTask
import com.onmetal.constant.buildLikeBand
import com.onmetal.web.okPut

class LikeBandTask : AsyncTask<String, Unit, Unit>() {

    override fun doInBackground(vararg params: String?) {
        val url = buildLikeBand(params[0], params[1]!!)
        url.okPut(null)
    }
}
