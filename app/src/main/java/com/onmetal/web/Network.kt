package com.onmetal.web

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AlertDialog

fun showNoNetworkAlert(activity: Activity) {
    AlertDialog.Builder(activity).setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again")
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .setIcon(android.R.drawable.ic_dialog_alert).show()
}

fun isNetworkConnected(activity: Activity): Boolean {
    val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}