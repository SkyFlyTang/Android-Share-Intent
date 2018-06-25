package com.sky.android.share

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sky.share.ShareFragmentDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_test.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "sdfsdfsdfsdfsdf")
            intent.type = "text/plain"
            ShareFragmentDialog.shareIntent(fragmentManager, intent)
        }
    }
}
