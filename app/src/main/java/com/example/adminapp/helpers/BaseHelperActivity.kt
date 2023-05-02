package com.example.adminapp.helpers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseHelperActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = intent.getStringExtra("name")
    }
}