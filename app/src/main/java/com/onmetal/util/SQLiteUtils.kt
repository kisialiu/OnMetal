package com.onmetal.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.onmetal.constant.dbName

private val likedBandsTable = "likedBands"
private val likedAlbumsTable = "likedAlbums"

fun getDb(context: Context): SQLiteDatabase {
    return context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null)
}

fun addLikedBand(context: Context, bandId: String) {
    addLiked(context, bandId, likedBandsTable)
}

fun addLikedAlbum(context: Context, albumId: String) {
    addLiked(context, albumId, likedAlbumsTable)
}

fun removeLikedBand(context: Context, bandId: String) {
    removeLiked(context, bandId, likedBandsTable)
}

fun removeLikedAlbum(context: Context, albumId: String) {
    removeLiked(context, albumId, likedAlbumsTable)
}

fun isBandLikedSQL(context: Context, bandId: String): Boolean {
    return isLiked(context, bandId, likedBandsTable)
}

fun isAlbumLikedSQL(context: Context, albumId: String): Boolean {
    return isLiked(context, albumId, likedAlbumsTable)
}

fun getLikedBands(context: Context): List<String> {
    return getLiked(context, likedBandsTable)
}

fun getLikedAlbums(context: Context): List<String> {
    return getLiked(context, likedAlbumsTable)
}

private fun createTable(context: Context, dbName: String) {
    getDb(context).execSQL("CREATE TABLE IF NOT EXISTS $dbName (_id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT)")
}

private fun addLiked(context: Context, id: String, dbName: String) {
    val row = ContentValues()
    row.put("id", id)

    createTable(context, dbName)
    getDb(context).insert(dbName, null, row)
}

private fun removeLiked(context: Context, id: String, dbName: String) {
    getDb(context).delete(dbName, "id=$id", null)
}

private fun isLiked(context: Context, id: String, dbName: String): Boolean {
    createTable(context, dbName)
    val cursor = getDb(context).rawQuery("select id from $dbName", null)
    while (cursor.moveToNext()) {
        val cid = cursor.getString(0)
        if (cid == id) {
            return true
        }
    }
    return false
}

private fun getLiked(context: Context, dbName: String): List<String> {
    val list = ArrayList<String>()

    createTable(context, dbName)
    val cursor = getDb(context).rawQuery("select id from $dbName", null)
    while (cursor.moveToNext()) {
        list.add(cursor.getString(0))
    }

    return list
}