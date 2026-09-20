package com.example.zeno.data.local

import android.content.Context
import com.example.zeno.data.model.server.Subject
import com.example.zeno.features.assessment.data.LearningProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

class UserManager(context: Context) {

    private val gson = Gson()
    private val preferences = context.getSharedPreferences(
        "zeno_user",
        Context.MODE_PRIVATE
    )

    fun saveAcademicData(
        grade: String?,
        schoolSystem: String?,
        track: String? = null
    ) {
        preferences.edit()
            .putString("grade", grade)
            .putString("school_system", schoolSystem)
            .putString("track", track)
            .apply()
    }

    fun saveProfileData(
        displayName: String?,
        birthDate: String?,
        country: String? = null,
        email: String? = null
    ) {
        preferences.edit()
            .putString("display_name", displayName)
            .putString("birth_date", birthDate)
            .apply {
                if (country != null) putString("country", country)
                if (email != null) putString("email", email)
            }
            .apply()
    }

    fun saveDisplayName(name: String?) {
        preferences.edit().putString("display_name", name).apply()
    }

    fun saveEmail(email: String?) {
        preferences.edit().putString("email", email).apply()
    }

    fun getEmail(): String? {
        return preferences.getString("email", null)
    }

    fun saveThemeModeString(mode: String) {
        preferences.edit().putString("theme_mode", mode).apply()
    }

    fun getThemeModeString(): String {
        return preferences.getString("theme_mode", "system") ?: "system"
    }

    fun saveThemeMode(isDark: Boolean) {
        saveThemeModeString(if (isDark) "dark" else "light")
    }

    fun getThemeMode(systemDefault: Boolean): Boolean {
        return when (getThemeModeString()) {
            "dark" -> true
            "light" -> false
            else -> systemDefault
        }
    }

    fun saveNotificationsEnabled(enabled: Boolean) {
        preferences.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return preferences.getBoolean("notifications_enabled", true)
    }

    fun saveFocusModeEnabled(enabled: Boolean) {
        preferences.edit().putBoolean("focus_mode_enabled", enabled).apply()
    }

    fun isFocusModeEnabled(): Boolean {
        return preferences.getBoolean("focus_mode_enabled", false)
    }

    fun saveLanguage(lang: String) {
        preferences.edit().putString("app_language", lang).apply()
    }

    fun getLanguage(): String {
        val saved = preferences.getString("app_language", null)
        if (saved != null) return saved
        val sysLang = Locale.getDefault().language
        return if (sysLang == "ar") "ar" else "en"
    }

    fun saveInitialLanguageSelected(selected: Boolean) {
        preferences.edit().putBoolean("is_initial_language_selected", selected).apply()
    }

    fun isInitialLanguageSelected(): Boolean {
        return preferences.getBoolean("is_initial_language_selected", false)
    }

    fun saveVerificationStatus(isVerified: Boolean) {
        preferences.edit()
            .putBoolean("is_verified", isVerified)
            .apply()
    }

    fun getVerificationStatus(): Boolean {
        return preferences.getBoolean("is_verified", false)
    }

    fun saveSubjects(subjects: List<Subject>) {
        val json = gson.toJson(subjects)
        preferences.edit()
            .putString("subjects", json)
            .apply()
    }

