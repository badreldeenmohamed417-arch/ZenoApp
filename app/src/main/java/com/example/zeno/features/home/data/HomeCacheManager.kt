package com.example.zeno.features.home.data

import android.content.Context
import com.example.zeno.features.home.data.dto.ProgressOverviewResponse
import com.google.gson.Gson
import java.util.Calendar

class HomeCacheManager(context: Context) {
    private val prefs = context.getSharedPreferences("home_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveProgress(progress: ProgressOverviewResponse) {
        val old = getProgress()
        val merged = if (old != null && progress.date == old.date) {
            progress.copy(
                minutesToday = maxOf(progress.minutesToday ?: 0, old.minutesToday ?: 0),
                questionsAskedToday = maxOf(progress.questionsAskedToday ?: 0, old.questionsAskedToday ?: 0)
            )
        } else progress

        prefs.edit()
            .putString("progress_data", gson.toJson(merged))
            .putLong("last_saved_time", System.currentTimeMillis())
            .apply()
    }

    fun getProgress(): ProgressOverviewResponse? {
        val json = prefs.getString("progress_data", null)
        if (json != null) {
            return try {
                gson.fromJson(json, ProgressOverviewResponse::class.java)
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    fun addLocalStudyMinutes(minutes: Int) {
        val current = getProgress() ?: return
        val newMinutes = (current.minutesToday ?: 0) + minutes
        
        val weekly = current.weeklyStudyTime?.toMutableMap() ?: mutableMapOf()
        val calendar = Calendar.getInstance()
        val currentDayStr = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> "س"
            Calendar.SUNDAY -> "ح"
            Calendar.MONDAY -> "ن"
            Calendar.TUESDAY -> "ث"
            Calendar.WEDNESDAY -> "ر"
            Calendar.THURSDAY -> "خ"
            Calendar.FRIDAY -> "ج"
            else -> ""
        }
        if (currentDayStr.isNotEmpty()) {
            weekly[currentDayStr] = (weekly[currentDayStr] ?: 0) + minutes
        }
        
        val updated = current.copy(
            minutesToday = newMinutes,
            weeklyStudyTime = weekly
        )
        saveProgress(updated)
    }

    fun addLocalQuestions(count: Int) {
        val current = getProgress() ?: return
        val updated = current.copy(questionsAskedToday = (current.questionsAskedToday ?: 0) + count)
        saveProgress(updated)
    }
}
