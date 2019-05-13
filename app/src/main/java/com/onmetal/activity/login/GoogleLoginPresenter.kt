package com.onmetal.activity.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.onmetal.util.UserManager

class GoogleLoginPresenter(var activity: AppCompatActivity, private var startMainActivity: () -> Unit) {

    companion object {
        const val RC_SIGN_IN = 1
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    fun configGoogleLogin(realGoogleBtn: ImageView) {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(activity, options)

        realGoogleBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            activity.startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    fun onActivityResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        handleGoogleSignInResult(task)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            UserManager.putGoogleUser(account)
            startMainActivity.invoke()
        } catch (e: ApiException) {
//            updateUI(null)
        }
    }

}