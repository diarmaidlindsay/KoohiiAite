package tech.diarmaid.koohiiaite.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HTTP client for communicating with kanji.koohii.com.
 * Uses OkHttp with the KoohiiSessionStore as a CookieJar for automatic cookie management.
 */
@Singleton
class KoohiiApiClient @Inject constructor(
    private val client: OkHttpClient,
    private val sessionStore: KoohiiSessionStore
) {
    /**
     * Downloads the user's stories CSV from Koohii.
     * Returns the CSV text content, or a failure if the request failed.
     *
     * The stored session cookies are automatically injected by the CookieJar.
     */
    suspend fun downloadStoriesCsv(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$BASE_URL/study/export")
                .header("User-Agent", USER_AGENT)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            when {
                // Check if we were redirected to login (session expired)
                response.request.url.encodedPath == "/login" -> {
                    sessionStore.clear()
                    Result.failure(KoohiiAuthException("Session expired. Please log in again."))
                }
                !response.isSuccessful -> {
                    Result.failure(KoohiiApiException("HTTP ${response.code}: ${response.message}"))
                }
                // Verify it looks like CSV content
                !body.startsWith("framenr,") -> {
                    Result.failure(KoohiiApiException("Unexpected response format. Are you logged in?"))
                }
                else -> {
                    Result.success(body)
                }
            }
        } catch (e: Exception) {
            Result.failure(KoohiiApiException("Network error: ${e.message}", e))
        }
    }

    companion object {
        private const val BASE_URL = "https://kanji.koohii.com"
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36"
    }
}

class KoohiiAuthException(message: String) : Exception(message)
class KoohiiApiException(message: String, cause: Throwable? = null) : Exception(message, cause)
