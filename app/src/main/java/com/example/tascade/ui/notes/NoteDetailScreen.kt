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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun NoteDetailScreen(
    note: Note?,
    onNavigateUp: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
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

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFF1A237E)) // Deep Blue
                    .border(width = 3.dp, color = Color.Black)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
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
                            text = "VIEW NOTE",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 5.sp,
                            fontSize = 44.sp,
                            fontFamily = BebasNeue
                        )
                    }

                    // Delete button in top bar actions
                    if (note != null) {
                        val deleteInteractionSource = remember { MutableInteractionSource() }
                        val deleteIsPressed by deleteInteractionSource.collectIsPressedAsState()
                        val deleteOffset by animateDpAsState(
                            targetValue = if (deleteIsPressed) 0.dp else (-3).dp,
                            label = "DeleteBtn"
                        )
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.Black)
                                .clickable(
                                    onClick = {
                                        onDeleteClick()
                                        onNavigateUp()
                                    },
                                    interactionSource = deleteInteractionSource,
                                    indication = null
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .offset(x = deleteOffset, y = deleteOffset)
                                    .background(Color(0xFFEF4444)) // Red
                                    .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFACC15)) // Yellow
                .halftoneBackground()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (note == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NOTE NOT FOUND",
                        fontFamily = BebasNeue,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Scrollable note title and content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Title Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                        ) {
                            Box(
                                modifier = Modifier
                                    .offset(x = (-4).dp, y = (-4).dp)
                                    .background(Color.White)
                                    .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = note.title.ifEmpty { "Untitled" },
                                        color = Color(0xFF1A237E), // Deep Blue
                                        fontSize = 32.sp,
                                        fontFamily = BebasNeue,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val dateStr = remember(note.timestamp) {
                                        java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(note.timestamp))
                                    }
                                    Text(
                                        text = "Last updated: $dateStr",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Content Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                        ) {
                            Box(
                                modifier = Modifier
                                    .offset(x = (-4).dp, y = (-4).dp)
                                    .background(Color.White)
                                    .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = note.content.ifEmpty { "No content" },
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Edit Action Button at the bottom
                    val editInteractionSource = remember { MutableInteractionSource() }
                    val editIsPressed by editInteractionSource.collectIsPressedAsState()
                    val editOffset by animateDpAsState(
                        targetValue = if (editIsPressed) 0.dp else (-4).dp,
                        label = "EditBtn"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .clickable(
                                onClick = onEditClick,
                                interactionSource = editInteractionSource,
                                indication = null
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(x = editOffset, y = editOffset)
                                .background(Color(0xFF1A237E)) // Deep Blue
                                .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "EDIT",
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
}
