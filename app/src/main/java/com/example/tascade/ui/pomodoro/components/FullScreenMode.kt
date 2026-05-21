package com.example.tascade.ui.pomodoro.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tascade.PomodoroViewModel
import com.example.tascade.ui.theme.BebasNeue

@Composable
fun FullScreenMode(
    timeString: String,
    pomodoroViewModel: PomodoroViewModel,
    modifier: Modifier = Modifier,
    onExpand: () -> Unit
) {
    val isWorkSession by pomodoroViewModel.isWorkSession.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (-8).dp, y = (-8).dp)
                .background(color = if(pomodoroViewModel.isWorkSession.collectAsState().value) {Color.White} else { Color.Cyan })
                .border(width = 4.dp, color = Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Compress,
                contentDescription = "Exit Fullscreen",
                tint = Color.Black,
                modifier = Modifier
                    .padding(16.dp)
                    .size(32.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onExpand() }
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isWorkSession) "WORK SESSION" else "BREAK TIME",
                    color = Color(0xFF7B1FA2),
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontFamily = BebasNeue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                )

                Text(
                    text = timeString,
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = BebasNeue
                    )
                )
            }
        }
    }
}