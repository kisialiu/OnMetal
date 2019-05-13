package com.onmetal.web.model

import android.os.Parcel
import android.os.Parcelable

data class SearchResults(
        var results: List<SearchResult>,
        var totalPages: Int,
        var searchField: String) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(results)
        parcel.writeInt(totalPages)
        parcel.writeString(searchField)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchResults> {
        override fun createFromParcel(parcel: Parcel): SearchResults {
            return SearchResults(parcel)
        }

        override fun newArray(size: Int): Array<SearchResults?> {
            return arrayOfNulls(size)
        }
    }

    private constructor(parcel: Parcel) : this(
            ArrayList<SearchResult>().apply {
                parcel.readTypedList<SearchResult>(this, SearchResult.CREATOR)
            },
            totalPages = parcel.readInt(),
            searchField = parcel.readString()
    )
}

data class DiscsSearchResults(
        var results: List<Disc>,
        var totalPages: Int,
        var searchField: String) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(results)
        parcel.writeInt(totalPages)
        parcel.writeString(searchField)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DiscsSearchResults> {
        override fun createFromParcel(parcel: Parcel): DiscsSearchResults {
            return DiscsSearchResults(parcel)
        }

        override fun newArray(size: Int): Array<DiscsSearchResults?> {
            return arrayOfNulls(size)
        }
    }

    private constructor(parcel: Parcel) : this(
            ArrayList<Disc>().apply {
                parcel.readTypedList<Disc>(this, Disc.CREATOR)
            },
            totalPages = parcel.readInt(),
            searchField = parcel.readString()
    )
}

data class SearchResult(
        var name: String,
        var id: String,
        var genre: String,
        var country: String) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
        parcel.writeString(genre)
        parcel.writeString(country)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchResult> {
        override fun createFromParcel(parcel: Parcel): SearchResult = SearchResult(parcel)
        override fun newArray(size: Int): Array<SearchResult?> = arrayOfNulls(size)
    }

    private constructor(source: Parcel) : this(
            name = source.readString(),
            id = source.readString(),
            genre = source.readString(),
            country = source.readString()
    )
}

data class Band(
        var id: String,
        var name: String,
        var logo: String,
        var country: String,
        var genre: String,
        var status: String,
        var bio: String,
        var discs: List<Disc>,
        var lineup: List<Person>) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(logo)
        parcel.writeString(country)
        parcel.writeString(genre)
        parcel.writeString(status)
        parcel.writeString(bio)
        parcel.writeTypedList(discs)
        parcel.writeTypedList(lineup)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Band> {
        override fun createFromParcel(parcel: Parcel): Band = Band(parcel)

        override fun newArray(size: Int): Array<Band?> = arrayOfNulls(size)
    }

    private constructor(source: Parcel) : this(
            id = source.readString(),
            name = source.readString(),
            logo = source.readString(),
            country = source.readString(),
            genre = source.readString(),
            status = source.readString(),
            bio = source.readString(),
            discs = source.createTypedArrayList(Disc.CREATOR),
            lineup = source.createTypedArrayList(Person.CREATOR)
    )

}

data class Disc(
        var name: String,
        var id: String,
        var year: String,
        var type: String) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(year)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Disc> {
        override fun createFromParcel(parcel: Parcel): Disc = Disc(parcel)
        override fun newArray(size: Int): Array<Disc?> = arrayOfNulls(size)
    }

    private constructor(source: Parcel) : this(
            id = source.readString(),
            name = source.readString(),
            year = source.readString(),
            type = source.readString()
    )

}

data class Person(
        var name: String,
        var id: String,
        var instruments: String,
        var current: Boolean) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(instruments)
        parcel.writeValue(current)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Person> {
        override fun createFromParcel(parcel: Parcel): Person = Person(parcel)
        override fun newArray(size: Int): Array<Person?> = arrayOfNulls(size)
    }

    private constructor(source: Parcel) : this(
            id = source.readString(),
            name = source.readString(),
            instruments = source.readString(),
            current = source.readValue(null) as Boolean
    )

}

