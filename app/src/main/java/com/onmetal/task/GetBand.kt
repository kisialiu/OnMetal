package com.onmetal.task

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.onmetal.constant.buildGetBandByIdUrl
import com.onmetal.constant.buildGetDiscByIdUrl
import com.onmetal.web.okGet

/**
 * Created by UladzimirKisialiou on 10/12/17.
 */
fun getBand(id: String?): JsonObject? {
    val url = buildGetBandByIdUrl(id)
    val response = url.okGet()
    return Parser().parse(StringBuilder(response)) as JsonObject
}

fun getAlbum(id: String?): JsonObject? {
    val url = buildGetDiscByIdUrl(id)
    val response = url.okGet()
    if (response.isNotEmpty()) {
        return Parser().parse(StringBuilder(response)) as JsonObject
    }
    return null
}