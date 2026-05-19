package com.example.tascade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tascade.data.TimerDataStore.Companion.IS_WORK_SESSION
import com.example.tascade.data.TimerDataStore.Companion.TARGET_END_TIME
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit


class PomodoroViewModel : ViewModel() {

    private val _timerValue = MutableStateFlow(1500)
    val timerValue: StateFlow<Int> = _timerValue.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _isWorkSession = MutableStateFlow(true)
    val isWorkSession: StateFlow<Boolean> = _isWorkSession.asStateFlow()

    private val _workDuration = MutableStateFlow(1500)
    val workDuration: StateFlow<Int> = _workDuration.asStateFlow()

    private val _breakDuration = MutableStateFlow(300)
    val breakDuration: StateFlow<Int> = _breakDuration.asStateFlow()

    fun pauseTimer() {
        _isRunning.value = false
    }

    fun resetTimer() {
        _isRunning.value = false
        _timerValue.value = _workDuration.value
        _isWorkSession.value = true
    }
//reset button wouldn't reset properly back to 25 min for ex because of co-routines and delay func
    fun startTimer() {
        if (_isRunning.value) return

        viewModelScope.launch {
            _isRunning.value = true

            while (_isRunning.value) {

                if (_timerValue.value > 0) {
                    delay(1000L)
                    if (_isRunning.value) {
                        _timerValue.value -= 1
                    }
                } else {
                    delay(1000L)
                    if (_isWorkSession.value) {
                        _isWorkSession.value = false
                        _timerValue.value = _breakDuration.value

                    } else {
                        _isRunning.value = false
                        _isWorkSession.value = true
                        _timerValue.value = _workDuration.value
                    }
                }
            }
        }
    }
    fun increaseWorkTime() {
        if (_workDuration.value < 6000) {
            _workDuration.value += 60
        }
    }

    fun decreaseWorkTime() {
        if (_workDuration.value > 60) {
            _workDuration.value -= 60
        }
    }

    fun increaseBreakTime(){
        if(_breakDuration.value<1800){
            _breakDuration.value += 60
        }
    }

    fun decreaseBreakTime(){
        if(_breakDuration.value>60){
            _breakDuration.value -= 60
        }
    }

    //we use future time because when the user closes the app and re opens at a later point in time then
    //then it will show the time that was saved when started which is wrong as time has passed since then


}