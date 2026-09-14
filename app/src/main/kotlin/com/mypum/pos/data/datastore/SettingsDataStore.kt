package com.mypum.pos.data.datastore
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
val Context.settingsDataStore by preferencesDataStore("settings")
class SettingsDataStore(private val context:Context){ val darkMode=context.settingsDataStore.data.map{it[booleanPreferencesKey("dark_mode")]?:false} }
