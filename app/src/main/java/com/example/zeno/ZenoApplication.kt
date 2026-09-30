package com.example.zeno

import android.app.Application
import com.example.zeno.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

import com.google.firebase.FirebaseApp
import com.example.zeno.data.server.ApiClient

class ZenoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize ApiClient
        ApiClient.initialize(this)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        startKoin {
            androidContext(this@ZenoApplication)
            modules(appModule)
        }
        
    }
}
