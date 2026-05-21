package com.example.tascade.ui.pomodoro.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
fun PomodoroBoard(
    modifier: Modifier = Modifier,
    timeString: String,
    pomodoroViewModel: PomodoroViewModel,
    onExpand: () -> Unit
) {
    val sessionText = if (pomodoroViewModel.isWorkSession.collectAsState().value) "Work Session" else "Break Time"

    Box(
        modifier = modifier
            .height(250.dp)
            .aspectRatio(1.5f)
            .background(color = Color.Black)
            .fillMaxWidth(0.9f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (-4).dp, y = (-4).dp)
                .background(color = if(pomodoroViewModel.isWorkSession.collectAsState().value) {Color.White} else { Color.Cyan })
                .border(color = Color.Black, shape = RectangleShape, width = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Expand,
                contentDescription = "Exit Fullscreen",
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(32.dp)
                    .clickable { onExpand() }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = sessionText,
                    color = Color(0xFF7B1FA2),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontFamily = BebasNeue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )
                Text(
                    text = timeString,
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 100.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = BebasNeue
                    )
                )
            }
        }
    }
}