data class SmallBand(
        var id: String,
        var name: String) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SmallBand> {
        override fun createFromParcel(parcel: Parcel): SmallBand = SmallBand(parcel)
        override fun newArray(size: Int): Array<SmallBand?> = arrayOfNulls(size)
    }

    private constructor(source: Parcel) : this(
            id = source.readString(),
            name = source.readString()
    )

}

data class FullPerson(
        var id: String,
        var name: String,
        var bio: String,
        var photo: String,
        var active: List<SmallBand>,
        var past: List<SmallBand>,
        var guest: List<SmallBand>,
        var realName: String,
        var age: String,
        var placeOfOrigin: String,
        var gender: String) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(bio)
        parcel.writeString(photo)
        parcel.writeTypedList(active)
        parcel.writeTypedList(past)
        parcel.writeTypedList(guest)
        parcel.writeString(realName)
        parcel.writeString(age)
        parcel.writeString(placeOfOrigin)
        parcel.writeString(gender)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FullPerson> {
        override fun createFromParcel(parcel: Parcel): FullPerson = FullPerson(parcel)
        override fun newArray(size: Int): Array<FullPerson?> = arrayOfNulls(size)
    }

    private constructor(source: Parcel) : this(
            id = source.readString(),
            name = source.readString(),
            bio = source.readString(),
            photo = source.readString(),
            active = source.createTypedArrayList(SmallBand.CREATOR),
            past = source.createTypedArrayList(SmallBand.CREATOR),
            guest = source.createTypedArrayList(SmallBand.CREATOR),
            realName = source.readString(),
            age = source.readString(),
            placeOfOrigin = source.readString(),
            gender = source.readString()
    )

}

data class Song(
        var title: String,
        var length: String) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(length)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song = Song(parcel)

        override fun newArray(size: Int): Array<Song?> = arrayOfNulls(size)
    }

    private constructor(source: Parcel) : this(
            title = source.readString(),
            length = source.readString()
    )

}

data class Album(
        var id: String,
        var title: String,
        var bandId: String,
        var bandName: String,
        var album_cover: String,
        var type: String,
        var releaseDate: String,
        var label: String,
        var format: String,
        var songs: List<Song>) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(bandId)
        parcel.writeString(bandName)
        parcel.writeString(album_cover)
        parcel.writeString(type)
        parcel.writeString(releaseDate)
        parcel.writeString(label)
        parcel.writeString(format)
        parcel.writeTypedList(songs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Album> {
        override fun createFromParcel(parcel: Parcel): Album = Album(parcel)

        override fun newArray(size: Int): Array<Album?> = arrayOfNulls(size)
    }

    private constructor(source: Parcel) : this(
            id = source.readString(),
            title = source.readString(),
            bandId = source.readString(),
            bandName = source.readString(),
            album_cover = source.readString(),
            type = source.readString(),
            releaseDate = source.readString(),
            label = source.readString(),
            format = source.readString(),
            songs = source.createTypedArrayList(Song.CREATOR)
    )

    fun toDisc(): Disc {
        return Disc(title, id, releaseDate, type)
    }

}

data class User(
        var id: String,
        var email: String,
        var photoUrl: String,
        var likedAlbums: MutableList<String>,
        var likedBands: MutableList<String>,
        var type: String) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(email)
        parcel.writeString(photoUrl)
        parcel.writeStringList(likedAlbums)
        parcel.writeStringList(likedBands)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User = User(parcel)

        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
    }

    private constructor(source: Parcel) : this(
            id = source.readString(),
            email = source.readString(),
            photoUrl = source.readString(),
            likedAlbums = source.createStringArrayList(),
            likedBands = source.createStringArrayList(),
            type = source.readString()
    )

}

data class UserInfo(
        var likedAlbums: MutableList<Album>,
        var likedBands: MutableList<Band>) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(likedAlbums)
        parcel.writeTypedList(likedBands)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        override fun createFromParcel(parcel: Parcel): UserInfo = UserInfo(parcel)

        override fun newArray(size: Int): Array<UserInfo?> = arrayOfNulls(size)
    }

    private constructor(source: Parcel) : this(
            likedAlbums = source.createTypedArrayList(Album.CREATOR),
            likedBands = source.createTypedArrayList(Band.CREATOR)
    )

}
