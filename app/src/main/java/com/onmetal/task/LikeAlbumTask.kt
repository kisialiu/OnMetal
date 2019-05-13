package com.onmetal.task

import android.os.AsyncTask
import com.onmetal.constant.buildLikeAlbum
import com.onmetal.web.okPut

class LikeAlbumTask : AsyncTask<String, Unit, Unit>() {

    override fun doInBackground(vararg params: String?) {
        val url = buildLikeAlbum(params[0], params[1]!!)
        url.okPut(null)
    }
}
