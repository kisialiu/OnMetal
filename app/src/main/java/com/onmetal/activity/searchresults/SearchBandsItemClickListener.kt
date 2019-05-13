package com.onmetal.activity.searchresults

import android.content.Intent
import android.view.View
import com.onmetal.activity.main.MainActivity
import com.onmetal.activity.showband.ShowBandActivity
import com.onmetal.task.GetBandByIdTask
import java.util.concurrent.TimeUnit

class SearchBandsItemClickListener(var id: String) : View.OnClickListener {

    override fun onClick(v: View?) {
        val intent = Intent(v?.context, ShowBandActivity::class.java)
        val task = GetBandByIdTask()
        task.execute(id)
        val band = task.get(30, TimeUnit.SECONDS)
        intent.putExtra(MainActivity.bandExtra, band)
        v?.context?.startActivity(intent)
    }
}