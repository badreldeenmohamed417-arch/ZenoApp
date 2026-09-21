package com.example.zeno.core.di

import com.example.zeno.core.billing.BillingManager
import com.example.zeno.core.config.data.ConfigApi
import com.example.zeno.core.config.data.repository.ConfigRepository
import com.example.zeno.core.data.AuthStorage
import com.example.zeno.core.data.EncryptedAuthStorageImpl
import com.example.zeno.core.network.AuthInterceptor
import com.example.zeno.core.network.RetrofitClient
import com.example.zeno.core.network.TokenAuthenticator
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.auth.data.AuthApi
import com.example.zeno.features.auth.data.AuthRepository
import com.example.zeno.features.chat.data.ChatApi
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.home.data.ProgressApi
import com.example.zeno.features.home.data.repository.ProgressRepository
import com.example.zeno.features.premium.data.SubscriptionApi
import com.example.zeno.features.premium.data.repository.SubscriptionRepository
import com.example.zeno.features.session.data.SessionApi
import com.example.zeno.features.session.data.StudyPlanApi
import com.example.zeno.features.session.data.repository.SessionRepository
import com.example.zeno.features.session.data.repository.StudyPlanRepository
import com.example.zeno.features.student.data.StudentApi
import com.example.zeno.features.student.data.repository.StudentRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

val appModule = module {
    // Core & Storage
    single<AuthStorage> { EncryptedAuthStorageImpl(get()) }
    single { UserManager(get()) }
    single { BillingManager(get(), get()) }

    // Network (Interceptors & Retrofit)
    single { AuthInterceptor(get()) }
    single { TokenAuthenticator(get(), get(), RetrofitClient.MAIN_SERVER_BASE_URL) }
    
    single(named("MainRetrofit")) { 
        RetrofitClient.createMainServerRetrofit(get(), get()) 
    }
    single(named("AiRetrofit")) { 
        RetrofitClient.createAiServerRetrofit(get(), get()) 
    }

    // APIs
    single<AuthApi> { get<retrofit2.Retrofit>(named("MainRetrofit")).create(AuthApi::class.java) }
    single<StudentApi> { get<retrofit2.Retrofit>(named("MainRetrofit")).create(StudentApi::class.java) }
    single<ProgressApi> { get<retrofit2.Retrofit>(named("MainRetrofit")).create(ProgressApi::class.java) }
    single<ChatApi> { get<retrofit2.Retrofit>(named("AiRetrofit")).create(ChatApi::class.java) }
    single<SessionApi> { get<retrofit2.Retrofit>(named("MainRetrofit")).create(SessionApi::class.java) }
    single<StudyPlanApi> { get<retrofit2.Retrofit>(named("MainRetrofit")).create(StudyPlanApi::class.java) }
    single<SubscriptionApi> { get<retrofit2.Retrofit>(named("MainRetrofit")).create(SubscriptionApi::class.java) }
    single<ConfigApi> { get<retrofit2.Retrofit>(named("MainRetrofit")).create(ConfigApi::class.java) }

    // Repositories
    single { AuthRepository(get(), get(), get(), androidContext()) }
    single { StudentRepository(get()) }
    single { com.example.zeno.features.home.data.HomeCacheManager(androidContext()) }
    single { ProgressRepository(get(), get()) }
    single { ChatRepository(get()) }
    single { SessionRepository(get(), get()) }
    single { StudyPlanRepository(get()) }
    single { SubscriptionRepository(get()) }
    single { ConfigRepository(get()) }
    
    // Legacy Repositories
    single(named("legacyChat")) { 
        com.example.zeno.data.repository.ChatRepository(
            com.example.zeno.data.local.db.AppDatabase.getDatabase(get()).chatDao(), 
            com.example.zeno.data.server.ApiClient.chat()
        ) 
    }
    
    // ViewModels
    viewModel { com.example.zeno.features.profile.presentation.ProfileViewModel(androidApplication(), get()) }
    viewModel { com.example.zeno.features.assessment.presentation.AssessmentChatViewModel(get(), get(), get()) }
    viewModel { com.example.zeno.features.premium.presentation.PremiumViewModel(androidApplication(), get()) }
    viewModel { com.example.zeno.features.session.presentation.SessionsViewModel(androidApplication(), get(), get()) }
    viewModel { com.example.zeno.features.chat.presentation.ChatViewModel(androidApplication(), get(), get()) }
    viewModel { com.example.zeno.features.home.presentation.HomeViewModel(androidApplication(), get()) }
    viewModel { com.example.zeno.features.auth.presentation.EmailVerificationViewModel(androidApplication(), get(), get()) }
}
