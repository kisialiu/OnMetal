package com.onmetal.task

import android.os.AsyncTask
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.array
import com.beust.klaxon.string
import com.onmetal.constant.buildPostUser
import com.onmetal.web.model.User
import com.onmetal.web.okPost

class GetUserByEmailTask : AsyncTask<String, Unit, User?>() {

    override fun doInBackground(vararg params: String?): User? {
        val url = buildPostUser()
        val response = url.okPost(params.get(0)!!)
        val result = Parser().parse(StringBuilder(response)) as JsonObject
        val user: User? =
                User(
                        result.string("id") ?: "",
                        result.string("email") ?: "",
                        result.string("photoUrl") ?: "",
                        result.array<String>("likedAlbums") ?: arrayListOf(),
                        result.array<String>("likedBands") ?: arrayListOf(),
                        result.string("type") ?: ""
                )
        return user
    }
}
