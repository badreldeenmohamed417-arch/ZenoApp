package com.example.zeno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.zeno.data.AppColors
import com.example.zeno.futures.auth.MailSentVerifyScreen
import com.example.zeno.futures.main.MainScreen
import com.example.zeno.futures.setup.SetupGrade
import com.example.zeno.futures.setup.SetupProfile
import com.example.zeno.ui.theme.ZenoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).background(AppColors.BG)) {
                        SetupGrade (
                            {},
                        )
                    }
                }
            }
        }
    }
}