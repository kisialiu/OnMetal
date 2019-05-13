package com.onmetal.activity.account

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.onmetal.R
import com.onmetal.activity.searchresults.SearchBandsItemClickListener
import com.onmetal.web.model.Band
import kotlinx.android.synthetic.main.account_info_bands.view.*

class LikedBandsAdapter(val bands: MutableList<Band>) : RecyclerView.Adapter<LikedBandsAdapter.ResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ResultsViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.account_info_bands, parent, false)
        return ResultsViewHolder(v)
    }

    override fun onBindViewHolder(holder: ResultsViewHolder?, position: Int) {
        holder?.bind(bands[position])
    }

    override fun getItemCount(): Int {
        return bands.size
    }

    class ResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(band: Band) {
            if (!band.logo.isEmpty()) {
                Glide.with(itemView.context)
                        .load(band.logo)
                        .into(itemView.logo)
            }
            itemView.bandName.context.apply {
                itemView.bandName.text = band.name
                itemView.country.text = band.country
            }
            itemView.setOnClickListener(SearchBandsItemClickListener(band.id))
        }

    }
}
