package org.jellyfin.androidtv.data.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jellyfin.androidtv.BuildConfig
import org.jellyfin.androidtv.R
import org.json.JSONArray
import timber.log.Timber
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class AppUpdateService {

	companion object {
		private const val RELEASES_API_URL = "https://api.github.com/repos/leonida92/leofin/releases"
	}

	suspend fun checkAndInstallUpdate(
		context: Context,
		onStatusChange: (String) -> Unit,
		onFinished: () -> Unit
	) = withContext(Dispatchers.IO) {
		try {
			onStatusChange(context.getString(R.string.checking_for_updates))

			val url = URL(RELEASES_API_URL)
			val conn = url.openConnection() as HttpURLConnection
			conn.setRequestProperty("User-Agent", "Leofin-AndroidTV")
			conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
			conn.connectTimeout = 10000
			conn.readTimeout = 10000

			val responseCode = conn.responseCode
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw IllegalStateException("GitHub API responded with code $responseCode")
			}

			val responseText = conn.inputStream.bufferedReader().use { it.readText() }
			val releases = JSONArray(responseText)

			var updateAvailable = false
			var targetVersion = ""
			var apkDownloadUrl = ""
			val currentVersion = BuildConfig.VERSION_NAME

			for (i in 0 until releases.length()) {
				val release = releases.getJSONObject(i)
				val tagName = release.optString("tag_name", "").removePrefix("v").trim()
				val isDraft = release.optBoolean("draft", false)
				if (isDraft || tagName.isEmpty()) continue

				val assets = release.optJSONArray("assets") ?: continue
				for (j in 0 until assets.length()) {
					val asset = assets.getJSONObject(j)
					val assetName = asset.optString("name", "")
					if (assetName.endsWith(".apk", ignoreCase = true)) {
						if (isNewerVersion(tagName, currentVersion)) {
							updateAvailable = true
							targetVersion = tagName
							apkDownloadUrl = asset.optString("browser_download_url", "")
							break
						}
					}
				}
				if (updateAvailable) break
			}

			if (!updateAvailable || apkDownloadUrl.isEmpty()) {
				val upToDateMsg = context.getString(R.string.up_to_date, currentVersion)
				withContext(Dispatchers.Main) {
					onStatusChange(upToDateMsg)
					Toast.makeText(context, upToDateMsg, Toast.LENGTH_SHORT).show()
				}
				return@withContext
			}

			// Download APK
			withContext(Dispatchers.Main) {
				onStatusChange(context.getString(R.string.update_available, targetVersion))
			}

			val destFile = File(context.cacheDir, "leofin-update-$targetVersion.apk")
			if (destFile.exists()) destFile.delete()

			val downloadConn = URL(apkDownloadUrl).openConnection() as HttpURLConnection
			downloadConn.instanceFollowRedirects = true
			downloadConn.setRequestProperty("User-Agent", "Leofin-AndroidTV")
			downloadConn.connectTimeout = 15000
			downloadConn.readTimeout = 30000

			val totalBytes = downloadConn.contentLengthLong
			downloadConn.inputStream.use { input ->
				destFile.outputStream().use { output ->
					val buffer = ByteArray(8192)
					var bytesRead: Int
					var totalRead = 0L
					var lastPercent = -1

					while (input.read(buffer).also { bytesRead = it } != -1) {
						output.write(buffer, 0, bytesRead)
						totalRead += bytesRead
						if (totalBytes > 0) {
							val percent = ((totalRead * 100) / totalBytes).toInt()
							if (percent != lastPercent) {
								lastPercent = percent
								withContext(Dispatchers.Main) {
									onStatusChange(context.getString(R.string.downloading_update, percent))
								}
							}
						}
					}
				}
			}

			withContext(Dispatchers.Main) {
				onStatusChange(context.getString(R.string.installing_update))
				installApk(context, destFile)
			}
		} catch (e: Exception) {
			Timber.e(e, "Failed to check or install update")
			withContext(Dispatchers.Main) {
				val errorMsg = context.getString(R.string.update_check_failed)
				onStatusChange(errorMsg)
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
			}
		} finally {
			withContext(Dispatchers.Main) {
				onFinished()
			}
		}
	}

	private fun installApk(context: Context, apkFile: File) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
			Toast.makeText(context, "Please allow Leofin to install unknown apps", Toast.LENGTH_LONG).show()
			val permissionIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
				data = Uri.parse("package:${context.packageName}")
				flags = Intent.FLAG_ACTIVITY_NEW_TASK
			}
			context.startActivity(permissionIntent)
			return
		}

		val apkUri = FileProvider.getUriForFile(
			context,
			"${context.packageName}.fileprovider",
			apkFile
		)
		val intent = Intent(Intent.ACTION_VIEW).apply {
			setDataAndType(apkUri, "application/vnd.android.package-archive")
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
		}
		context.startActivity(intent)
	}

	fun isNewerVersion(latest: String, current: String): Boolean {
		val cleanLatest = latest.removePrefix("v").trim()
		val cleanCurrent = current.removePrefix("v").trim()
		if (cleanLatest == cleanCurrent) return false

		val latestParts = cleanLatest.split('.', '-').mapNotNull { it.toIntOrNull() }
		val currentParts = cleanCurrent.split('.', '-').mapNotNull { it.toIntOrNull() }

		val maxLen = maxOf(latestParts.size, currentParts.size)
		for (i in 0 until maxLen) {
			val l = latestParts.getOrElse(i) { 0 }
			val c = currentParts.getOrElse(i) { 0 }
			if (l > c) return true
			if (l < c) return false
		}
		return false
	}
}
