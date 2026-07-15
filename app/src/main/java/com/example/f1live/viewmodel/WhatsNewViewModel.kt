package com.example.f1live.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.f1live.api.GitHubApi
import com.example.f1live.api.GitHubRelease
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WhatsNewPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("whats_new_prefs", Context.MODE_PRIVATE)

    fun getLastSeenVersion(): String? = prefs.getString("last_seen_version", null)

    fun setLastSeenVersion(version: String) {
        prefs.edit().putString("last_seen_version", version).apply()
    }
}

sealed class WhatsNewState {
    object Idle : WhatsNewState()
    data class Show(val release: GitHubRelease) : WhatsNewState()
}

class WhatsNewViewModel(application: Application) : AndroidViewModel(application) {
    private val api = GitHubApi.create()
    private val prefs = WhatsNewPrefs(application)

    private val _state = MutableStateFlow<WhatsNewState>(WhatsNewState.Idle)
    val state: StateFlow<WhatsNewState> = _state

    fun checkWhatsNew(currentVersion: String) {
        val lastSeen = prefs.getLastSeenVersion()

        // Fresh install — just record it, don't show a popup on first launch
        if (lastSeen == null) {
            prefs.setLastSeenVersion(currentVersion)
            return
        }

        // Already seen this version's notes
        if (lastSeen == currentVersion) return

        viewModelScope.launch {
            try {
                val release = api.getReleaseByTag("v$currentVersion") // matches your "v1.0.0" tag
                _state.value = WhatsNewState.Show(release)
                prefs.setLastSeenVersion(currentVersion)
            } catch (e: Exception) {
                Log.e("WhatsNew", "Failed to fetch release notes", e)
                prefs.setLastSeenVersion(currentVersion) // don't retry-loop on failure
            }
        }
    }

    fun dismiss() { _state.value = WhatsNewState.Idle }
}