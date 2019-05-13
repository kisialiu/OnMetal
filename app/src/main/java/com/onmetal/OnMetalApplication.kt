package com.onmetal

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import com.vk.sdk.VKSdk

class OnMetalApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        VKSdk.initialize(this)
        MobileAds.initialize(this, "ca-app-pub-6052092604388790~2471756880")
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }
}