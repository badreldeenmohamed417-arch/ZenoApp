package com.example.zeno.core.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height

@Composable
fun ZenoMarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        val blocks = text.split(Regex("(?m)^---$|^\\*\\*\\*$"))
        blocks.forEachIndexed { index, block ->
            val annotatedString = buildAnnotatedString {
                val lines = block.trim('\n').split("\n")
                lines.forEachIndexed { lineIdx, line ->
                    if (line.startsWith("## ")) {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            parseInlineMarkdown(this, line.removePrefix("## "))
                        }
                    } else if (line.startsWith("### ")) {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            parseInlineMarkdown(this, line.removePrefix("### "))
                        }
                    } else {
                        parseInlineMarkdown(this, line)
                    }
                    
                    if (lineIdx < lines.size - 1) {
                        append("\n")
                    }
                }
            }
            
            if (annotatedString.isNotEmpty()) {
                Text(
                    text = annotatedString,
                    color = color,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            if (index < blocks.size - 1) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.HorizontalDivider(
                    color = color.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun parseInlineMarkdown(builder: androidx.compose.ui.text.AnnotatedString.Builder, text: String) {
    val pattern = Regex("(\\*\\*.*?\\*\\*|\\*.*?\\*)")
    val matches = pattern.findAll(text)
    
    var lastIndex = 0
    for (match in matches) {
        val start = match.range.first
        val end = match.range.last + 1
        
        if (start > lastIndex) {
            builder.append(text.substring(lastIndex, start))
        }
        
        val matchedText = match.value
        when {
            matchedText.startsWith("**") && matchedText.endsWith("**") -> {
                builder.withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(matchedText.removeSurrounding("**"))
                }
            }
            matchedText.startsWith("*") && matchedText.endsWith("*") -> {
                builder.withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(matchedText.removeSurrounding("*"))
                }
            }
            else -> {
                builder.append(matchedText)
            }
        }
        
        lastIndex = end
    }
    
    if (lastIndex < text.length) {
        builder.append(text.substring(lastIndex))
    }
}
