package com.example.zeno.core.widgets

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.data.AppColors

private const val NO_REACTION = 0
private const val LIKE = 1
private const val DISLIKE = -1

fun withZenoAttribution(context: Context, text: String): String {
    val attribution = context.getString(R.string.zeno_attribution)
    return "$text\n\n$attribution"
}

fun shareFromZeno(context: Context, text: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, withZenoAttribution(context, text))
    }
    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.chat_share)))
}

@Composable
fun ZenoMessageActions(
    text: String,
    modifier: Modifier = Modifier,
    onMore: (() -> Unit)? = null,
    onLike: (() -> Unit)? = null,
    onDislike: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var reaction by remember(text) { mutableIntStateOf(NO_REACTION) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EmojiActionButton(icon = Icons.Outlined.ContentCopy, contentDescription = "Copy", reaction = false) {
            clipboardManager.setText(AnnotatedString(withZenoAttribution(context, text)))
            Toast.makeText(context, context.getString(R.string.chat_copied_toast), Toast.LENGTH_SHORT).show()
        }
        EmojiActionButton(icon = Icons.Outlined.Share, contentDescription = "Share", reaction = false) {
            shareFromZeno(context, text)
        }
        EmojiActionButton(icon = Icons.Outlined.ThumbUp, contentDescription = "Like", reaction = reaction == LIKE) {
            val next = if (reaction == LIKE) NO_REACTION else LIKE
            reaction = next
            onLike?.invoke()
        }
        EmojiActionButton(icon = Icons.Outlined.ThumbDown, contentDescription = "Dislike", reaction = reaction == DISLIKE) {
            val next = if (reaction == DISLIKE) NO_REACTION else DISLIKE
            reaction = next
            onDislike?.invoke()
        }
        if (onMore != null) {
            EmojiActionButton(icon = Icons.Outlined.MoreHoriz, contentDescription = "More", reaction = false, onClick = onMore)
        }
    }
}

@Composable
private fun EmojiActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    reaction: Boolean,
    onClick: () -> Unit
) {
    val background = if (reaction) AppColors.Accent.copy(alpha = 0.18f) else Color.Transparent
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = AppColors.TextPrimary,
        modifier = Modifier
            .size(34.dp)
            .background(background, CircleShape)
            .clickable(onClick = onClick)
            .padding(7.dp)
    )
}

fun formatChatText(text: String, kind: String): String {
    if (kind == "bullet") {
        return if (text.isBlank()) "- " else text.lines().joinToString("\n") { line ->
            if (line.trimStart().startsWith("- ")) line else "- " + line.trimStart()
        }
    }

    val marker = when (kind) {
        "bold" -> "**"
        "italic" -> "*"
        "code" -> "`"
        else -> return text
    }

    val trimmed = text.trim()
    if (trimmed.isBlank()) return "$marker$marker"

    return if (trimmed.startsWith(marker) && trimmed.endsWith(marker) && trimmed.length >= marker.length * 2) {
        trimmed.removePrefix(marker).removeSuffix(marker)
    } else {
        "$marker$trimmed$marker"
    }
}

@Composable
fun ZenoFormattingToolbar(
    text: String,
    onTextChange: (String) -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolbarButton("𝐁") { onTextChange(formatChatText(text, "bold")) }
        ToolbarButton("𝘐") { onTextChange(formatChatText(text, "italic")) }
        ToolbarButton("<>") { onTextChange(formatChatText(text, "code")) }
        ToolbarButton("•") { onTextChange(formatChatText(text, "bullet")) }
        ToolbarButton("…", onMore)
    }
}

@Composable
private fun ToolbarButton(
    label: String,
    onClick: () -> Unit
) {
    Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.TextPrimary,
        modifier = Modifier
            .background(AppColors.SurfaceVariant.copy(alpha = 0.75f), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}
