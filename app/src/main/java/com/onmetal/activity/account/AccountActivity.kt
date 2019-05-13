package com.onmetal.activity.account

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.onmetal.R
import com.onmetal.activity.BaseActivity
import com.onmetal.util.UserManager

class AccountActivity : BaseActivity() {

    private lateinit var presenter: AccountPresenter
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        toolbar = findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        presenter = AccountPresenter(this, 20)
        presenter.setUpLiked(findViewById(R.id.likedAlbumsRv), findViewById(R.id.likedBandsRv))
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

}
