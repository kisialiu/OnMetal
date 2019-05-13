package com.onmetal.activity.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.onmetal.R
import com.onmetal.activity.main.MainActivity
import com.onmetal.util.UserManager
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError


class LogInActivity : AppCompatActivity() {

    private lateinit var fbPresenter: FbLoginPresenter
    private lateinit var googlePresenter: GoogleLoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        VkLoginPresenter(this)
                .configVkButton(
                        findViewById(R.id.vk_btn)
                )

        fbPresenter = FbLoginPresenter(this, {
            startMainActivity()
        })
        fbPresenter.configFbLogin(
                findViewById(R.id.login_button),
                findViewById(R.id.fb_btn)
        )

        googlePresenter = GoogleLoginPresenter(this, {
            startMainActivity()
        })
        googlePresenter.configGoogleLogin(findViewById(R.id.google_btn))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleLoginPresenter.RC_SIGN_IN) {
            googlePresenter.onActivityResult(data)
        } else if (!VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
                    override fun onResult(res: VKAccessToken) {
                        UserManager.putVkUser(res)
                        startMainActivity()
                    }

                    override fun onError(error: VKError) {
                        Log.DEBUG
                    }
                })) {
        }
        fbPresenter.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startMainActivity() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
