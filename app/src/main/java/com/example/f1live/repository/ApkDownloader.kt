package com.example.f1live.repository

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ApkDownloader(private val context: Context) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var downloadId: Long = -1L

    fun downloadApk(
        url: String,
        fileName: String,
        onProgress: (Int) -> Unit,
        onComplete: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("F1Live Update")
            .setDescription("Downloading new version")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)

        downloadId = downloadManager.enqueue(request)

        // Poll progress on IO thread
        CoroutineScope(Dispatchers.IO).launch {
            var downloading = true
            while (downloading) {
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                if (cursor.moveToFirst()) {
                    val bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))

                    if (bytesTotal > 0) {
                        withContext(Dispatchers.Main) {
                            onProgress((bytesDownloaded * 100L / bytesTotal).toInt())
                        }
                    }

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            val localUri = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                            val file = File(Uri.parse(localUri).path!!)
                            val apkUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                            withContext(Dispatchers.Main) { onComplete(apkUri) }
                            downloading = false
                        }
                        DownloadManager.STATUS_FAILED -> {
                            withContext(Dispatchers.Main) { onError("Download failed") }
                            downloading = false
                        }
                    }
                }
                cursor.close()
                delay(400)
            }
        }
    }

    fun installApk(apkUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

    fun canRequestInstall(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()
}

fun isNewerVersion(current: String, latest: String): Boolean {
    val c = current.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
    val l = latest.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
    for (i in 0 until maxOf(c.size, l.size)) {
        val cv = c.getOrElse(i) { 0 }
        val lv = l.getOrElse(i) { 0 }
        if (lv != cv) return lv > cv
    }
    return false
}

