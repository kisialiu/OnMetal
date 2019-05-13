package com.onmetal.activity.account

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.onmetal.R
import com.onmetal.activity.showband.ShowBandDiscClickListener
import com.onmetal.web.model.Album
import kotlinx.android.synthetic.main.account_info_albums.view.*

class LikedAlbumsAdapter(val discs: MutableList<Album>) : RecyclerView.Adapter<LikedAlbumsAdapter.ResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ResultsViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.account_info_albums, parent, false)
        return ResultsViewHolder(v)
    }

    override fun onBindViewHolder(holder: ResultsViewHolder?, position: Int) {
        holder?.bind(discs[position])
    }

    override fun getItemCount(): Int {
        return discs.size
    }

    class ResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(album: Album) {
            if (!album.album_cover.isEmpty()) {
                Glide.with(itemView.context)
                        .load(album.album_cover)
                        .into(itemView.logo)
            }
            itemView.albumName.context.apply {
                itemView.albumName.text = album.title
                itemView.bandName.text = album.bandName
            }
            itemView.setOnClickListener(ShowBandDiscClickListener(album.id))
        }

    }
}
