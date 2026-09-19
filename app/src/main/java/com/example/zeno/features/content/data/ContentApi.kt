package com.example.zeno.features.content.data

import com.example.zeno.features.content.data.dto.AssessmentQuestionDTO
import com.example.zeno.features.content.data.dto.SubjectDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface ContentApi {
    @GET("main/content/subjects")
    suspend fun getSubjects(): List<SubjectDTO>

    @GET("main/content/subjects/{subject_id}/assessment")
    suspend fun getAssessment(
        @Path("subject_id") subjectId: String
    ): List<AssessmentQuestionDTO>
}
