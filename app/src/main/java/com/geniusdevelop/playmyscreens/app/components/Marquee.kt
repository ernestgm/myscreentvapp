package com.geniusdevelop.playmyscreens.app.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun Marquee(
    text: String,
    textColor: String,
    bgColor: String,
) {
    val context = LocalContext.current

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
    <style>
        html,body {
            margin:0;
            padding: 0;
        }
        
        body {
            height: 130px;
        }
        
        #marquee {
            display: block;
            height: 100%;
            width: 100%;
            color: ${textColor};
            background-color: ${bgColor};
            font-size: 50px;
            font-height: bold;
            text-transform: uppercase;
        }
    </style>
</head>
            <body>
                <marquee id="marquee" scrollamount="10" scrolldelay="50">${text}</marquee>
            </body>
        </html>
    """

    val browser = WebView(context).apply {
        webViewClient = WebViewClient() // Prevents opening URLs in a browser
    }

    if (text.isNotEmpty()) {
        AndroidView(
            factory = {
                browser
            },
            update = { webview ->
                webview.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}