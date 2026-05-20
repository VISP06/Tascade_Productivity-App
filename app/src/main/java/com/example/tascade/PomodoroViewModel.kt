package com.example.tascade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.tascade.data.TimerDataStore
import kotlin.reflect.KClass


class PomodoroViewModel(val dataStore: TimerDataStore) : ViewModel() {
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
            dataStore.saveTimerState(timeRemainingSeconds = _timerValue.value, isWork = _isWorkSession.value)
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

    init {
        viewModelScope.launch {
            dataStore.isWorkSessionFlow.collect { isWork ->
                _isWorkSession.value = isWork
            }
        }

        viewModelScope.launch {
            dataStore.targetTimeFlow.collect { targetTime ->
                //If the target time is 0, it means the file is empty (first time opening app)
                if (targetTime > 0L) {
                    val currentTime = System.currentTimeMillis()
                    val timeRemainingMillis = targetTime - currentTime

                    if (timeRemainingMillis > 0) {
                        _timerValue.value = (timeRemainingMillis / 1000).toInt()

                        if (!_isRunning.value) {
                            startTimer()
                        }
                    } else {
                        _timerValue.value = 0
                        _isRunning.value = false
                    }
                }
            }
        }
    }
}

class PomodoroViewModelFactory(private val timerDataStore: TimerDataStore) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PomodoroViewModel::class.java)) {
            return PomodoroViewModel(timerDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}