package com.example.tascade.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("timer_prefs")

data class SavedTimerState(
    val targetTime: Long,
    val timeRemaining: Int,
    val isRunning: Boolean,
    val isWork: Boolean
)

class TimerDataStore(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        //we use future time because when the user closes the app and re opens at a later point in time then
        //then it will show the time that was saved when started which is wrong as time has passed since then
        val TARGET_TIME = longPreferencesKey("target_time")
        val TIME_REMAINING = intPreferencesKey("time_remaining")
        val IS_RUNNING = booleanPreferencesKey("is_running")
        val IS_WORK = booleanPreferencesKey("is_work")
    }

    // Read the single package
    val savedStateFlow: Flow<SavedTimerState> = dataStore.data.map { prefs ->
        SavedTimerState(
            targetTime = prefs[TARGET_TIME] ?: 0L,
            timeRemaining = prefs[TIME_REMAINING] ?: 1500,
            isRunning = prefs[IS_RUNNING] ?: false,
            isWork = prefs[IS_WORK] ?: true
        )
    }

    suspend fun saveTimerState(timeRemaining: Int, isWork: Boolean, isRunning: Boolean) {
        dataStore.edit { prefs ->
            prefs[TIME_REMAINING] = timeRemaining
            prefs[IS_WORK] = isWork
            prefs[IS_RUNNING] = isRunning

            if (isRunning) {
                prefs[TARGET_TIME] = System.currentTimeMillis() + (timeRemaining * 1000L)
            } else {
                prefs[TARGET_TIME] = 0L
            }
        }
    }
}