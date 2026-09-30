package com.example.zeno.features.studio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.core.NetworkUtils
import com.example.zeno.features.studio.data.repository.StudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class StudioType {
    MATERIAL, // ملزمة
    QUIZ      // اختبار
}

data class StudioUiState(
    val selectedType: StudioType = StudioType.MATERIAL,
    val subjects: List<String> = emptyList(),
    val selectedSubject: String = "",
    val availableLessons: List<String> = emptyList(),
    val selectedLessons: List<String> = emptyList(),
    val customLessonInput: String = "",
    val pagesCount: Int = 3,
    val isLoading: Boolean = false,
    val isFetchingSubjects: Boolean = false,
    val resultMarkdown: String? = null,
    val costDeducted: Int? = null,
    val errorMessage: String? = null
)

class StudioViewModel(
    private val studioRepository: StudioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    init {
        loadSubjects()
    }

    fun loadSubjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingSubjects = true) }
            val result = studioRepository.getStudentProfile()
            if (result.isSuccess) {
                val profile = result.getOrNull()
                val profileSubjects = profile?.subjects?.map { it.name }?.filter { it.isNotBlank() } ?: emptyList()
                val defaultSubjects = if (profileSubjects.isNotEmpty()) {
                    profileSubjects
                } else {
                    listOf("الرياضيات", "الفيزياء", "الكيمياء", "الأحياء", "اللغة العربية", "اللغة الإنجليزية")
                }
                
                _uiState.update { state ->
                    state.copy(
                        subjects = defaultSubjects,
                        selectedSubject = state.selectedSubject.ifBlank { defaultSubjects.firstOrNull() ?: "" },
                        isFetchingSubjects = false
                    )
                }
            } else {
                val fallbackSubjects = listOf("الرياضيات", "الفيزياء", "الكيمياء", "الأحياء", "اللغة العربية", "اللغة الإنجليزية")
                _uiState.update { state ->
                    state.copy(
                        subjects = fallbackSubjects,
                        selectedSubject = state.selectedSubject.ifBlank { fallbackSubjects.first() },
                        isFetchingSubjects = false
                    )
                }
            }
        }
    }

    fun selectType(type: StudioType) {
        _uiState.update { it.copy(selectedType = type, errorMessage = null) }
    }

    fun selectSubject(subject: String) {
        _uiState.update { it.copy(selectedSubject = subject, errorMessage = null) }
    }

    fun toggleLessonSelection(lesson: String) {
        _uiState.update { state ->
            val currentList = state.selectedLessons.toMutableList()
            if (currentList.contains(lesson)) {
                currentList.remove(lesson)
            } else {
                currentList.add(lesson)
            }
            state.copy(selectedLessons = currentList, errorMessage = null)
        }
    }

    fun addCustomLesson(lesson: String) {
        val trimmed = lesson.trim()
        if (trimmed.isBlank()) return
        _uiState.update { state ->
            val updatedLessons = state.selectedLessons.toMutableList()
            if (!updatedLessons.contains(trimmed)) {
                updatedLessons.add(trimmed)
            }
            state.copy(selectedLessons = updatedLessons, customLessonInput = "", errorMessage = null)
        }
    }

    fun updateCustomLessonInput(input: String) {
        _uiState.update { it.copy(customLessonInput = input) }
    }

    fun removeLesson(lesson: String) {
        _uiState.update { state ->
            val updated = state.selectedLessons.filterNot { it == lesson }
            state.copy(selectedLessons = updated)
        }
    }

    fun setPagesCount(pages: Int) {
        _uiState.update { it.copy(pagesCount = pages.coerceIn(1, 10)) }
    }

    fun generate() {
        val state = _uiState.value
        if (state.selectedSubject.isBlank()) {
            _uiState.update { it.copy(errorMessage = "يرجى اختيار المادة الدراسية") }
            return
        }

        val lessonsToSend = if (state.selectedLessons.isNotEmpty()) {
            state.selectedLessons
        } else if (state.customLessonInput.isNotBlank()) {
            listOf(state.customLessonInput.trim())
        } else {
            emptyList()
        }

        if (lessonsToSend.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "يرجى تحديد درس واحد على الأقل") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, resultMarkdown = null, costDeducted = null) }

        viewModelScope.launch {
            val result = if (state.selectedType == StudioType.MATERIAL) {
                studioRepository.generateMaterial(
                    subject = state.selectedSubject,
                    lessons = lessonsToSend,
                    pages = state.pagesCount
                )
            } else {
                studioRepository.generateQuiz(
                    subject = state.selectedSubject,
                    lessons = lessonsToSend
                )
            }

            if (result.isSuccess) {
                val response = result.getOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        resultMarkdown = response?.content ?: response?.message ?: "تم إنشاء المحتوى بنجاح.",
                        costDeducted = response?.costDeducted
                    )
                }
            } else {
                val ex = result.exceptionOrNull()
                val errText = ex?.let { NetworkUtils.getErrorMessage(it) } ?: ex?.message ?: "حدث خطأ أثناء الإنشاء."
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errText
                    )
                }
            }
        }
    }

    fun clearResult() {
        _uiState.update { it.copy(resultMarkdown = null, costDeducted = null, errorMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
