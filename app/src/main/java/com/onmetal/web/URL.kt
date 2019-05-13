package com.onmetal.web

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.net.URL

fun URL.okGet(): String {
    val client = OkHttpClient()
    val request = Request.Builder().url(this).build()
    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        return response.body()!!.string()
    }
    throw IOException("Can't get the response for $this")
}

fun URL.okPost(postBody: String): String {
    val client = OkHttpClient()
    val requestBody = RequestBody.create(MediaType.parse("application/json"), postBody)
    val request = Request.Builder().url(this).post(requestBody).build()
    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        return response.body()!!.string()
    }
    throw IOException("Can't get the response for $this")
}

fun URL.okPut(postBody: String?) {
    val client = OkHttpClient()
    val requestBody = RequestBody.create(MediaType.parse("application/json"), postBody ?: "")
    val request = Request.Builder().url(this).put(requestBody).build()
    client.newCall(request).execute()
}