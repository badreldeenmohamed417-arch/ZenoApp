package com.example.zeno.features.profile.presentation

import com.example.zeno.core.txtStr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QuestionAnswer
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavController
import com.example.zeno.BuildConfig
import com.example.zeno.MainActivity
import com.example.zeno.R
import com.example.zeno.core.data.EncryptedAuthStorageImpl
import com.example.zeno.core.theme.ErrorRed
import com.example.zeno.core.widgets.SettingsTopHeaderBar
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.UserManager
import com.example.zeno.data.local.db.AppDatabase
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent

@Composable
fun AccountDataScreen(navController: NavController, viewModel: ProfileViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userManager = remember { UserManager(context) }

    val uiState by viewModel.uiState.collectAsState()
    val profileData = (uiState as? ProfileUiState.Success)?.data
    val authProvider = profileData?.authProvider ?: "email"
    val isGoogleUser = authProvider.equals("google", ignoreCase = true)
    val currentEmail = profileData?.email ?: userManager.getEmail() ?: ""

    val email = currentEmail.ifEmpty { "student@zeno.app" }
    val dataCopiedToastMsg = stringResource(R.string.auto_str_تم_نسخ_البيانات)
    val dataClearedToastMsg = stringResource(R.string.auto_str_تم_مسح_جميع)
    val mismatchMsg = stringResource(R.string.account_delete_google_mismatch)
    val exportRequestSentMsg = stringResource(R.string.account_export_request_sent)

    var showExportDataDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteChatsDialog by remember { mutableStateOf(false) }
    var showDeleteDataDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
    ) {
        SettingsTopHeaderBar(
            title = stringResource(id = R.string.settings_account_data),
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {

        Spacer(modifier = Modifier.height(24.dp))

        // Group 1: Data Management
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            AccountRowItem(
                title = stringResource(id = R.string.account_export_data),
                icon = Icons.Default.Download,
                iconTint = Color(0xFF60A5FA),
                onClick = { showExportDataDialog = true }
            )
            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            AccountRowItem(
                title = stringResource(id = R.string.auto_str_حذف_محلي),
                icon = Icons.Default.CleaningServices,
                iconTint = Color(0xFFFB923C),
                onClick = { showDeleteDataDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group 2: Session Management (Logout)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            AccountRowItem(
                title = stringResource(id = R.string.account_logout),
                icon = Icons.AutoMirrored.Filled.Logout,
                iconTint = Color(0xFFF87171),
                titleColor = Color(0xFFF87171),
                onClick = { showLogoutDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Group 3: Danger Zone
        Text(
            text = stringResource(id = R.string.settings_danger_zone),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ErrorRed,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(ErrorRed.copy(alpha = 0.06f))
                .border(1.dp, ErrorRed.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            AccountRowItem(
                title = stringResource(id = R.string.account_delete_all_chats),
                icon = Icons.Default.QuestionAnswer,
                iconTint = ErrorRed,
                titleColor = ErrorRed,
                onClick = { showDeleteChatsDialog = true }
            )
            HorizontalDivider(color = ErrorRed.copy(alpha = 0.15f), thickness = 1.dp)
            AccountRowItem(
                title = stringResource(id = R.string.account_delete),
                icon = Icons.Default.Delete,
                iconTint = ErrorRed,
                titleColor = ErrorRed,
                onClick = { showDeleteAccountDialog = true }
            )
        }
    }

    // Export Data Dialog
    if (showExportDataDialog) {
        var isExporting by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isExporting) showExportDataDialog = false },
            title = { Text(stringResource(id = R.string.account_export_data), color = TextWhite) },
            text = {
                Text(
                    text = stringResource(id = R.string.account_export_dialog_text, email),
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isExporting = true
                        viewModel.requestDataExport { success, msg ->
                            isExporting = false
                            showExportDataDialog = false
                            val toastMsg = if (success) {
                                exportRequestSentMsg
                            } else msg
                            Toast.makeText(context, context.txtStr(toastMsg), Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LimeAccent),
                    enabled = !isExporting
                ) {
                    Text(
                        text = if (isExporting) stringResource(id = R.string.account_export_processing) else stringResource(id = R.string.account_export_button),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDataDialog = false }, enabled = !isExporting) {
                    Text(stringResource(id = R.string.cancel_button), color = TextMuted)
                }
            },
            containerColor = CardBG
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(id = R.string.account_logout), color = TextWhite) },
            text = { Text(stringResource(id = R.string.account_logout_confirm), color = TextMuted, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        val authStorage = EncryptedAuthStorageImpl(context)
                        authStorage.clearToken()
                        userManager.clearUserData()
                        showLogoutDialog = false
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text(stringResource(id = R.string.account_logout), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(id = R.string.cancel_button), color = TextMuted)
                }
            },
            containerColor = CardBG
        )
    }

    // Delete All Chats Dialog
    if (showDeleteChatsDialog) {
        var isDeleting by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteChatsDialog = false },
            title = { Text(stringResource(id = R.string.auto_str_حذف_جميع_المحادثات), color = TextWhite) },
            text = { Text(stringResource(id = R.string.auto_str_هل_أنت_تأكد), color = TextMuted, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        isDeleting = true
                        viewModel.deleteConversations { success, msg ->
                            if (success) {
                                scope.launch {
                                    val db = AppDatabase.getDatabase(context)
                                    db.chatDao().clearConversations()
                                    db.chatDao().clearMessages()
                                    showDeleteChatsDialog = false
                                    Toast.makeText(context, context.txtStr(msg), Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                isDeleting = false
                                Toast.makeText(context, context.txtStr(msg), Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    enabled = !isDeleting
                ) {
                    Text(if (isDeleting) stringResource(id = R.string.auto_str_جاري_الحذف) else stringResource(id = R.string.auto_str_حذف), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteChatsDialog = false }, enabled = !isDeleting) {
                    Text(stringResource(id = R.string.cancel_button), color = TextMuted)
                }
            },
            containerColor = CardBG
        )
    }

    // Delete All Data Dialog
    if (showDeleteDataDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDataDialog = false },
            title = { Text(stringResource(id = R.string.auto_str_حذف_جميع_البيانات), color = TextWhite) },
            text = { Text(stringResource(id = R.string.auto_str_هل_أنت_متأكد), color = TextMuted, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            userManager.clearUserData()
                            val db = AppDatabase.getDatabase(context)
                            db.chatDao().clearConversations()
                            db.chatDao().clearMessages()
                            showDeleteDataDialog = false
                            Toast.makeText(context, dataClearedToastMsg, Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            context.startActivity(intent)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text(stringResource(id = R.string.auto_str_حذف_محلي), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDataDialog = false }) {
                    Text(stringResource(id = R.string.cancel_button), color = TextMuted)
                }
            },
            containerColor = CardBG
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        if (isGoogleUser) {
            var isAuthenticating by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { if (!isAuthenticating) showDeleteAccountDialog = false },
                title = { Text(stringResource(R.string.account_delete_google_title), color = TextWhite) },
                text = {
                    Text(
                        stringResource(R.string.account_delete_google_msg),
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isAuthenticating = true
                            scope.launch {
                                try {
                                    val credentialManager = CredentialManager.create(context)
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                                        .setFilterByAuthorizedAccounts(false)
                                        .build()

                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()

                                    val result = credentialManager.getCredential(context = context, request = request)
                                    val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

                                    val selectedEmail = googleCredential.id.trim().lowercase()
                                    val activeEmail = currentEmail.trim().lowercase()

                                    if (activeEmail.isNotEmpty() && selectedEmail != activeEmail) {
                                        isAuthenticating = false
                                        Toast.makeText(context, context.txtStr(mismatchMsg), Toast.LENGTH_LONG).show()
                                    } else {
                                        viewModel.deleteAccountGoogle(googleCredential.idToken) { success, msg ->
                                            isAuthenticating = false
                                            if (success) {
                                                userManager.clearUserData()
                                                showDeleteAccountDialog = false
                                                Toast.makeText(context, context.txtStr(msg), Toast.LENGTH_LONG).show()
                                                val intent = Intent(context, MainActivity::class.java).apply {
                                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                }
                                                context.startActivity(intent)
                                            } else {
                                                Toast.makeText(context, context.txtStr(msg), Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    isAuthenticating = false
                                    Toast.makeText(context, "Google authentication failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        enabled = !isAuthenticating
                    ) {
                        Text(
                            if (isAuthenticating) stringResource(R.string.auto_str_جاري_الحذف)
                            else stringResource(R.string.account_delete_google_btn),
                            color = Color.White
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAccountDialog = false }, enabled = !isAuthenticating) {
                        Text(stringResource(R.string.cancel_button), color = TextMuted)
                    }
                },
                containerColor = CardBG
            )
        } else {
            var password by remember { mutableStateOf("") }
            var isDeleting by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { if (!isDeleting) showDeleteAccountDialog = false },
                title = { Text(stringResource(id = R.string.auto_str_حذف_الحساب_نهائيا), color = TextWhite) },
                text = {
                    Column {
                        Text(stringResource(id = R.string.auto_str_سيتم_حذف_حسابك), color = TextMuted, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(id = R.string.auth_password_hint)) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ErrorRed,
                                unfocusedBorderColor = CardBorder,
                                focusedContainerColor = DarkBG,
                                unfocusedContainerColor = DarkBG
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isDeleting = true
                            viewModel.deleteAccount(password) { success, msg ->
                                isDeleting = false
                                if (success) {
                                    scope.launch {
                                        userManager.clearUserData()
                                        showDeleteAccountDialog = false
                                        Toast.makeText(context, context.txtStr(msg), Toast.LENGTH_LONG).show()
                                        val intent = Intent(context, MainActivity::class.java).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                        context.startActivity(intent)
                                    }
                                } else {
                                    Toast.makeText(context, context.txtStr(msg), Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        enabled = !isDeleting && password.isNotEmpty()
                    ) {
                        Text(if (isDeleting) stringResource(id = R.string.auto_str_جاري_الحذف) else stringResource(id = R.string.account_delete), color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAccountDialog = false }, enabled = !isDeleting) {
                        Text(stringResource(id = R.string.cancel_button), color = TextMuted)
                    }
                },
                containerColor = CardBG
            )
        }
    }
}
}

@Composable
fun AccountRowItem(
    title: String,
    icon: ImageVector,
    iconTint: Color = Color(0xFF60A5FA),
    titleColor: Color = TextWhite,
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
                color = titleColor
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
