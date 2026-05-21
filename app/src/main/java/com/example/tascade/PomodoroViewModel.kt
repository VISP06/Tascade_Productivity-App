package com.example.tascade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.tascade.data.TimerDataStore
import kotlinx.coroutines.flow.first

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

    init {
        viewModelScope.launch {
            val state = dataStore.savedStateFlow.first()
            _isWorkSession.value = state.isWork
            _timerValue.value = state.timeRemaining
            _isRunning.value = false
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        viewModelScope.launch {
            dataStore.saveTimerState(timeRemaining = _timerValue.value, isWork = _isWorkSession.value, isRunning = false)
        }
    }

    fun resetTimer() {
        _isRunning.value = false
        _timerValue.value = _workDuration.value
        _isWorkSession.value = true
        viewModelScope.launch {
            //datastore gets resetted
            dataStore.saveTimerState(timeRemaining = _workDuration.value, isWork = true, isRunning = false)
        }
    }

    fun startTimer() {
        if (_isRunning.value) return

        viewModelScope.launch {
            _isRunning.value = true
            dataStore.saveTimerState(timeRemaining = _timerValue.value, isWork = _isWorkSession.value, isRunning = true)

            while (_isRunning.value) {
                if (_timerValue.value > 0) {
                    delay(1000L)
                    if (_isRunning.value) {
                        _timerValue.value -= 1
                        if (_timerValue.value % 5 == 0) {
                            viewModelScope.launch {
                                dataStore.saveTimerState(timeRemaining = _timerValue.value, isWork = _isWorkSession.value, isRunning = true)
                            }
                        }
                    }
                } else {
                    delay(1000L)
                    if (_isWorkSession.value) {
                        _isWorkSession.value = false
                        _timerValue.value = _breakDuration.value
                        //save state on Work->Break transition
                        dataStore.saveTimerState(timeRemaining = _timerValue.value, isWork = _isWorkSession.value, isRunning = true)
                    } else {
                        _isRunning.value = false
                        _isWorkSession.value = true
                        _timerValue.value = _workDuration.value
                        //save state on session end
                        dataStore.saveTimerState(timeRemaining = _timerValue.value, isWork = _isWorkSession.value, isRunning = false)
                    }
                }
            }
        }
    }

    fun increaseWorkTime() {
        if (_workDuration.value < 6000){
            _workDuration.value += 60
        }
    }

    fun decreaseWorkTime() {
        if (_workDuration.value > 60) {
            _workDuration.value -= 60
        }
    }

    fun increaseBreakTime() {
        if (_breakDuration.value < 1800){
            _breakDuration.value += 60
        }
    }

    fun decreaseBreakTime() {
        if (_breakDuration.value > 60){
            _breakDuration.value -= 60
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