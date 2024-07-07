package com.geniusdevelop.myscreens.app.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.myscreens.app.api.response.Images
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "session_preferences")

class SessionManager(val context: Context) {

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val NAME = stringPreferencesKey("username")
        val USER_ID = stringPreferencesKey("user_id")
        val DEVICE_CODE = stringPreferencesKey("device_code")
        val TOKEN = stringPreferencesKey("token")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val deviceCode: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[DEVICE_CODE]
        }

    val name: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[NAME]
        }

    val userId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }

    val token: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN]
        }

    suspend fun saveDeviceCode(code: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_CODE] = code
        }
    }

    suspend fun saveSession(isLoggedIn: Boolean, username: String, userId: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[USER_ID] = userId
            preferences[NAME] = username
            preferences[TOKEN] = token
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
