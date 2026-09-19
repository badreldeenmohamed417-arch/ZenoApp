package com.example.zeno.features.auth.presentation

import com.example.zeno.core.txt

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.data.local.UserManager

private val DarkBG = Color(0xFF0F1013)
private val CardBG = Color(0xFF181A1F)
private val CardBorder = Color(0xFF262930)
private val LimeAccent = Color(0xFFD2F535)
private val TextWhite = Color(0xFFF3F3F5)
private val TextMuted = Color(0xFF8E929B)

@Composable
fun LanguageSelectionScreen(
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    var selectedLanguage by remember { mutableStateOf(userManager.getLanguage()) }

    var activeDialogTitle by remember { mutableStateOf<String?>(null) }
    var activeDialogContent by remember { mutableStateOf<String?>(null) }

    val termsTitle = stringResource(R.string.info_terms)
    val termsContent = stringResource(R.string.terms_content)
    val privacyTitle = stringResource(R.string.info_privacy)
    val privacyContent = stringResource(R.string.privacy_policy_content)

    fun selectLanguage(lang: String) {
        if (selectedLanguage != lang) {
            selectedLanguage = lang
            userManager.saveLanguage(lang)
            (context as? Activity)?.recreate()
        }
    }

    if (activeDialogTitle != null && activeDialogContent != null) {
        AlertDialog(
            onDismissRequest = {
                activeDialogTitle = null
                activeDialogContent = null
            },
            title = { Text(activeDialogTitle!!, fontWeight = FontWeight.Bold, color = TextWhite) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(activeDialogContent!!, color = TextMuted, fontSize = 13.5.sp, lineHeight = 20.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    activeDialogTitle = null
                    activeDialogContent = null
                }) {
                    Text(stringResource(R.string.ok_button), color = LimeAccent, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardBG,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_zeno_logo),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            colorFilter = ColorFilter.tint(LimeAccent)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.lang_select_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.lang_select_subtitle),
            fontSize = 13.5.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Language Option: Arabic
        LanguageOptionCard(
            title = txt("auto_str_العربية"),
            flag = "🇪🇬",
            isSelected = selectedLanguage == "ar",
            onClick = { selectLanguage("ar") }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Language Option: English
        LanguageOptionCard(
            title = "English",
            flag = "🇬🇧",
            isSelected = selectedLanguage == "en",
            onClick = { selectLanguage("en") }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Terms and Consent Notice with Clickable Terms & Privacy Links
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.lang_consent_notice),
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.lang_terms_link),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LimeAccent,
                    modifier = Modifier
                        .clickable {
                            activeDialogTitle = termsTitle
                            activeDialogContent = termsContent
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Text(
                    text = " • ",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Text(
                    text = stringResource(id = R.string.lang_privacy_link),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LimeAccent,
                    modifier = Modifier
                        .clickable {
                            activeDialogTitle = privacyTitle
                            activeDialogContent = privacyContent
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Continue Button
        Button(
            onClick = {
                userManager.saveLanguage(selectedLanguage)
                userManager.saveInitialLanguageSelected(true)
                onContinue()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LimeAccent)
        ) {
            Text(
                text = stringResource(id = R.string.lang_continue_button),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LanguageOptionCard(
    title: String,
    flag: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) LimeAccent.copy(alpha = 0.15f) else CardBG)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) LimeAccent else CardBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = flag, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) LimeAccent else TextWhite
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = LimeAccent,
                    unselectedColor = TextMuted
                )
            )
        }
    }
}
