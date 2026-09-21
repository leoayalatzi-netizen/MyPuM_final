package com.mypum.pos.data.datastore
import androidx.datastore.preferences.core.edit
import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
private val Context.sessionStore by preferencesDataStore("session")
class SessionDataStore(private val context:Context){ val activeUserId=context.sessionStore.data.map{it[longPreferencesKey("user_id")]}; suspend fun setUser(id:Long)=context.sessionStore.edit{it[longPreferencesKey("user_id")]=id} }
