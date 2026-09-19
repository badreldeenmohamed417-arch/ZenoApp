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

@Composable
fun ZenoMarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val annotatedString = buildAnnotatedString {
        val lines = text.split("\n")
        lines.forEachIndexed { index, line ->
            var currentLine = line
            
            if (currentLine.startsWith("## ")) {
                withStyle(
                    style = SpanStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    parseInlineMarkdown(this, currentLine.removePrefix("## "))
                }
            } else if (currentLine.startsWith("### ")) {
                withStyle(
                    style = SpanStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    parseInlineMarkdown(this, currentLine.removePrefix("### "))
                }
            } else {
                parseInlineMarkdown(this, currentLine)
            }
            
            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }
    
    Text(
        text = annotatedString,
        modifier = modifier,
        color = color,
        style = MaterialTheme.typography.bodyLarge
    )
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
