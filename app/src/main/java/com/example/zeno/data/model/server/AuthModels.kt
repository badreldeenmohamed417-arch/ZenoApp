package com.example.zeno.data.model.server

import com.google.gson.annotations.SerializedName

// 1. طلب التسجيل الأساسي (بريد وكلمة مرور فقط)
data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("id") val id: String
)

// 2. طلب استكمال بيانات المستخدم (بعد التسجيل أو بعد أول تسجيل دخول بـ Google)
data class CompleteDataRequest(
    @SerializedName("country") val country: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("grade") val grade: String? = null,
    @SerializedName("school_system") val schoolSystem: String? = null,
    @SerializedName("language") val language: String = "ar"
)

// 3. تسجيل الدخول التقليدي
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("device_name") val deviceName: String? = null,
    @SerializedName("platform") val platform: String? = null
)

// 4. تسجيل الدخول بـ Google (تطابق id_token المطلوبة في AuthService)
data class GoogleLoginRequest(
    @SerializedName("id_token") val idToken: String
)

// 5. استجابة التوكنات (مضاف إليها is_new_user لـ Google Auth)
data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("is_new_user") val isNewUser: Boolean? = null
)

data class RefreshRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("language") val language: String = "ar"
)

data class ResetPasswordRequest(
    @SerializedName("token") val token: String,
    @SerializedName("new_password") val newPassword: String
)

// --- الإضافات الجديدة الخاصة بالتحقق من الإيميل ---

// 6. طلب تأكيد البريد الإلكتروني عبر التوكن
data class VerifyEmailRequest(
    @SerializedName("token") val token: String
)

// 7. طلب إعادة إرسال رابط التوثيق
data class ResendVerificationRequest(
    @SerializedName("email") val email: String,
    @SerializedName("language") val language: String = "ar"
)

// 8. استجابة عامة للعمليات التي تُرجع success و message
data class SimpleResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)