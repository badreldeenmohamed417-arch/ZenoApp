package com.example.zeno.data.local

import android.content.Context
import com.example.zeno.data.model.server.Subject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserManager(context: Context) {

    private val gson = Gson()
    private val preferences = context.getSharedPreferences(
        "zeno_user",
        Context.MODE_PRIVATE
    )

    fun saveAcademicData(
        grade: String?,
        schoolSystem: String?
    ) {
        preferences.edit()
            .putString("grade", grade)
            .putString("school_system", schoolSystem)
            .apply()
    }

    fun saveProfileData(
        displayName: String?,
        birthDate: String?
    ) {
        preferences.edit()
            .putString("display_name", displayName)
            .putString("birth_date", birthDate)
            .apply()
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

    fun getDisplayName(): String? {
        return preferences.getString("display_name", null)
    }

    fun getBirthDate(): String? {
        return preferences.getString("birth_date", null)
    }

    fun clearUserData() {
        preferences.edit().clear().apply()
    }
}