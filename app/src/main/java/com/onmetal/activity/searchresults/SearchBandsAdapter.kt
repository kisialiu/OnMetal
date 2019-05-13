package com.onmetal.activity.searchresults

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.beust.klaxon.JsonObject
import com.beust.klaxon.string
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onmetal.R
import com.onmetal.task.getBand
import com.onmetal.web.model.SearchResult
import kotlinx.android.synthetic.main.item.view.*

class SearchBandsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val logosCache = HashMap<String, JsonObject>()
    private var searchResults = ArrayList<SearchResult>()

    private val content = 1
    private val loading = 2
    private var lastPage = false

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        return when(viewType) {
            content -> {
                val v = LayoutInflater.from(parent?.context).inflate(R.layout.item, parent, false)
                ResultsViewHolder(v, logosCache)
            }
            else -> {
                val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_loading, parent, false)
                LoadingHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if(position != searchResults.size) {
            (holder as? ResultsViewHolder)?.bind(searchResults[position])
        } else {
            (holder as? LoadingHolder)?.bind()
        }
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder?) {
        super.onViewDetachedFromWindow(holder)
        if (holder is ResultsViewHolder) {
            holder.unbind()
        }
    }

    fun add(result: SearchResult) {
        searchResults.add(result)
        notifyItemInserted(searchResults.size - 1)
    }

    fun addAll(results: List<SearchResult>) {
        results.forEach {
            add(it)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == searchResults.size - 1 && !lastPage) loading else content
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
        private var pd: ProgressBar = itemView.bandLogoProgressBar

        fun finish(band: JsonObject?) {
            pd.visibility = View.GONE
            if (!band?.string("logo").isNullOrEmpty()) {
                Glide.with(itemView.context)
                        .load(band?.string("logo"))
                        .apply(RequestOptions().placeholder(R.color.placeholder_color))
                        .into(itemView.logo)
            }
        }

        @SuppressLint("StaticFieldLeak")
        fun bind(result: SearchResult) {
            task?.cancel(true)
            itemView.logo.setImageBitmap(null)

            if (logosCache.containsKey(result.id)) {
                finish(logosCache[result.id])
            } else {
                task = object : AsyncTask<String, Unit, JsonObject?>() {
                    override fun onPreExecute() {
                        super.onPreExecute()
                        pd.visibility = View.VISIBLE
                    }

                    override fun doInBackground(vararg params: String?): JsonObject? {
                        return getBand(params[0])
                    }

                    override fun onPostExecute(result: JsonObject?) {
                        super.onPostExecute(result)

                        result?.let { band ->
                            band.string("id")?.let {
                                logosCache.put(it, band)
                            }
                        }

                        if (isCancelled) {
                            return
                        }

                        finish(result)
                    }
                }.apply {
                    executeOnExecutor(THREAD_POOL_EXECUTOR, result.id)
                }
            }

            itemView.albumName.context.apply {
                itemView.albumName.text = getString(R.string.name, result.name)
                itemView.textView4.text = getString(R.string.genre, result.genre)
                itemView.textView5.text = getString(R.string.country, result.country)
            }

            itemView.setOnClickListener(SearchBandsItemClickListener(result.id))
        }

        fun unbind() {
            task?.cancel(true)
        }

    }
}