package com.example.zeno

import android.app.Application
import com.example.zeno.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

import com.google.firebase.FirebaseApp
import org.koin.android.ext.android.inject
import com.example.zeno.core.billing.BillingManager

class ZenoApplication : Application() {
    private val billingManager: BillingManager by inject()
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        startKoin {
            androidContext(this@ZenoApplication)
            modules(appModule)
        }
        
        // Initialize RevenueCat
        billingManager.initialize()
    }
}
