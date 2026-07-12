package com.example.f1live.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f1live.BuildConfig
import com.example.f1live.api.GitHubApi
import com.example.f1live.api.GitHubRelease
import com.example.f1live.repository.ApkDownloader
import com.example.f1live.repository.isNewerVersion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UpdateState {
    object Idle : UpdateState()
    data class Available(val release: GitHubRelease) : UpdateState()
    data class Downloading(val progress: Int) : UpdateState()
    data class ReadyToInstall(val apkUri: Uri) : UpdateState()
    data class NeedsInstallPermission(val apkUri: Uri) : UpdateState() // NEW
    data class Error(val message: String) : UpdateState()
}

class UpdateViewModel : ViewModel() {
    private val api = GitHubApi.create()
    private val _state = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val state: StateFlow<UpdateState> = _state

    fun checkForUpdate(currentVersion: String) {
        viewModelScope.launch {
            try {
                val release = api.getLatestRelease()
                Log.d("UpdateCheck", "Latest: ${release.tag_name}, Current: $currentVersion")
                if (isNewerVersion(currentVersion, release.tag_name)) {
                    Log.d("UpdateCheck", "Update available!")
                    Log.d("UpdateCheck", "BuildConfig version: ${BuildConfig.VERSION_NAME}")
                    _state.value = UpdateState.Available(release)
                } else {
                    Log.d("UpdateCheck", "No update needed")
                }
            } catch (e: Exception) {
                Log.e("UpdateCheck", "Failed to check update", e)
            }
        }
    }

    fun startDownload(context: Context, release: GitHubRelease) {
        val apkAsset = release.assets.firstOrNull { it.name.endsWith(".apk") } ?: return
        _state.value = UpdateState.Downloading(0)
        ApkDownloader(context).downloadApk(
            url = apkAsset.browser_download_url,
            fileName = apkAsset.name,
            onProgress = { _state.value = UpdateState.Downloading(it) },
            onComplete = { uri -> onDownloadComplete(context, uri) },
            onError = { msg -> _state.value = UpdateState.Error(msg) }
        )
    }

    // Called right after download finishes, and again when user returns from Settings
    fun onDownloadComplete(context: Context, apkUri: Uri) {
        val canInstall = ApkDownloader(context).canRequestInstall()
        _state.value = if (canInstall) {
            UpdateState.ReadyToInstall(apkUri)
        } else {
            UpdateState.NeedsInstallPermission(apkUri)
        }
    }

    // Call this when the user comes back from the Settings screen
    fun recheckPermission(context: Context) {
        val current = _state.value
        if (current is UpdateState.NeedsInstallPermission) {
            onDownloadComplete(context, current.apkUri)
        }
    }

    fun dismiss() { _state.value = UpdateState.Idle }
}