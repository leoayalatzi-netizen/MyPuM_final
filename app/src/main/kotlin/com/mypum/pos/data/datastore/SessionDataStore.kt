package com.mypum.pos.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(
    name = "session"
)

private object SessionKeys {
    val USER_ID: Preferences.Key<Long> = longPreferencesKey("user_id")
}

class SessionDataStore(
    private val context: Context
) {

    val activeUserId: Flow<Long?> =
        context.sessionDataStore.data.map { preferences ->
            preferences[SessionKeys.USER_ID]
        }

    suspend fun setUser(id: Long) {
        context.sessionDataStore.edit { preferences ->
            preferences[SessionKeys.USER_ID] = id
        }
    }

    suspend fun clearUser() {
        context.sessionDataStore.edit { preferences ->
            preferences.remove(SessionKeys.USER_ID)
        }
    }
}
