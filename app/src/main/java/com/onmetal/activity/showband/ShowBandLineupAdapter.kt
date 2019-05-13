package com.onmetal.activity.showband

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.onmetal.R
import com.onmetal.web.model.FullPerson
import kotlinx.android.synthetic.main.lineup_item.view.*

/**
 * Created by UladzimirKisialiou on 10/11/17.
 */
class ShowBandLineupAdapter(private val persons: List<FullPerson>) : RecyclerView.Adapter<ShowBandLineupAdapter.ResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ResultsViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.lineup_item, parent, false)
        return ResultsViewHolder(v)
    }

    override fun onBindViewHolder(holder: ResultsViewHolder?, position: Int) {
        holder?.bind(persons[position])
    }

    override fun getItemCount(): Int {
        return persons.size
    }

    class ResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var pd: ProgressBar = itemView.progressBar

        fun bind(person: FullPerson) {
            pd.visibility = View.VISIBLE
            itemView.logo.setImageBitmap(null)
            if (!person.photo.isEmpty()) {
                Glide.with(itemView.context)
                        .load(person.photo)
                        .into(itemView.logo)
            }
            pd.visibility = View.GONE
            itemView.name.text = person.name
        }

    }

}