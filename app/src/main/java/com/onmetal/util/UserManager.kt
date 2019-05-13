package com.onmetal.util

import android.content.Context
import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.onmetal.task.GetUserByEmailTask
import com.onmetal.task.LikeAlbumTask
import com.onmetal.task.LikeBandTask
import com.onmetal.task.UpdateUserTask
import com.onmetal.web.model.User
import com.vk.sdk.VKAccessToken
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKApiUser
import com.vk.sdk.api.model.VKList
import org.json.JSONArray
import org.json.JSONObject


object UserManager {
    private var activeUser: User? = null

    fun get(): User? = activeUser

    fun putFbUser(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
                accessToken
        ) { `object`, response ->
            val email = `object`.getString("email")
            val photo = `object`.getJSONObject("picture").getJSONObject("data").getString("url")

            val jsonObj = createJson(email, photo, UserType.FACEBOOK.type)
            val user = GetUserByEmailTask().execute(jsonObj.toString()).get()
            activeUser = user
            activeUser?.type = UserType.FACEBOOK.type
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email,picture")
        request.parameters = parameters
        request.executeAsync()
    }

    fun putGoogleUser(account: GoogleSignInAccount) {
        val jsonObj = createJson(account.email, account.photoUrl.toString(), UserType.GOOGLE.type)
        val user = GetUserByEmailTask().execute(jsonObj.toString()).get()
        activeUser = user
        activeUser?.type = UserType.GOOGLE.type
    }

    fun putVkUser(account: VKAccessToken) {
        val yourRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_50"))
        var photo: String? = ""
        yourRequest.executeSyncWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) {
                super.onComplete(response)
                val usersArray = response?.parsedModel as VKList<VKApiUser>
                photo = usersArray[0].photo_50
            }
        })
        val jsonObj = createJson(
                account.email,
                photo,
                UserType.VK.type
        )
        val user = GetUserByEmailTask().execute(jsonObj.toString()).get()
        activeUser = user
        activeUser?.type = UserType.VK.type
    }

    private fun createJson(email: String?, photo: String?, type: String?): JSONObject {
        val jsonObj = JSONObject()
        jsonObj.put("email", email)
        jsonObj.put("photoUrl", photo)
        jsonObj.put("type", type)
        return jsonObj
    }

    private fun createJsonFromUser(user: User?): JSONObject {
        val json = createJson(user?.email, user?.photoUrl, user?.type)
        json.put("likedBands", JSONArray(user?.likedBands))
        json.put("likedAlbums", JSONArray(user?.likedAlbums))
        return json
    }

//    fun likeBand(bandId: String) {
//        activeUser?.likedBands?.add(bandId)
//        UpdateUserTask().execute(createJsonFromUser(activeUser).toString())
//        LikeBandTask().execute(bandId, "true")
//    }

    fun likeBand(context: Context, bandId: String) {
        addLikedBand(context, bandId)
        LikeBandTask().execute(bandId, "true")
    }

//    fun unlikeBand(bandId: String) {
//        activeUser?.likedBands?.remove(bandId)
//        UpdateUserTask().execute(createJsonFromUser(activeUser).toString())
//        LikeBandTask().execute(bandId, "false")
//    }

    fun unlikeBand(context: Context, bandId: String) {
        removeLikedBand(context, bandId)
        LikeBandTask().execute(bandId, "false")
    }

//    fun isBandLiked(bandId: String): Boolean {
//        return activeUser?.likedBands?.contains(bandId) ?: false
//    }

    fun isBandLiked(context: Context, bandId: String): Boolean {
        return isBandLikedSQL(context, bandId)
    }

//    fun likeAlbum(albumId: String) {
//        activeUser?.likedAlbums?.add(albumId)
//        UpdateUserTask().execute(createJsonFromUser(activeUser).toString())
//        LikeAlbumTask().execute(albumId, "true")
//    }

    fun likeAlbum(context: Context, albumId: String) {
        addLikedAlbum(context, albumId)
        LikeAlbumTask().execute(albumId, "true")
    }

//    fun unlikeAlbum(albumId: String) {
//        activeUser?.likedAlbums?.remove(albumId)
//        UpdateUserTask().execute(createJsonFromUser(activeUser).toString())
//        LikeAlbumTask().execute(albumId, "false")
//    }

    fun unlikeAlbum(context: Context, albumId: String) {
        removeLikedAlbum(context, albumId)
        LikeAlbumTask().execute(albumId, "false")
    }

//    fun isAlbumLiked(albumId: String): Boolean {
//        return activeUser?.likedAlbums?.contains(albumId) ?: false
//    }

    fun isAlbumLiked(context: Context, albumId: String): Boolean {
        return isAlbumLikedSQL(context, albumId)
    }
}

enum class UserType(val type: String) {
    GOOGLE("google"),
    FACEBOOK("facebook"),
    VK("vkontakte")
}