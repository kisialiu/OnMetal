package com.onmetal.constant

import java.net.URL

val dbName = "onmetal.db"
val serviceTime: Long = 24 * 60 * 60 * 1000
private val base = "http://onmetalrest.us-east-2.elasticbeanstalk.com"
//private val base = "http://10.0.2.2:8080"

fun buildGetBandByIdUrl(id: String?) = URL("$base/band?id=$id")
fun buildGetSearchUrl(bandName: String?, page: String?) = URL("$base/search/bands?bandName=$bandName&page=$page&size=20")
fun buildGetSearchBandsByGenreUrl(genre: String?, page: String?) = URL("$base/search/bands?genre=$genre&page=$page&size=20")
fun buildGetDiscByIdUrl(id: String?) = URL("$base/disc?id=$id")
fun buildGetDiscs(title: String?, page: String?) = URL("$base/discs?title=$title&page=$page&size=40")
fun buildGetLatestReleases(count: String?) = URL("$base/discs/latest?count=$count")
fun buildGetLatestReleasesPaged(page: String?) = URL("$base/discs/latest/paged?page=$page&size=40")
fun buildPostUser() = URL("$base/users")
fun buildLikeAlbum(id: String?, like: String) = URL("$base/disc?id=$id&like=$like")
fun buildLikeBand(id: String?, like: String) = URL("$base/band?id=$id&like=$like")
fun buildGetCurrentLineup(bandId: String?) = URL("$base/band/lineup/current?bandId=$bandId")
fun buildTodayReleases() = URL("$base/band/todayReleases")
