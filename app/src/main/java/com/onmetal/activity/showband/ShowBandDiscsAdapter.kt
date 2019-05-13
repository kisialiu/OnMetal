package com.onmetal.activity.showband

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beust.klaxon.JsonObject
import com.beust.klaxon.string
import com.bumptech.glide.Glide
import com.onmetal.R
import com.onmetal.task.getAlbum
import com.onmetal.web.model.Disc
import kotlinx.android.synthetic.main.disc.view.*

/**
 * Created by UladzimirKisialiou on 10/11/17.
 */
class ShowBandDiscsAdapter(val discs: List<Disc>) : RecyclerView.Adapter<ShowBandDiscsAdapter.ResultsViewHolder>() {

    private val logosCache = HashMap<String, JsonObject>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ResultsViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.disc, parent, false)
        val vh = ResultsViewHolder(v, logosCache)
        return vh
    }

    override fun onBindViewHolder(holder: ResultsViewHolder?, position: Int) {
        holder?.bind(discs[position])
    }

    override fun getItemCount(): Int {
        return discs.size
    }

    override fun onViewDetachedFromWindow(holder: ShowBandDiscsAdapter.ResultsViewHolder?) {
        super.onViewDetachedFromWindow(holder)
        holder?.unbind()
    }

    class ResultsViewHolder(itemView: View, private val logosCache: HashMap<String, JsonObject>) : RecyclerView.ViewHolder(itemView) {

        private var task: AsyncTask<String, Unit, JsonObject?>? = null

        fun finish(album: JsonObject?) {
            if (!album?.string("album_cover").isNullOrEmpty()) {
                Glide.with(itemView.context)
                        .load(album?.string("album_cover"))
                        .into(itemView.discCover)
            }
        }

        @SuppressLint("StaticFieldLeak")
        fun bind(disc: Disc) {
            task?.cancel(true)
            itemView.discCover.setImageBitmap(null)

            if (logosCache.containsKey(disc.id)) {
                finish(logosCache[disc.id])
            } else {
                task = object : AsyncTask<String, Unit, JsonObject?>() {
                    override fun doInBackground(vararg params: String?): JsonObject? {
                        return getAlbum(params[0])
                    }

                    override fun onPostExecute(result: JsonObject?) {
                        super.onPostExecute(result)

                        result?.let { band ->
                            band
                                    .string("id")?.let {
                                        logosCache.put(it, band)
                                    }
                        }

                        if (isCancelled) {
                            return
                        }

                        finish(result)
                    }
                }.apply {
                    executeOnExecutor(THREAD_POOL_EXECUTOR, disc.id)
                }
            }

            itemView.discName.context.apply {
                itemView.discName.text = disc.name
                itemView.discYear.text = "Year: ${disc.year}"
                itemView.discType.text = "Type: ${disc.type}"
            }

            itemView.setOnClickListener(ShowBandDiscClickListener(disc.id))
        }

        fun unbind() {
            task?.cancel(true)
        }

    }

}