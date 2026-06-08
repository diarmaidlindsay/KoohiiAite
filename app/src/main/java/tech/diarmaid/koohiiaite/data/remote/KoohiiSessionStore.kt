package tech.diarmaid.koohiiaite.data.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores Koohii session cookies securely using EncryptedSharedPreferences.
 * Also implements OkHttp's CookieJar to seamlessly inject cookies into requests.
 */
@Singleton
class KoohiiSessionStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) : CookieJar {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Called by OkHttp when a response sets cookies.
     * We only persist cookies for the Koohii domain.
     */
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (!isKoohiiUrl(url)) return

        for (cookie in cookies) {
            when (cookie.name) {
                COOKIE_REVTK -> prefs.edit().putString(COOKIE_REVTK, cookie.value).apply()
                COOKIE_KOOHII -> prefs.edit().putString(COOKIE_KOOHII, cookie.value).apply()
            }
        }
    }

    /**
     * Called by OkHttp before sending a request.
     * Returns stored cookies for Koohii URLs.
     */
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        if (!isKoohiiUrl(url)) return emptyList()

        val cookies = mutableListOf<Cookie>()

        prefs.getString(COOKIE_REVTK, null)?.let { value ->
            cookies.add(
                Cookie.Builder()
                    .domain(url.host)
                    .path("/")
                    .name(COOKIE_REVTK)
                    .value(value)
                    .build()
            )
        }

        prefs.getString(COOKIE_KOOHII, null)?.let { value ->
            cookies.add(
                Cookie.Builder()
                    .domain(url.host)
                    .path("/")
                    .name(COOKIE_KOOHII)
                    .value(value)
                    .build()
            )
        }

        return cookies
    }

    /**
     * Extracts cookies from a raw cookie header string (from WebView's CookieManager)
     * and persists them.
     */
    fun saveCookiesFromWebView(cookieHeader: String) {
        val cookies = cookieHeader.split(";").mapNotNull { part ->
            val trimmed = part.trim()
            val eqIndex = trimmed.indexOf('=')
            if (eqIndex > 0) {
                trimmed.substring(0, eqIndex) to trimmed.substring(eqIndex + 1)
            } else null
        }

        prefs.edit().apply {
            for ((name, value) in cookies) {
                when (name) {
                    COOKIE_REVTK -> putString(COOKIE_REVTK, value)
                    COOKIE_KOOHII -> putString(COOKIE_KOOHII, value)
                }
            }
            apply()
        }
    }

    /**
     * Returns whether we have a stored session (i.e. the user has logged in before).
     */
    fun isLoggedIn(): Boolean {
        return prefs.getString(COOKIE_REVTK, null) != null
    }

    /**
     * Clears all stored cookies (logout).
     */
    fun clear() {
        prefs.edit().clear().apply()
    }

    private fun isKoohiiUrl(url: HttpUrl): Boolean {
        return url.host == "kanji.koohii.com"
    }

    companion object {
        private const val PREFS_FILE_NAME = "koohii_session"
        private const val COOKIE_REVTK = "RevTK"
        private const val COOKIE_KOOHII = "koohii"
    }
}
