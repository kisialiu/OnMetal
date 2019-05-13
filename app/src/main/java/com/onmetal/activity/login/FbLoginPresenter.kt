package com.onmetal.activity.login

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.onmetal.util.UserManager

class FbLoginPresenter(var context: Context, var startMainActivity: () -> Unit) {

    private lateinit var callbackManager: CallbackManager

    fun configFbLogin(fbLoginButton: LoginButton, realFbBtn: ImageView) {
        setPermissions(fbLoginButton)

        realFbBtn.setOnClickListener {
            fbLoginButton.performClick()
        }

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        UserManager.putFbUser(loginResult.accessToken)
                        startMainActivity.invoke()
                    }

                    override fun onCancel() {
                        // App code
                    }

                    override fun onError(exception: FacebookException) {
                        Log.DEBUG
                    }
                })
    }

    private fun setPermissions(fbLoginButton: LoginButton) {
        fbLoginButton.setReadPermissions("email")
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

}