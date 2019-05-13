package com.onmetal.activity

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.onmetal.R
import com.onmetal.activity.account.AccountActivity
import com.onmetal.activity.login.LogInActivity
import com.onmetal.activity.main.MainActivity
import com.onmetal.util.UserManager
import com.onmetal.util.UserType
import com.onmetal.web.model.User
import com.vk.sdk.VKSdk

open class BaseActivity : AppCompatActivity()/*, NavigationView.OnNavigationItemSelectedListener*/ {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                if (this !is MainActivity) {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }

            R.id.account -> {
                if (this !is AccountActivity) {
                    startActivity(Intent(this, AccountActivity::class.java))
                }
            }
        }
        return false
    }

    protected fun setUpToolbar() {
        var toolbar = findViewById<Toolbar>(R.id.tool_bar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

//    private lateinit var toogle: ActionBarDrawerToggle
//    private lateinit var navView: NavigationView
//    private lateinit var drawer: DrawerLayout
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.base_layout)
//
//        var toolbar: Toolbar = findViewById(R.id.my_toolbar)
//        setSupportActionBar(toolbar)
//    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.navigation_items, menu)
//        return true
//    }

//    override fun setContentView(layout: Int) {
//        drawer = layoutInflater.inflate(R.layout.base_layout, null) as DrawerLayout
//        val activityContainer = drawer.findViewById(R.id.activity_content) as FrameLayout
//        layoutInflater.inflate(layout, activityContainer, true)
//        super.setContentView(layout)
//
//        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
//        setSupportActionBar(toolbar)
//        toogle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_open, R.string.nav_close)
//        drawer.addDrawerListener(toogle)
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        supportActionBar?.setHomeButtonEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        navView = findViewById(R.id.navVIew)
//        navView.setNavigationItemSelectedListener(this)
////        setHeaderData(UserManager.get())
//    }

//    private fun setHeaderData(user: User?) {
//        val item = navView.menu.getItem(2)
//        val header = navView.getHeaderView(0)
//        val photo = header.findViewById<ImageView>(R.id.user_photo)
//        val email = header.findViewById<TextView>(R.id.user_email)
//        if (user != null) {
//            Glide.with(this)
//                    .load(user.photoUrl)
//                    .into(photo)
//            email.text = user.email
//            item.title = "Sign Out"
//        } else {
//            photo.setImageBitmap(null)
//            email.text = ""
//            item.title = "Sign In"
//        }
//    }

//    override fun onPostCreate(savedInstanceState: Bundle?) {
//            super.onPostCreate(savedInstanceState)
//            toogle.syncState()
//    }

//    override fun onRestart() {
//        super.onRestart()
//        drawer.closeDrawers()
//    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        if (toogle.onOptionsItemSelected(item)) {
//            return true
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        when (id) {
//            R.id.nav_home -> {
//                if (this !is MainActivity) {
//                    startActivity(Intent(this, MainActivity::class.java))
//                }
//            }
//            R.id.logInOut -> {
//                if (item.title == "Sign Out") {
//                    val user = UserManager.get()
//                    if (user?.type == UserType.GOOGLE.type) {
//                        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut().addOnCompleteListener {
//                            setHeaderData(null)
//                        }
//                    } else if (user?.type == UserType.FACEBOOK.type) {
//                        LoginManager.getInstance().logOut()
//                        setHeaderData(null)
//                    } else if (user?.type == UserType.VK.type) {
//                        VKSdk.logout()
//                        setHeaderData(null)
//                    }
//                }
//                startActivity(Intent(this, LogInActivity::class.java))
//            }
//            R.id.account -> {
//                startActivity(Intent(this, AccountActivity::class.java))
//            }
//        }
//        return false
//    }

//    fun setLikeIcon(isLiked: Boolean, likeImage: ImageView) {
//        val animSet = AnimationSet(true)
//        animSet.interpolator = DecelerateInterpolator()
//        animSet.fillAfter = true
//        animSet.isFillEnabled = true
//
//        if (isLiked) {
//            val animRotate = RotateAnimation(0.0f, -45.0f,
//                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//                    RotateAnimation.RELATIVE_TO_SELF, 0.5f)
//            animRotate.duration = 1500
//            animRotate.fillAfter = true
//            animSet.addAnimation(animRotate)
//        } else {
//            val animRotate = RotateAnimation(-45.0f, -90.0f,
//                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//                    RotateAnimation.RELATIVE_TO_SELF, 0.5f)
//            animRotate.duration = 1500
//            animRotate.fillAfter = true
//            animSet.addAnimation(animRotate)
//        }
//        likeImage.startAnimation(animSet)
//    }

    fun getDisplaySize(): Point {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }
}
