package tech.diarmaid.koohiiaite.ui.importstory

import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import tech.diarmaid.koohiiaite.data.remote.KoohiiSessionStore

/**
 * A composable that shows a WebView for logging into kanji.koohii.com.
 *
 * When login is detected (navigated away from /login to the homepage),
 * it extracts cookies from the WebView, saves them via KoohiiSessionStore,
 * and calls onLoginSuccess.
 */
@Composable
fun KoohiiLoginWebView(
    sessionStore: KoohiiSessionStore,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }
    var wasOnLoginPage by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Text(
            text = "Log in to your Koohii account below. Stories will download automatically after login.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.userAgentString = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36"

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false

                            val isLoginPage = url?.contains("/login") == true

                            if (isLoginPage) {
                                wasOnLoginPage = true
                            } else if (wasOnLoginPage && !isLoginPage) {
                                // We were on the login page and now navigated away — login succeeded.
                                // Extract cookies from the WebView and persist them.
                                val cookieManager = CookieManager.getInstance()
                                val cookies = cookieManager.getCookie("https://kanji.koohii.com")
                                if (cookies != null) {
                                    sessionStore.saveCookiesFromWebView(cookies)
                                }
                                onLoginSuccess()
                            }
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            // Allow all navigation within koohii.com
                            return false
                        }
                    }

                    loadUrl("https://kanji.koohii.com/login")
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
    }
}
