package com.onmetal.activity.showalbum

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.onmetal.R
import com.onmetal.web.model.Song
import kotlinx.android.synthetic.main.song.view.*

/**
 * Created by UladzimirKisialiou on 10/11/17.
 */
class ShowAlbumSongsAdapter(private val songs: List<Song>) : RecyclerView.Adapter<ShowAlbumSongsAdapter.ResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ResultsViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.song, parent, false)
        return ResultsViewHolder(v)
    }

    override fun onBindViewHolder(holder: ResultsViewHolder?, position: Int) {
        holder?.bind(songs[position])
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    class ResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(song: Song) {
            itemView.songTitle.context.apply {
                itemView.songTitle.text = song.title
                itemView.songLength.text = song.length
            }
        }

    }

}