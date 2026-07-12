package com.example.f1live.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


private val Context.widgetDataStore: DataStore<Preferences> by preferencesDataStore(name = "f1_widget_prefs")

object F1WidgetDataManager {
    private val RACE_DATA_KEY: androidx.datastore.preferences.core.Preferences.Key<String> = stringPreferencesKey("current_race_data")
    private val TRACK_IMAGE_KEY: androidx.datastore.preferences.core.Preferences.Key<String> = stringPreferencesKey("track_image_url")
    private val gson = Gson()

    suspend fun saveRaceData(context: Context, raceInfo: F1WidgetRaceInfo?) {
        context.widgetDataStore.edit { preferences ->
            if (raceInfo != null) {
                preferences[RACE_DATA_KEY] = gson.toJson(raceInfo)
            } else {
                preferences.remove(RACE_DATA_KEY)
            }
        }
    }

    suspend fun saveTrackImage(context: Context, imageUrl: String?) {
        context.widgetDataStore.edit { preferences ->
            if (imageUrl != null) {
                preferences[TRACK_IMAGE_KEY] = imageUrl
            } else {
                preferences.remove(TRACK_IMAGE_KEY)
            }
        }
    }

    suspend fun getRaceData(context: Context): F1WidgetRaceInfo? {
        return context.widgetDataStore.data.map { preferences ->
            preferences[RACE_DATA_KEY]?.let { json ->
                try {
                    gson.fromJson(json, F1WidgetRaceInfo::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        }.first()
    }

    suspend fun getTrackImage(context: Context): String? {
        return context.widgetDataStore.data.map { preferences ->
            preferences[TRACK_IMAGE_KEY]
        }.first()
    }
}

// ============================================================================
// 2. Data Class for Widget (Add to F1WidgetDataManager.kt)
// ============================================================================
data class F1WidgetRaceInfo(
    val raceName: String,
    val circuitName: String,
    val location: String,
    val formattedDate: String,
    val formattedTime: String?,
    val isOngoing: Boolean,
    val ongoingStatus: String,
    val daysUntil: String,
    val round: String
)