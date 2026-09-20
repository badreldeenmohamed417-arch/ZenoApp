package com.example.zeno.features.profile.presentation

import org.koin.compose.koinInject

import com.example.zeno.core.txtStr

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zeno.R
import com.example.zeno.core.config.data.repository.ConfigRepository
import com.example.zeno.core.data.EncryptedAuthStorageImpl
import com.example.zeno.core.network.AuthInterceptor
import com.example.zeno.core.network.RetrofitClient
import com.example.zeno.core.network.TokenAuthenticator
import com.example.zeno.core.ui.modifiers.bounceClickable
import com.example.zeno.core.widgets.SettingsTopHeaderBar
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.UserManager

import com.example.zeno.data.serverConnections.AuthApi
import kotlinx.coroutines.launch

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    configRepository: ConfigRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userManager = remember { UserManager(context) }
    val uiState by viewModel.uiState.collectAsState()

    val profileData = (uiState as? ProfileUiState.Success)?.data

    val displayName = profileData?.displayName ?: userManager.getDisplayName() ?: "Student"
    val username = profileData?.username ?: userManager.getDisplayName() ?: ""
    val email = profileData?.email ?: userManager.getEmail() ?: "student@zeno.app"
    val authProvider = profileData?.authProvider ?: "email"
    val isGoogleUser = authProvider.equals("google", ignoreCase = true)

    val avatarInitial = (displayName.ifBlank { username.ifBlank { email } }).trim().firstOrNull()?.uppercase() ?: "Z"

    val usernameTakenMsg = stringResource(R.string.profile_username_taken)
    val resetPasswordSentMsg = stringResource(R.string.profile_reset_password_sent)

    var inputUsername by remember(username) { mutableStateOf(username) }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditEmailDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
    ) {
        SettingsTopHeaderBar(
            title = stringResource(id = R.string.settings_profile),
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Spacer(modifier = Modifier.height(24.dp))

        // User Avatar Logo (Non-clickable, first letter of name)
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(CardBG)
                .border(2.dp, CardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatarInitial,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        val isArabic = userManager.getLanguage() != "en"
        val planTitle = userManager.getSubscriptionPlanTitle(isArabic)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(LimeAccent.copy(alpha = 0.15f))
                .border(1.dp, LimeAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = planTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LimeAccent
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Username Field (At the top of the page - Editable & Unique)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_username_label),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputUsername,
                    onValueChange = { inputUsername = it },
                    placeholder = { Text(stringResource(R.string.profile_username_hint), color = TextMuted, fontSize = 14.sp) },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LimeAccent,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = DarkBG,
                        unfocusedContainerColor = DarkBG,
                        cursorColor = LimeAccent
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LimeAccent)
                        .bounceClickable {
                            if (inputUsername.isNotBlank()) {
                                if (inputUsername.trim().length < 2 || inputUsername.trim().length > 15) {
                                    Toast.makeText(context, "اسم المستخدم يجب أن يكون بين 2 و 15 حرفاً", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.updateUsername(inputUsername) { success, message ->
                                        val msg = if (success) {
                                            message
                                        } else {
                                            if (message.contains("already taken", ignoreCase = true)) {
                                                usernameTakenMsg
                                            } else message
                                        }
                                        Toast.makeText(context, context.txtStr(msg), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. User Info Card (Name, Email, Password)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Name Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.account_name),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                if (!isGoogleUser) {
                    IconButton(onClick = { showEditNameDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = LimeAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = CardBorder, thickness = 1.dp)

            // Email Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.account_email),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = email,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                if (!isGoogleUser) {
                    IconButton(onClick = { showEditEmailDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = LimeAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Password Row (Only if NOT Google User!)
            if (!isGoogleUser) {
                HorizontalDivider(color = CardBorder, thickness = 1.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.account_password),
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "••••••••",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF23262E))
                            .bounceClickable {
                                // Reset Password via Email
                                scope.launch {
                                    try {
                                        val authRepository = org.koin.core.context.GlobalContext.get().get<com.example.zeno.features.auth.data.AuthRepository>()
                                        authRepository.forgotPassword(com.example.zeno.features.auth.data.ForgotPasswordRequest(email))
                                        Toast.makeText(context, context.txtStr(resetPasswordSentMsg), Toast.LENGTH_LONG).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, e.message ?: "Error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.profile_reset_password),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LimeAccent
                        )
                    }
                }
            }
        }
    }

    if (showEditNameDialog) {
        var newName by remember { mutableStateOf(displayName) }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text(stringResource(R.string.profile_edit_name_title), color = TextWhite) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    textStyle = TextStyle(color = TextWhite),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LimeAccent,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = DarkBG,
                        unfocusedContainerColor = DarkBG
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.updateDisplayName(newName) { success, msg ->
                                Toast.makeText(context, context.txtStr(msg), Toast.LENGTH_SHORT).show()
                            }
                            showEditNameDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.save_button), color = LimeAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text(stringResource(R.string.cancel_button), color = TextMuted)
                }
            },
            containerColor = CardBG
        )
    }

    if (showEditEmailDialog) {
        var newEmail by remember { mutableStateOf(email) }
        AlertDialog(
            onDismissRequest = { showEditEmailDialog = false },
            title = { Text(stringResource(R.string.profile_edit_email_title), color = TextWhite) },
            text = {
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    singleLine = true,
                    textStyle = TextStyle(color = TextWhite),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LimeAccent,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = DarkBG,
                        unfocusedContainerColor = DarkBG
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newEmail.isNotBlank()) {
                            viewModel.requestEmailChange(newEmail) { success, msg ->
                                Toast.makeText(context, context.txtStr(msg), Toast.LENGTH_LONG).show()
                            }
                            showEditEmailDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.save_button), color = LimeAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditEmailDialog = false }) {
                    Text(stringResource(R.string.cancel_button), color = TextMuted)
                }
            },
            containerColor = CardBG
        )
    }
}
}
