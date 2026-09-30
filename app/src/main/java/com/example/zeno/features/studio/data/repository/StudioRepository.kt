package com.example.zeno.features.studio.data.repository

import com.example.zeno.features.studio.data.StudioApi
import com.example.zeno.features.studio.data.dto.GenerateMaterialRequest
import com.example.zeno.features.studio.data.dto.GenerateQuizRequest
import com.example.zeno.features.studio.data.dto.StudioResponse
import com.example.zeno.features.student.data.repository.StudentRepository
import com.example.zeno.features.student.data.dto.ProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StudioRepository(
    private val studioApi: StudioApi,
    private val studentRepository: StudentRepository
) {
    suspend fun getStudentProfile(): Result<ProfileResponse> = studentRepository.getProfile()

    suspend fun generateMaterial(subject: String, lessons: List<String>, pages: Int): Result<StudioResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = studioApi.generateMaterial(
                    GenerateMaterialRequest(subject = subject, lessons = lessons, pages = pages)
                )
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun generateQuiz(subject: String, lessons: List<String>): Result<StudioResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = studioApi.generateQuiz(
                    GenerateQuizRequest(subject = subject, lessons = lessons)
                )
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
