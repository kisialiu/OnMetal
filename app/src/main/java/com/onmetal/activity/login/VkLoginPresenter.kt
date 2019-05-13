package com.onmetal.activity.login

import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk


class VkLoginPresenter(var activity: AppCompatActivity) {
    fun configVkButton(btn: ImageView) {
        btn.setOnClickListener {
            VKSdk.login(activity, VKScope.EMAIL)
        }
    }
}