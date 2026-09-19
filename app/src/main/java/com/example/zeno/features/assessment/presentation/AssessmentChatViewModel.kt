package com.example.zeno.features.assessment.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.R
import com.example.zeno.core.config.data.repository.ConfigRepository
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.assessment.data.AssessmentChatMessage
import com.example.zeno.features.assessment.data.EnglishSkillsProfile
import com.example.zeno.features.assessment.data.LearningProfile
import com.example.zeno.features.assessment.data.MessageSender
import com.example.zeno.features.session.data.repository.StudyPlanRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssessmentChatViewModel(
    private val context: Context,
    private val studyPlanRepository: StudyPlanRepository,
    private val configRepository: ConfigRepository
) : ViewModel() {

    private val userManager = UserManager(context)
    private val isArabic = userManager.getLanguage() == "ar"

    private val _messages = MutableStateFlow<List<AssessmentChatMessage>>(emptyList())
    val messages: StateFlow<List<AssessmentChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _learningProfile = MutableStateFlow(LearningProfile())
    val learningProfile: StateFlow<LearningProfile> = _learningProfile.asStateFlow()

    private var currentStep = 0
    private var selectedSubject = ""
    private var selectedStudyMinutes = 180

    // Dynamic config cache
    private val config = configRepository.getConfig()

    init {
        startGreetingFlow()
    }

    private fun startGreetingFlow() {
        viewModelScope.launch {
            _isTyping.value = true
            delay(600)
            val name = userManager.getDisplayName()?.takeIf { it.isNotBlank() }
            val greetingText = if (isArabic) {
                if (name != null) context.getString(R.string.assessment_greeting_name, name)
                else context.getString(R.string.assessment_greeting)
            } else {
                if (name != null) context.getString(R.string.assessment_greeting_name_en, name)
                else context.getString(R.string.assessment_greeting_en)
            }

            addMessage(
                AssessmentChatMessage(
                    sender = MessageSender.ZENO,
                    text = greetingText
                )
            )

            val subjects = userManager.getSubjects().map { it.name }.take(5).toMutableList()
            if (subjects.isEmpty()) {
                val dynamicSubjects = config?.defaultSubjects
                if (!dynamicSubjects.isNullOrEmpty()) {
                    subjects.addAll(dynamicSubjects)
                } else {
                    subjects.addAll(listOf("Math 🔢", "Arabic 📚", "English 🇬🇧", "Science 🔬"))
                }
            }
            subjects.add(if (isArabic) "auto_str_مادة_أخرى" else "Other Subject")

            delay(1000)
            addMessage(
                AssessmentChatMessage(
                    sender = MessageSender.ZENO,
                    text = context.getString(R.string.assessment_q1),
                    quickReplies = subjects
                )
            )
            _isTyping.value = false
        }
    }

    fun handleUserAnswer(userAnswer: String) {
        if (userAnswer.isBlank() || _isTyping.value) return

        val currentList = _messages.value.toMutableList()
        val updatedList = currentList.map { msg ->
            if (msg.quickReplies.isNotEmpty()) msg.copy(quickReplies = emptyList()) else msg
        }.toMutableList()

        updatedList.add(
            AssessmentChatMessage(
                sender = MessageSender.USER,
                text = userAnswer.trim()
            )
        )
        _messages.value = updatedList

        viewModelScope.launch {
            _isTyping.value = true
            delay(1000)

            when (currentStep) {
                0 -> {
                    selectedSubject = userAnswer.replace(Regex("[^\\p{L}\\p{N}\\s]"), "").trim()
                    _learningProfile.value = _learningProfile.value.copy(
                        difficultSubjects = listOf(if (selectedSubject.isNotBlank()) selectedSubject else userAnswer)
                    )
                    currentStep = 1

                    val options = config?.assessmentOptions?.focusAreas ?: listOf("Understanding Concepts 🧠", "Memorization 📖", "Solving ✍️")

                    addMessage(
                        AssessmentChatMessage(
                            sender = MessageSender.ZENO,
                            text = context.getString(R.string.assessment_q2, selectedSubject),
                            quickReplies = options
                        )
                    )
                }

                1 -> {
                    _learningProfile.value = _learningProfile.value.copy(
                        focusAreas = listOf(userAnswer)
                    )
                    currentStep = 2

                    val styles = config?.assessmentOptions?.explanationStyles ?: listOf("Detailed 📝", "Practical 💡", "Concise ⚡")

                    addMessage(
                        AssessmentChatMessage(
                            sender = MessageSender.ZENO,
                            text = context.getString(R.string.assessment_q3),
                            quickReplies = styles
                        )
                    )
                }

                2 -> {
                    _learningProfile.value = _learningProfile.value.copy(
                        preferredExplanationStyle = userAnswer
                    )
                    currentStep = 3

                    val options = config?.assessmentOptions?.strengths ?: listOf("Memorization", "Understanding", "Application")
                    val question = context.getString(R.string.assessment_q4)

                    addMessage(
                        AssessmentChatMessage(
                            sender = MessageSender.ZENO,
                            text = question,
                            quickReplies = options
                        )
                    )
                }

                3 -> {
                    currentStep = 4
                    val strengths = listOf(context.getString(R.string.assessment_strength_1, selectedSubject))

                    _learningProfile.value = _learningProfile.value.copy(
                        strengths = strengths,
                        englishSkills = EnglishSkillsProfile(
                            grammar = context.getString(R.string.assessment_level_excellent),
                            vocabulary = context.getString(R.string.assessment_level_good),
                            reading = context.getString(R.string.assessment_level_good),
                            writing = context.getString(R.string.assessment_level_needs_support)
                        )
                    )

                    addMessage(
                        AssessmentChatMessage(
                            sender = MessageSender.ZENO,
                            text = context.getString(R.string.assessment_q4_reply)
                        )
                    )

                    delay(1000)
                    addMessage(
                        AssessmentChatMessage(
                            sender = MessageSender.ZENO,
                            text = context.getString(R.string.assessment_q5)
                        )
                    )

                    val userGrade = userManager.getGrade() ?: ""
                    val isSeniorOrBacc = userGrade.contains("auto_str_تالتة") ||
                            userGrade.contains("Third") ||
                            userGrade.contains("3") ||
                            userGrade.contains("auto_str_بكالوريا") ||
                            userGrade.contains("Bacc")

                    if (isSeniorOrBacc) {
                        delay(1200)
                        addMessage(
                            AssessmentChatMessage(
                                sender = MessageSender.ZENO,
                                text = context.getString(R.string.assessment_senior)
                            )
                        )
                    }

                    delay(1200)
                    addMessage(
                        AssessmentChatMessage(
                            sender = MessageSender.ZENO,
                            text = context.getString(R.string.assessment_q6),
                            quickReplies = listOf("⏱️ 1 Hour", "⏱️ 2.5 Hours", "⏱️ 5 Hours", "⏱️ +5 Hours")
                        )
                    )
                }

                4 -> {
                    currentStep = 5
                    val minutes = when {
                        userAnswer.contains("1") -> 60
                        userAnswer.contains("2.5") -> 150
                        userAnswer.contains("+5") -> 360
                        userAnswer.contains("5") -> 300
                        else -> 180
                    }
                    selectedStudyMinutes = minutes

                    addMessage(
                        AssessmentChatMessage(
                            sender = MessageSender.ZENO,
                            text = context.getString(R.string.assessment_q6_reply, userAnswer)
                        )
                    )

                    delay(1000)
                    addMessage(
                        AssessmentChatMessage(
                            sender = MessageSender.ZENO,
                            text = if (isArabic) context.getString(R.string.assessment_conclusion)
                            else "Your personalized learning profile is ready!"
                        )
                    )

                    delay(500)
                    addMessage(
                        AssessmentChatMessage(
                            sender = MessageSender.ZENO,
                            text = "",
                            isProfileCard = true
                        )
                    )
                }
            }

            _isTyping.value = false
        }
    }

    private fun addMessage(message: AssessmentChatMessage) {
        val updated = _messages.value.toMutableList()
        updated.add(message)
        _messages.value = updated
    }

    private val _isGeneratingPlan = MutableStateFlow(false)
    val isGeneratingPlan: StateFlow<Boolean> = _isGeneratingPlan.asStateFlow()

    fun completeAssessment(onComplete: () -> Unit) {
        val finalProfile = _learningProfile.value.copy(isAssessmentCompleted = true)
        userManager.saveLearningProfile(finalProfile)

        viewModelScope.launch {
            _isGeneratingPlan.value = true
            studyPlanRepository.generateStudyPlan(selectedStudyMinutes)
            _isGeneratingPlan.value = false
            onComplete()
        }
    }
}
