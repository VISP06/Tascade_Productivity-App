package com.example.tascade.ui.notes

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tascade.model.Note
import com.example.tascade.ui.theme.BebasNeue
import com.example.tascade.util.halftoneBackground

@Composable
fun NotesListScreen(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onAddNoteClick: () -> Unit,
    onDeleteNote: (Note) -> Unit,
    globalPadding: PaddingValues
) {
    Scaffold(
        topBar = {
            NotesTopBar(title = "NOTES")
        },
        floatingActionButton = {
            NotesFAB(
                onClick = onAddNoteClick,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFACC15)) // Vibrant yellow
                .halftoneBackground()
                .padding(innerPadding)
        ) {
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Neo-brutalist empty state card
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.Black)
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(x = (-4).dp, y = (-4).dp)
                                .background(Color.White)
                                .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                                .padding(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color(0xFF1A237E) // Deep Blue
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "NO NOTES YET",
                                    color = Color.Black,
                                    fontSize = 24.sp,
                                    fontFamily = BebasNeue,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "TAP THE FAB TO CREATE A NEW ONE!",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1), // Single-column list
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNoteClick(note) },
                            onDeleteClick = { onDeleteNote(note) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesTopBar(modifier: Modifier = Modifier, title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(Color(0xFF1A237E)) // Deep Blue
            .border(width = 3.dp, color = Color.Black)
            .padding(start = 36.dp, top = 40.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 5.sp,
            fontSize = 64.sp,
            fontFamily = BebasNeue,
            modifier = modifier
        )
    }
}

@Composable
fun NotesFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offsetAnimation by animateDpAsState(
        targetValue = if (isPressed) 0.dp else (-4).dp,
        label = "FABPress"
    )

    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        Box(
            modifier = Modifier
                .offset(x = offsetAnimation, y = offsetAnimation)
                .background(Color(0xFFFACC15)) // Vibrant Yellow
                .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Note",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offsetAnimation by animateDpAsState(
        targetValue = if (isPressed) 0.dp else (-4).dp,
        label = "NoteCardPress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        Box(
            modifier = Modifier
                .offset(x = offsetAnimation, y = offsetAnimation)
                .background(Color.White)
                .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = note.title.ifEmpty { "Untitled" },
                        color = Color(0xFF1A237E), // Deep Blue
                        fontSize = 24.sp,
                        fontFamily = BebasNeue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = note.content,
                        color = Color.Black,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val dateStr = remember(note.timestamp) {
                        java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(note.timestamp))
                    }
                    Text(
                        text = dateStr,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // Delete button
                val deleteInteractionSource = remember { MutableInteractionSource() }
                val deleteIsPressed by deleteInteractionSource.collectIsPressedAsState()
                val deleteOffsetAnimation by animateDpAsState(
                    targetValue = if (deleteIsPressed) 0.dp else (-2).dp,
                    label = "DeletePress"
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black)
                        .clickable(
                            onClick = onDeleteClick,
                            interactionSource = deleteInteractionSource,
                            indication = null
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = deleteOffsetAnimation, y = deleteOffsetAnimation)
                            .background(Color(0xFFEF4444)) // Red
                            .border(width = 1.5.dp, color = Color.Black, shape = RectangleShape)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
