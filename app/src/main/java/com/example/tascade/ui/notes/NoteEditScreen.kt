package com.example.tascade.ui.notes

import android.media.SoundPool
import androidx.compose.ui.platform.LocalContext
import com.example.tascade.R
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tascade.model.Note
import com.example.tascade.ui.theme.BebasNeue
import com.example.tascade.util.halftoneBackground

@Composable
fun NoteEditScreen(
    note: Note?,
    onSaveClick: (title: String, content: String) -> Unit,
    onNavigateUp: () -> Unit,
    globalPadding: PaddingValues
) {
    val context = LocalContext.current
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(5)
            .build()
    }
    val pageTurnId = remember {
        soundPool.load(context, R.raw.page_turn, 1)
    }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    // Initialize state with note contents if it exists
    LaunchedEffect(note) {
        if (note != null) {
            title = note.title
            content = note.content
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFF1A237E)) // Deep Blue
                    .border(width = 3.dp, color = Color.Black)
                    .padding(start = 16.dp, top = 40.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val backInteractionSource = remember { MutableInteractionSource() }
                    val backIsPressed by backInteractionSource.collectIsPressedAsState()
                    val backOffset by animateDpAsState(
                        targetValue = if (backIsPressed) 0.dp else (-3).dp,
                        label = "BackBtn"
                    )
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.Black)
                            .clickable(
                                onClick = {
                                    soundPool.play(pageTurnId, 1f, 1f, 1, 0, 1f)
                                    onNavigateUp()
                                },
                                interactionSource = backInteractionSource,
                                indication = null
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(x = backOffset, y = backOffset)
                                .background(Color.White)
                                .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }

                    Text(
                        text = if (note == null) "NEW NOTE" else "EDIT NOTE",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 5.sp,
                        fontSize = 44.sp,
                        fontFamily = BebasNeue
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFACC15)) // Vibrant yellow background
                .halftoneBackground()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title Input
                NeoBrutalistTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title",
                    placeholder = "Enter note title...",
                    singleLine = true
                )

                // Content Input (expanded height)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Content",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontFamily = BebasNeue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color.Black)
                    ) {
                        TextField(
                            value = content,
                            onValueChange = { content = it },
                            placeholder = { Text("Write your thoughts here...", color = Color.Gray, fontSize = 16.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            modifier = Modifier
                                .offset(x = (-4).dp, y = (-4).dp)
                                .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                                .fillMaxSize()
                        )
                    }
                }

                // Save Button
                val saveInteractionSource = remember { MutableInteractionSource() }
                val saveIsPressed by saveInteractionSource.collectIsPressedAsState()
                val saveOffset by animateDpAsState(
                    targetValue = if (saveIsPressed) 0.dp else (-4).dp,
                    label = "SaveBtn"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .clickable(
                            onClick = {
                                if (title.isNotBlank() || content.isNotBlank()) {
                                    soundPool.play(pageTurnId, 1f, 1f, 1, 0, 1f)
                                    onSaveClick(title, content)
                                    onNavigateUp()
                                }
                            },
                            interactionSource = saveInteractionSource,
                            indication = null
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = saveOffset, y = saveOffset)
                            .background(Color(0xFF1A237E)) // Deep Blue
                            .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SAVE",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontFamily = BebasNeue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NeoBrutalistTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    singleLine: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 18.sp,
            fontFamily = BebasNeue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                placeholder = { Text(placeholder, color = Color.Gray, fontSize = 16.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                modifier = Modifier
                    .offset(x = (-4).dp, y = (-4).dp)
                    .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                    .fillMaxWidth()
            )
        }
    }
}
