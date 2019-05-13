package com.onmetal.activity.account

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.beust.klaxon.*
import com.onmetal.constant.buildGetBandByIdUrl
import com.onmetal.constant.buildGetDiscByIdUrl
import com.onmetal.constant.buildUserInfo
import com.onmetal.util.ItemOffsetDecoration
import com.onmetal.util.UserManager
import com.onmetal.util.getLikedAlbums
import com.onmetal.util.getLikedBands
import com.onmetal.web.model.*
import com.onmetal.web.okGet
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class AccountPresenter(var context: Context, private var offset: Int) {

    private var disposable: Disposable? = null

    fun setUpLiked(likedAlbumsRecyclerView: RecyclerView, likedBandsRecyclerView: RecyclerView/*, user: User*/) {
        val likedAlbums = getLikedAlbums(context)
        val likedBands = getLikedBands(context)

        Single.fromCallable(Callable<List<Album>> {
            val list = ArrayList<Album>()
            for(likedAlbum in likedAlbums) {
                val url = buildGetDiscByIdUrl(likedAlbum)
                val response = url.okGet()
                val result = Parser().parse(StringBuilder(response)) as JsonObject
                val discs: List<Song>? = result.array<JsonObject>("songs")?.map {
                    Song(
                            it.string("title") ?: "",
                            it.string("length") ?: ""
                    )
                }
                val album = Album(
                        result.string("id") ?: "",
                        result.string("title") ?: "",
                        result.obj("band")?.string("_id") ?: "",
                        result.obj("band")?.string("name") ?: "",
                        result.string("album_cover") ?: "",
                        result.string("type") ?: "",
                        result.string("releaseDate") ?: "",
                        result.string("label") ?: "",
                        result.string("format") ?: "",
                        discs ?: emptyList())
                list.add(album)
            }
            return@Callable list
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    likedAlbumsRecyclerView.adapter = LikedAlbumsAdapter(data.toMutableList())
                    finishLiked(likedAlbumsRecyclerView)
                }, { _ ->
                })

        Single.fromCallable(Callable<List<Band>> {
            val list = ArrayList<Band>()
            for(likedBand in likedBands) {
                val url = buildGetBandByIdUrl(likedBand)
                val response = url.okGet()
                val result = Parser().parse(StringBuilder(response)) as JsonObject
                val discs: List<Disc>? = result.array<JsonObject>("discography")?.map {
                    Disc(
                            it.string("name") ?: "",
                            it.string("id") ?: "",
                            it.string("year") ?: "",
                            it.string("type") ?: ""
                    )
                }
                val lineup: List<Person>? = result.array<JsonObject>("currentLineup")?.map {
                    Person(
                            it.string("name") ?: "",
                            it.string("id") ?: "",
                            it.array<String>("instruments").toString(),
                            it.boolean("current") ?: false
                    )
                }
                val band = Band(
                        result.string("id") ?: "",
                        result.string("bandName") ?: "",
                        result.string("logo") ?: "",
                        result.obj("details")?.string("country") ?: "",
                        result.obj("details")?.string("genre") ?: "",
                        result.obj("details")?.string("status") ?: "",
                        result.string("bio") ?: "",
                        discs ?: emptyList(),
                        lineup ?: emptyList())
                list.add(band)
            }
            return@Callable list
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    likedBandsRecyclerView.adapter = LikedBandsAdapter(data.toMutableList())
                    finishLiked(likedBandsRecyclerView)
                }, { _ ->
                })


//        disposable = Single
//                .fromCallable(Callable<UserInfo> {
//                    val url = buildUserInfo(user.email, user.type)
//                    val response = url.okGet()
//                    val result = Parser().parse(StringBuilder(response)) as JsonObject
//                    val albums = result.array<JsonObject>("albums")?.map {
//                        Album(
//                                it.string("id") ?: "",
//                                it.string("title") ?: "",
//                                it.obj("band")?.string("_id") ?: "",
//                                it.obj("band")?.string("name") ?: "",
//                                it.string("album_cover") ?: "",
//                                it.string("type") ?: "",
//                                it.string("releaseDate") ?: "",
//                                it.string("label") ?: "",
//                                it.string("format") ?: "",
//                                emptyList())
//                    }
//
//                    val bands = result.array<JsonObject>("bands")?.map {
//                        Band(
//                                it.string("id") ?: "",
//                                it.string("bandName") ?: "",
//                                it.string("logo") ?: "",
//                                it.obj("details")?.string("country") ?: "",
//                                it.obj("details")?.string("genre") ?: "",
//                                it.obj("details")?.string("status") ?: "",
//                                it.string("bio") ?: "",
//                                emptyList(),
//                                emptyList())
//                    }
//
//
//                    return@Callable UserInfo(albums?.toMutableList()
//                            ?: arrayListOf(), bands?.toMutableList() ?: arrayListOf())
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ data ->
//                    likedAlbumsRecyclerView.adapter = LikedAlbumsAdapter(data.likedAlbums)
//                    likedBandsRecyclerView.adapter = LikedBandsAdapter(data.likedBands)
//                    finishLiked(likedAlbumsRecyclerView)
//                    finishLiked(likedBandsRecyclerView)
//                }, { _ ->
//                })
    }

    private fun finishLiked(rv: RecyclerView) {
        val llm = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val itemDec = ItemOffsetDecoration(0, 0, offset, 0)
        rv.setHasFixedSize(true)
        rv.layoutManager = llm
        rv.addItemDecoration(itemDec)
    }

    fun dispose() {
        disposable?.dispose()
    }
}