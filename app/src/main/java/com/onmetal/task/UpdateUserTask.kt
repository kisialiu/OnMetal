package com.onmetal.task

import android.os.AsyncTask
import com.onmetal.constant.buildPostUser
import com.onmetal.web.okPut

class UpdateUserTask : AsyncTask<String, Unit, Unit>() {

    override fun doInBackground(vararg params: String?) {
        val url = buildPostUser()
        url.okPut(params[0]!!)
    }
}