    fun getSubjects(): List<Subject> {
        val json = preferences.getString("subjects", null) ?: return emptyList()
        val type = object : TypeToken<List<Subject>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getGrade(): String? {
        return preferences.getString("grade", null)
    }

    fun getSchoolSystem(): String? {
        return preferences.getString("school_system", null)
    }

    fun getTrack(): String? {
        return preferences.getString("track", null)
    }

    fun getDisplayName(): String? {
        return preferences.getString("display_name", null)
    }

    fun getCountry(): String? {
        return preferences.getString("country", "EG") // Default to EG
    }

    fun getBirthDate(): String? {
        return preferences.getString("birth_date", null)
    }

    fun saveLearningProfile(profile: LearningProfile) {
        val json = gson.toJson(profile)
        preferences.edit()
            .putString("learning_profile", json)
            .putBoolean("is_assessment_completed", profile.isAssessmentCompleted)
            .apply()
    }

    fun getLearningProfile(): LearningProfile? {
        val json = preferences.getString("learning_profile", null) ?: return null
        return try {
            gson.fromJson(json, LearningProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun isAssessmentCompleted(): Boolean {
        return preferences.getBoolean("is_assessment_completed", false)
    }

    fun saveOnboardingStatus(isOnboarded: Boolean) {
        preferences.edit()
            .putBoolean("is_onboarded", isOnboarded)
            .apply()
    }

    fun isOnboarded(): Boolean {
        return preferences.getBoolean("is_onboarded", false)
    }

    fun getStudentContextForAi(): String {
        val profile = getLearningProfile()
        val name = getDisplayName() ?: "الطالب"
        val grade = getGrade() ?: "غير محدد"
        val system = getSchoolSystem() ?: "غير محدد"
        val track = getTrack() ?: ""

        val builder = StringBuilder()
        builder.append("سياق الطالب الحالي:\n")
        builder.append("- الاسم: $name\n")
        builder.append("- المرحلة الدراسية: $grade ($system ${if (track.isNotBlank()) "- $track" else ""})\n")

        if (profile != null) {
            if (profile.difficultSubjects.isNotEmpty()) {
                builder.append("- المواد المستهدفة والشاقة: ${profile.difficultSubjects.joinToString(", ")}\n")
            }
            if (profile.focusAreas.isNotEmpty()) {
                builder.append("- المجالات المحتاجة تركيز وصعوبة: ${profile.focusAreas.joinToString(", ")}\n")
            }
            if (profile.preferredExplanationStyle.isNotBlank()) {
                builder.append("- أسلوب الشرح المفضل: ${profile.preferredExplanationStyle}\n")
            }
            if (profile.strengths.isNotEmpty()) {
                builder.append("- نقاط القوة: ${profile.strengths.joinToString(", ")}\n")
            }
        }
        return builder.toString()
    }

    fun clearUserData() {
        preferences.edit().clear().apply()
    }

    fun saveCurrentChatId(id: String?) {
        preferences.edit().putString("current_chat_id", id).apply()
    }

    fun getCurrentChatId(): String? {
        return preferences.getString("current_chat_id", null)
    }

    fun saveProStatus(isPro: Boolean) {
        preferences.edit().putBoolean("is_pro", isPro).apply()
    }

    fun isPro(): Boolean {
        return preferences.getBoolean("is_pro", false)
    }

    fun saveSubscriptionPlan(planId: String?, planTitle: String?) {
        preferences.edit()
            .putString("subscription_plan_id", planId)
            .putString("subscription_plan_title", planTitle)
            .apply()
    }

    fun getSubscriptionPlanId(): String {
        return preferences.getString("subscription_plan_id", "free") ?: "free"
    }

    fun getSubscriptionPlanTitle(isArabic: Boolean = true): String {
        val stored = preferences.getString("subscription_plan_title", null)
        if (!stored.isNullOrBlank()) return stored.replace(Regex("[\\p{So}\\p{Sk}\\p{Sm}\\p{Sc}\\u200D\\uFE0F]+"), "").trim()
        val planId = getSubscriptionPlanId().lowercase()
        return when {
            planId.contains("6month") || planId.contains("legend") || planId.contains("الأسطورة") -> if (isArabic) "الأسطورة" else "Legend"
            planId.contains("3month") || planId.contains("champ") || planId.contains("المتفوق") -> if (isArabic) "المتفوق" else "Champion"
            planId.contains("month") || planId.contains("achieve") || planId.contains("المثابر") -> if (isArabic) "المثابر" else "Achiever"
            else -> if (isArabic) "طالب مجتهد" else "Diligent Student"
        }
    }
}