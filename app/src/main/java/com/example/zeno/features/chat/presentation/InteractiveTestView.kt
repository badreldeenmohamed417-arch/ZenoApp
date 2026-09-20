package com.example.zeno.features.chat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.zeno.R
import com.google.gson.Gson

private val DarkBG = Color(0xFF0F1013)
private val CardBG = Color(0xFF181A1F)
private val CardBorder = Color(0xFF262930)
private val LimeAccent = Color(0xFFD2F535)
private val TextWhite = Color(0xFFF3F3F5)
private val TextMuted = Color(0xFF8E929B)
private val CorrectGreen = Color(0xFF22C55E)
private val WrongRed = Color(0xFFEF4444)

data class InteractiveTest(
    val intent: String = "",
    val test: TestData? = null
)

data class TestData(
    val title: String? = null,
    val questions: List<QuestionData> = emptyList()
)

data class QuestionData(
    val q: String = "",
    val options: List<String> = emptyList(),
    val answer: String = ""
)

@Composable
fun InteractiveTestView(jsonString: String) {
    var testData by remember { mutableStateOf<TestData?>(null) }
    var showTestDialog by remember { mutableStateOf(false) }

    LaunchedEffect(jsonString) {
        try {
            val parsed = Gson().fromJson(jsonString, InteractiveTest::class.java)
            if (parsed?.intent == "CREATE_TEST" && parsed.test != null) {
                testData = parsed.test
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    testData?.let { data ->
        val qCount = data.questions.size

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, LimeAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(LimeAccent.copy(alpha = 0.15f))
                            .border(1.dp, LimeAccent.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = LimeAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = data.title ?: stringResource(id = R.string.test_card_title),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(id = R.string.test_card_subtitle, qCount),
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showTestDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LimeAccent)
            ) {
                Text(
                    text = stringResource(id = R.string.test_start_button),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        if (showTestDialog) {
            InteractiveTestDialog(
                testData = data,
                onDismiss = { showTestDialog = false }
            )
        }
    }
}

@Composable
fun InteractiveTestDialog(
    testData: TestData,
    onDismiss: () -> Unit
) {
    var showExitConfirmDialog by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }
    val userAnswers = remember { mutableStateMapOf<Int, String>() }

    val totalQuestions = testData.questions.size
    val correctCount = userAnswers.count { (index, ans) ->
        testData.questions.getOrNull(index)?.answer == ans
    }
    val scorePercentage = if (totalQuestions > 0) ((correctCount.toFloat() / totalQuestions) * 100).toInt() else 0

    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            title = { Text(stringResource(id = R.string.test_exit_dialog_title), fontWeight = FontWeight.Bold, color = TextWhite) },
            text = { Text(stringResource(id = R.string.test_exit_dialog_message), color = TextMuted, fontSize = 14.sp) },
            confirmButton = {
                TextButton(onClick = {
                    showExitConfirmDialog = false
                    onDismiss()
                }) {
                    Text(stringResource(id = R.string.exitSession), color = WrongRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmDialog = false }) {
                    Text(stringResource(id = R.string.cancel_button), color = TextMuted)
                }
            },
            containerColor = CardBG,
            shape = RoundedCornerShape(18.dp)
        )
    }

    Dialog(
        onDismissRequest = {
            if (isSubmitted) onDismiss() else showExitConfirmDialog = true
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBG)
        ) {
            // Header Bar
            Surface(
                color = CardBG,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = testData.title ?: stringResource(id = R.string.test_card_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    IconButton(
                        onClick = {
                            if (isSubmitted) onDismiss() else showExitConfirmDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = TextWhite
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Score Summary if Submitted
                if (isSubmitted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(LimeAccent.copy(alpha = 0.15f))
                            .border(1.dp, LimeAccent, RoundedCornerShape(18.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = LimeAccent,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(id = R.string.test_score_summary, correctCount, totalQuestions, scorePercentage),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Questions List
                testData.questions.forEachIndexed { index, question ->
                    QuestionItemView(
                        index = index + 1,
                        question = question,
                        selectedOption = userAnswers[index],
                        isSubmitted = isSubmitted,
                        onOptionSelected = { option ->
                            if (!isSubmitted) {
                                userAnswers[index] = option
                            }
                        }
                    )
                    if (index < testData.questions.size - 1) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            // Bottom Finish Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (isSubmitted) {
                            onDismiss()
                        } else {
                            isSubmitted = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LimeAccent)
                ) {
                    Text(
                        text = if (isSubmitted) stringResource(id = R.string.ok_button) else stringResource(id = R.string.test_finish_button),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionItemView(
    index: Int,
    question: QuestionData,
    selectedOption: String?,
    isSubmitted: Boolean,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$index. ${question.q}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        question.options.forEach { option ->
            val isSelected = selectedOption == option
            val isCorrect = option == question.answer

            val optionBg = when {
                !isSubmitted && isSelected -> LimeAccent.copy(alpha = 0.2f)
                !isSubmitted -> CardBG
                isSubmitted && isCorrect -> CorrectGreen.copy(alpha = 0.2f)
                isSubmitted && isSelected && !isCorrect -> WrongRed.copy(alpha = 0.2f)
                else -> CardBG
            }

            val optionBorder = when {
                !isSubmitted && isSelected -> LimeAccent
                !isSubmitted -> CardBorder
                isSubmitted && isCorrect -> CorrectGreen
                isSubmitted && isSelected && !isCorrect -> WrongRed
                else -> CardBorder
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(optionBg)
                    .border(1.dp, optionBorder, RoundedCornerShape(14.dp))
                    .clickable { onOptionSelected(option) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = option,
                    fontSize = 14.sp,
                    color = TextWhite
                )
            }
        }
    }
}
