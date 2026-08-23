package com.example.zeno.data.local

import android.content.Context

class UserManager(context: Context) {

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