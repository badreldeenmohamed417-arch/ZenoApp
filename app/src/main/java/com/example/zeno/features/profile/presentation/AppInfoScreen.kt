package com.example.zeno.features.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zeno.R
import com.example.zeno.core.config.data.repository.ConfigRepository
import com.example.zeno.core.widgets.SettingsTopHeaderBar
import com.example.zeno.data.AppColors

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent

@Composable
fun AppInfoScreen(
    navController: NavController,
    configRepository: ConfigRepository
) {
    val context = LocalContext.current
    val latestVersionToastMsg = stringResource(R.string.info_latest_version_toast)
    val termsTitle = stringResource(R.string.info_terms)
    val termsContent = stringResource(R.string.terms_content)
    val privacyTitle = stringResource(R.string.info_privacy)
    val privacyContent = stringResource(R.string.privacy_policy_content)
    val licensesTitle = stringResource(R.string.info_licenses)
    val licensesContent = stringResource(R.string.info_licenses_content)
    val creditsTitle = stringResource(R.string.info_developer_credits)
    val creditsContent = stringResource(R.string.info_developer_credits_content)
    val legalTitle = stringResource(R.string.info_legal_notices)
    val legalContent = stringResource(R.string.info_legal_notices_content)

    var activeDialogTitle by remember { mutableStateOf<String?>(null) }
    var activeDialogContent by remember { mutableStateOf<String?>(null) }

    fun showInfoDialog(title: String, content: String) {
        activeDialogTitle = title
        activeDialogContent = content
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
    ) {
        SettingsTopHeaderBar(
            title = stringResource(id = R.string.settings_about_app),
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {

        Spacer(modifier = Modifier.height(24.dp))

        // Group 1: Version & Terms
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            InfoRowItem(
                title = stringResource(id = R.string.info_app_version),
                icon = Icons.Default.Info,
                badge = "v1.0.0",
                onClick = { Toast.makeText(context, latestVersionToastMsg, Toast.LENGTH_SHORT).show() }
            )
            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            InfoRowItem(
                title = stringResource(id = R.string.info_terms),
                icon = Icons.AutoMirrored.Filled.Article,
                iconTint = Color(0xFFFB923C),
                onClick = { showInfoDialog(termsTitle, termsContent) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group 2: Privacy, Updates & Licenses
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            InfoRowItem(
                title = stringResource(id = R.string.info_privacy),
                icon = Icons.Default.Security,
                iconTint = Color(0xFF60A5FA),
                onClick = { showInfoDialog(privacyTitle, privacyContent) }
            )
            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            InfoRowItem(
                title = stringResource(id = R.string.info_check_updates),
                icon = Icons.Default.Autorenew,
                iconTint = Color(0xFF60A5FA),
                onClick = { Toast.makeText(context, latestVersionToastMsg, Toast.LENGTH_SHORT).show() }
            )
            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            InfoRowItem(
                title = stringResource(id = R.string.info_licenses),
                icon = Icons.Default.Gavel,
                iconTint = Color(0xFF60A5FA),
                onClick = { showInfoDialog(licensesTitle, licensesContent) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group 3: Credits
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            InfoRowItem(
                title = stringResource(id = R.string.info_developer_credits),
                icon = Icons.Default.Person,
                iconTint = Color(0xFF5CE29A),
                onClick = { showInfoDialog(creditsTitle, creditsContent) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section Title: Information
        Text(
            text = stringResource(id = R.string.info_section_information),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF87171),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Group 4: Legal Notices
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            InfoRowItem(
                title = stringResource(id = R.string.info_legal_notices),
                icon = Icons.Default.Book,
                iconTint = Color(0xFFF87171),
                onClick = { showInfoDialog(legalTitle, legalContent) }
            )
        }
    }

    // Dialog Popup for detail screens
    if (activeDialogTitle != null && activeDialogContent != null) {
        AlertDialog(
            onDismissRequest = {
                activeDialogTitle = null
                activeDialogContent = null
            },
            title = {
                Text(
                    text = activeDialogTitle ?: "",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = activeDialogContent ?: "",
                        color = TextMuted,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        activeDialogTitle = null
                        activeDialogContent = null
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.close_button),
                        color = LimeAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = CardBG,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
}

@Composable
fun InfoRowItem(
    title: String,
    icon: ImageVector,
    badge: String? = null,
    iconTint: Color = Color(0xFF60A5FA),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF23262E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Text(
                    text = badge,
                    fontSize = 12.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
