package com.example.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ReleaseInfo(
    val versionName: String,
    val releaseDate: String,
    val changelog: String,
    val apkDownloadUrl: String,
    val sourceZipUrl: String,
    val minAndroidVersion: String = "Android 7.0 (API 24)+",
    val apkSizeMb: String = "18.4 MB",
    val isUpdateAvailable: Boolean = false
)

object GitHubUpdateChecker {

    const val CURRENT_VERSION = "v1.0.0"
    const val GITHUB_REPO_URL = "https://github.com/aistudio-creator/ai-studio"
    const val LATEST_RELEASE_PAGE_URL = "$GITHUB_REPO_URL/releases/latest"

    suspend fun checkForUpdates(): ReleaseInfo = withContext(Dispatchers.IO) {
        // Fallback default info or real GitHub API query
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("https://api.github.com/repos/aistudio-creator/ai-studio/releases/latest")
                .header("User-Agent", "AI-Studio-Android")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string().orEmpty())
                val tagName = json.optString("tag_name", "v1.0.0")
                val body = json.optString("body", "Latest stable release of AI Studio.")
                val htmlUrl = json.optString("html_url", LATEST_RELEASE_PAGE_URL)

                val hasUpdate = tagName != CURRENT_VERSION
                return@withContext ReleaseInfo(
                    versionName = tagName,
                    releaseDate = "October 2026",
                    changelog = body.ifBlank { "Performance improvements, new AI Director styles, and faster multi-layer timeline scrubbing." },
                    apkDownloadUrl = htmlUrl,
                    sourceZipUrl = "$GITHUB_REPO_URL/archive/refs/tags/$tagName.zip",
                    isUpdateAvailable = hasUpdate
                )
            }
        } catch (_: Exception) {
            // Network or rate-limit fallback
        }

        ReleaseInfo(
            versionName = CURRENT_VERSION,
            releaseDate = "October 2026",
            changelog = "Initial release: 100+ effects, AI Video, Voice, Music, Director, and multi-layer timeline.",
            apkDownloadUrl = LATEST_RELEASE_PAGE_URL,
            sourceZipUrl = "$GITHUB_REPO_URL/archive/refs/tags/v1.0.0.zip",
            isUpdateAvailable = false
        )
    }

    fun openBrowserUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // fallback
        }
    }
}
