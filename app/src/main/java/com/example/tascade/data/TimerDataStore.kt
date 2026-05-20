package com.example.tascade.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timer_preferences")

class TimerDataStore(private val context: Context) {

    companion object {
        val TARGET_END_TIME = longPreferencesKey("target_end_time")
        val IS_WORK_SESSION = booleanPreferencesKey("is_work_session")
    }

    suspend fun saveTimerState(timeRemainingSeconds: Int, isWork: Boolean) {
        //we use future time because when the user closes the app and re opens at a later point in time then
        //then it will show the time that was saved when started which is wrong as time has passed since then
        val timeRemainingMillis = timeRemainingSeconds * 1000L
        val targetEndTime = System.currentTimeMillis() + timeRemainingMillis

        context.dataStore.edit { preferences ->
            preferences[TARGET_END_TIME] = targetEndTime
            preferences[IS_WORK_SESSION] = isWork
        }
    }

    val targetTimeFlow: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[TARGET_END_TIME] ?: 0L //to prevent crashing if the user just downloaded the app and assign it with 0 instead
        }

    val isWorkSessionFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_WORK_SESSION] ?: true
        }
}