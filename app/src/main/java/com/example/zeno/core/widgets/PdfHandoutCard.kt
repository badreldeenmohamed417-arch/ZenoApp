package com.example.zeno.core.widgets

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.core.network.RetrofitClient
import java.net.URI

private val CardBG = Color(0xFF181A1F)
private val LimeAccent = Color(0xFFD2F535)
private val RedPdf = Color(0xFFEF4444)
private val TextWhite = Color(0xFFF3F3F5)
private val TextMuted = Color(0xFF8E929B)

@Composable
fun PdfHandoutCard(messageText: String) {
    val context = LocalContext.current
    val openErrorToastMsg = stringResource(R.string.pdf_open_error_toast)

    // Extract PDF URL from message text
    val pdfUrlRegex = Regex("""https?://[^\s)]+\.pdf""")
    val rawMatch = pdfUrlRegex.find(messageText)?.value

    // Clean text by stripping markdown link syntax
    val cleanIntroText = messageText
        .replace(Regex("""\[(.*?)\]\((.*?)\)"""), "")
        .replace(pdfUrlRegex, "")
        .trim()

    val pdfUrl = rawMatch?.let { fixPdfUrl(it) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CardBG)
            .border(1.dp, LimeAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Top Header Row
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
                        .background(RedPdf.copy(alpha = 0.15f))
                        .border(1.dp, RedPdf.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = RedPdf,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(id = R.string.pdf_handout_title),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(id = R.string.pdf_handout_subtitle),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(LimeAccent.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "PDF",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LimeAccent
                )
            }
        }

        if (cleanIntroText.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = cleanIntroText,
                fontSize = 13.5.sp,
                color = TextWhite,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Download / Open Button
        Button(
            onClick = {
                if (!pdfUrl.isNullOrBlank()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        Toast.makeText(context, openErrorToastMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LimeAccent)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.pdf_handout_download_btn),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        }
    }
}

private fun fixPdfUrl(url: String): String {
    val baseUrl = RetrofitClient.MAIN_SERVER_BASE_URL
    val serverHost = try {
        val uri = URI(baseUrl)
        val scheme = if (uri.scheme != null) uri.scheme else "http"
        val host = if (uri.host != null) uri.host else "10.0.2.2"
        val port = if (uri.port != -1) uri.port else 8000
        "$scheme://$host:$port"
    } catch (e: Exception) {
        "http://10.0.2.2:8000"
    }

    return url.replace("http://127.0.0.1:8000", serverHost)
        .replace("http://localhost:8000", serverHost)
}
