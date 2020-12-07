package com.seagazer.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun horizontal(view: View) {
        startActivity(Intent(this, HorizontalActivity::class.java))
    }

    fun vertical(view: View) {
        startActivity(Intent(this, VerticalActivity::class.java))
    }
}