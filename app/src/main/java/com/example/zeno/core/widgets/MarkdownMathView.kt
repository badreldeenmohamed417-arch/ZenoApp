package com.example.zeno.core.widgets

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Base64
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MarkdownMathView(
    text: String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val htmlTemplate = remember(isDarkTheme) {
        val textColor = if (isDarkTheme) "#E2E8F0" else "#000000"
        val bgColor = "transparent" // We let the parent Compose Box handle the background

        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css">
            <script src="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/contrib/auto-render.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
            <style>
                body {
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                    font-size: 15px;
                    line-height: 1.5;
                    color: $textColor;
                    background-color: $bgColor;
                    margin: 0;
                    padding: 0;
                    word-wrap: break-word;
                    direction: rtl; /* For Arabic text */
                }
                pre {
                    background-color: ${if (isDarkTheme) "#1E293B" else "#F1F5F9"};
                    border-radius: 8px;
                    padding: 10px;
                    overflow-x: auto;
                    direction: ltr; /* Code blocks left-to-right */
                }
                code {
                    font-family: "Courier New", Courier, monospace;
                    background-color: ${if (isDarkTheme) "#1E293B" else "#F1F5F9"};
                    padding: 2px 4px;
                    border-radius: 4px;
                }
                pre code {
                    background-color: transparent;
                    padding: 0;
                }
                table { border-collapse: collapse; width: 100%; margin-bottom: 16px; }
                th, td { border: 1px solid ${if (isDarkTheme) "#334155" else "#CBD5E1"}; padding: 8px; text-align: right; }
                th { background-color: ${if (isDarkTheme) "#1E293B" else "#F1F5F9"}; }
                img { max-width: 100%; height: auto; border-radius: 8px; }
                /* Fix KaTeX direction for math */
                .katex { direction: ltr; }
            </style>
        </head>
        <body>
            <div id="content"></div>
            <script>
                function renderContent(rawText) {
                    // Convert markdown to HTML
                    document.getElementById('content').innerHTML = marked.parse(rawText);
                    
                    // Render KaTeX math
                    renderMathInElement(document.body, {
                      delimiters: [
                          {left: '$$', right: '$$', display: true},
                          {left: '$', right: '$', display: false},
                          {left: '\\(', right: '\\)', display: false},
                          {left: '\\[', right: '\\]', display: true}
                      ],
                      throwOnError : false
                    });
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    val encodedHtml = remember(htmlTemplate) {
        Base64.encodeToString(htmlTemplate.toByteArray(), Base64.NO_PADDING)
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setBackgroundColor(Color.TRANSPARENT)
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                }
                loadData(encodedHtml, "text/html", "base64")
            }
        },
        update = { webView ->
            // Escape the text to safely inject it into JS
            val escapedText = text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
            
            webView.evaluateJavascript("javascript:renderContent(\"$escapedText\");", null)
        }
    )
}
