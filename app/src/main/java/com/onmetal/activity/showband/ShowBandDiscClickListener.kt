package com.onmetal.activity.showband

import android.content.Intent
import android.view.View
import com.onmetal.activity.main.MainActivity
import com.onmetal.activity.showalbum.ShowAlbumActivity
import com.onmetal.task.GetAlbumByIdTask
import com.onmetal.web.model.Album
import java.util.concurrent.TimeUnit

class ShowBandDiscClickListener(var id: String) : View.OnClickListener {

    var album: Album? = null

    override fun onClick(v: View?) {
        val intent = Intent(v?.context, ShowAlbumActivity::class.java)
        val task = GetAlbumByIdTask()
        task.execute(id)
        album = task.get(30, TimeUnit.SECONDS)
        intent.putExtra(MainActivity.albumExtra, album)
        v?.context?.startActivity(intent)
    }
}