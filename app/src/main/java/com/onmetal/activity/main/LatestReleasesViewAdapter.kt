package com.onmetal.activity.main

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beust.klaxon.JsonObject
import com.beust.klaxon.obj
import com.beust.klaxon.string
import com.bumptech.glide.Glide
import com.onmetal.R
import com.onmetal.activity.showband.ShowBandDiscClickListener
import com.onmetal.task.getAlbum
import com.onmetal.web.model.Disc
import kotlinx.android.synthetic.main.search_album_item.view.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by UladzimirKisialiou on 10/11/17.
 */
class LatestReleasesViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val logosCache = HashMap<String, JsonObject>()
    private val discs = ArrayList<Disc>()

    private val content = 1
    private val loading = 2
    private var lastPage = false

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            content -> {
                val v = LayoutInflater.from(parent?.context).inflate(R.layout.search_album_item, parent, false)
                (v as CardView).preventCornerOverlap = false
                ResultsViewHolder(v, logosCache)
            }
            else -> {
                val v = LayoutInflater.from(parent?.context).inflate(R.layout.search_album_item_loading, parent, false)
                LoadingHolder(v)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if(position != discs.size) {
            (holder as? ResultsViewHolder)?.bind(discs[position])
        } else {
            (holder as? LoadingHolder)?.bind()
        }
    }

    override fun getItemCount(): Int {
        return discs.size
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder?) {
        super.onViewDetachedFromWindow(holder)
        if (holder is ResultsViewHolder) {
            holder.unbind()
        }
    }

    fun add(result: Disc) {
        discs.add(result)
        notifyItemInserted(discs.size - 1)
    }

    fun addAll(results: List<Disc>) {
        results.forEach {
            add(it)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == discs.size - 1 && !lastPage) loading else content
    }

    fun setLastPage(isLast: Boolean) {
        lastPage = isLast
    }

    class LoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {

        }
    }

    class ResultsViewHolder(itemView: View, private val logosCache: HashMap<String, JsonObject>) : RecyclerView.ViewHolder(itemView) {

        private var task: AsyncTask<String, Unit, JsonObject?>? = null

        fun finish(album: JsonObject?) {
            if (!album?.string("album_cover").isNullOrEmpty()) {
                Glide.with(itemView.context)
                        .load(album?.string("album_cover"))
                        .into(itemView.logo)
            }
            itemView.albumName.context.apply {
                itemView.albumName.text = album?.obj("band")?.string("name")
                itemView.bandName.text = album?.string("title")
                val date = Date(album?.string("releaseDate"))
                val format = SimpleDateFormat("E MMM d yyyy")
                itemView.releaseDate.text = format.format(date)
            }
        }

        @SuppressLint("StaticFieldLeak")
        fun bind(disc: Disc) {
            task?.cancel(true)
            itemView.logo.setImageBitmap(null)

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
            itemView.setOnClickListener(ShowBandDiscClickListener(disc.id))
        }

        fun unbind() {
            task?.cancel(true)
        }

    }

}