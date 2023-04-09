@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.adminapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import com.example.adminapp.ui.screens.AdminApp
import com.example.adminapp.ui.theme.AdminAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminAppTheme {
                AdminApp()
            }
        }
    }
}