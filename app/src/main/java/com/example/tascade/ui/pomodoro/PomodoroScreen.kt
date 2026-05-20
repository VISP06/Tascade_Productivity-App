package com.example.tascade.ui.pomodoro

import android.app.Activity
import android.media.SoundPool
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.tascade.PomodoroViewModel
import com.example.tascade.R
import com.example.tascade.ui.pomodoro.components.DecrementButton
import com.example.tascade.ui.pomodoro.components.FullScreenMode
import com.example.tascade.ui.pomodoro.components.IncrementButton
import com.example.tascade.ui.pomodoro.components.PauseButton
import com.example.tascade.ui.pomodoro.components.PomodoroBoard
import com.example.tascade.ui.pomodoro.components.ResetButton
import com.example.tascade.ui.pomodoro.components.StartButton
import com.example.tascade.ui.pomodoro.components.TimeAdjusterBlock
import com.example.tascade.ui.todo.components.TodoTopBar
import com.example.tascade.util.formatTimeString
import com.example.tascade.util.halftoneBackground

@Composable
fun PomodoroScreen(
    globalPadding: PaddingValues,
    modifier:Modifier = Modifier,
    pomodoroViewModel: PomodoroViewModel,
    isFullScreen: Boolean,
    onFullScreenToggle: () -> Unit
) {
    //if user pressed physical back button when in full screen, it has to go back to timer screen and not make bottom bar disappear
    BackHandler(enabled = isFullScreen) {
        onFullScreenToggle()
    } //basically what this means is, if we are in full screen mode then we just call that toggle method to take us out of it. that is what the back method will do for that case
    val time by pomodoroViewModel.timerValue.collectAsState()
    val workTime by pomodoroViewModel.workDuration.collectAsState()
    val breakTime by pomodoroViewModel.breakDuration.collectAsState()

    val timeString = formatTimeString(time)

    val context = LocalContext.current
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(5)
            .build()
    }
    val alarmSoundId = remember {
        soundPool.load(context, R.raw.pomodoro_alarm, 1)
    }

    LaunchedEffect(time) {
        if(time == 0)
            soundPool.play(alarmSoundId, 1f, 1f, 1, 0, 1f)
    }
    val view = LocalView.current
    val window = (view.context as Activity).window

    // 2. The Effect that watches your isFullScreen variable
    DisposableEffect(isFullScreen) {
        val insetsController = WindowCompat.getInsetsController(window, view)
        if (isFullScreen) {
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    if(isFullScreen){
        FullScreenMode(timeString = timeString, pomodoroViewModel = pomodoroViewModel, onExpand = onFullScreenToggle)
    }else {
        Scaffold(
            topBar = { TodoTopBar(topBarHeadingId = R.string.top_bar_title2) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFFACC15))
                    .halftoneBackground(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier.align(Alignment.TopCenter).padding(24.dp)
                ) {
                    PomodoroBoard(
                        timeString = timeString,
                        pomodoroViewModel = pomodoroViewModel,
                        onExpand = onFullScreenToggle
                    )
                    Spacer(
                        modifier = modifier.padding(12.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TimeAdjusterBlock(
                            boxName = "WORK",
                            timeValue = workTime,
                            decrementButton = { DecrementButton(onClick = { pomodoroViewModel.decreaseWorkTime() }) },
                            incrementButton = { IncrementButton(onClick = { pomodoroViewModel.increaseWorkTime() }) }
                        )

                        TimeAdjusterBlock(
                            boxName = "BREAK",
                            timeValue = breakTime,
                            decrementButton = { DecrementButton(onClick = { pomodoroViewModel.decreaseBreakTime() }) },
                            incrementButton = { IncrementButton(onClick = { pomodoroViewModel.increaseBreakTime() }) }
                        )
                    }
                    Spacer(
                        modifier = modifier.padding(12.dp)
                    )
                    AnimatedContent(
                        targetState = pomodoroViewModel.isRunning.collectAsState().value
                    ) { isActive -> //"extremely smart" solution of just adding ! to the isActive in if condition helps my app to work as intended
                        if (!isActive) {
                            StartButton(pomodoroViewModel = pomodoroViewModel)
                        } else {
                            PauseButton(pomodoroViewModel = pomodoroViewModel)
                        }
                    }

                    Spacer(
                        modifier = modifier.padding(12.dp)
                    )
                    ResetButton(pomodoroViewModel = pomodoroViewModel)
                }
            }
        }
    }
}