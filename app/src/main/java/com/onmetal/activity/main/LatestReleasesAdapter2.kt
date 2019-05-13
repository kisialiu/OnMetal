package com.onmetal.activity.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.beust.klaxon.*
import com.bumptech.glide.Glide
import com.onmetal.activity.showband.ShowBandDiscClickListener
import com.onmetal.task.getAlbum
import com.onmetal.web.model.Disc
import kotlinx.android.synthetic.main.release_item.view.*
import com.onmetal.R
import com.onmetal.constant.buildGetLatestReleasesPaged
import com.onmetal.web.model.DiscsSearchResults
import com.onmetal.web.okGet
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable

class LatestReleasesAdapter2(val discs: List<Disc>, val item: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val logosCache = HashMap<String, JsonObject>()
    private val content = 1
    private val button = 2

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            content -> {
                val v = LayoutInflater.from(parent?.context).inflate(item, parent, false)
                (v as CardView).preventCornerOverlap = false
                ResultsViewHolder(v, logosCache)
            }
            else -> {
                val v = LayoutInflater.from(parent?.context).inflate(R.layout.release_item_button, parent, false)
                ButtonViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if(position == discs.size) {
            (holder as? ButtonViewHolder)?.bind()
        } else {
            (holder as? ResultsViewHolder)?.bind(discs[position])
        }
    }

    override fun getItemCount(): Int {
        return discs.size + 1
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder?) {
        super.onViewDetachedFromWindow(holder)
        if (holder is ResultsViewHolder) {
            (holder as? ResultsViewHolder)?.unbind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == discs.size) button else content
    }

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val btn = itemView.findViewById<ImageView>(R.id.moreButton)
            btn.setOnClickListener {
                val pb = itemView.findViewById<ProgressBar>(R.id.moreProgressBar)
                btn.visibility = View.GONE
                pb.visibility = View.VISIBLE
                Single
                        .fromCallable(Callable<DiscsSearchResults> {
                            val url = buildGetLatestReleasesPaged("0")
                            val response = url.okGet()
                            val result = Parser().parse(StringBuilder(response)) as JsonObject
                            val results: List<Disc>? = result.array<JsonObject>("content")?.map {
                                Disc(
                                        it.string("name") ?: "",
                                        it.string("id") ?: "",
                                        it.string("year") ?: "",
                                        it.string("type") ?: "")
                            }
                            return@Callable if (results != null) DiscsSearchResults(
                                    results,
                                    result.int("totalPages") ?: 0,
                                    "") else null
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ data ->
                            btn.visibility = View.VISIBLE
                            pb.visibility = View.GONE
                            val intent = Intent(itemView.context, LatestReleasesActivity::class.java)
                            intent.putExtra(MainActivity.searchAlbumsExtra, data)
                            itemView.context.startActivity(intent)
                        }, { e ->

                        })
            }
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
                itemView.albumName.text = album?.string("title")
                itemView.bandName?.text = album?.obj("band")?.string("name")
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