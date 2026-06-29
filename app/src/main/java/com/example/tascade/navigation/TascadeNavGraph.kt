package com.example.tascade.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tascade.PomodoroViewModel
import com.example.tascade.TodoViewModel
import com.example.tascade.NotesViewModel
import com.example.tascade.NotesViewModelFactory
import com.example.tascade.data.OfflineTodoRepository
import com.example.tascade.data.TodoDatabase
import com.example.tascade.data.NoteDatabase
import com.example.tascade.data.OfflineNoteRepository
import com.example.tascade.navigation.AppRoutes.POMODORO
import com.example.tascade.navigation.AppRoutes.TASKS
import com.example.tascade.navigation.AppRoutes.NOTES
import com.example.tascade.navigation.AppRoutes.DETAIL_NOTE
import com.example.tascade.navigation.AppRoutes.EDIT_NOTE
import com.example.tascade.ui.pomodoro.PomodoroScreen
import com.example.tascade.ui.todo.TodoScreen
import com.example.tascade.ui.notes.NotesListScreen
import com.example.tascade.ui.notes.NoteDetailScreen
import com.example.tascade.ui.notes.NoteEditScreen

@Composable
fun TascadeNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    pomodoroViewModel: PomodoroViewModel,
    isFullScreen: Boolean,
    onFullScreenToggle: () -> Unit
){
    val context = LocalContext.current
    val notesDatabase = remember { NoteDatabase.getDatabase(context) }
    val notesRepository = remember { OfflineNoteRepository(notesDatabase.noteDao()) }
    val notesViewModel: NotesViewModel = viewModel(factory = NotesViewModelFactory(notesRepository))

    NavHost(
        navController = navController,
        startDestination = TASKS,
        modifier = modifier
    ){
        composable(route = TASKS){
            //Database Instantiation
            val databaseObject = TodoDatabase.getDatabase(context = LocalContext.current)
            //ViewModel Instantiation
            val vm = remember { TodoViewModel(repository = OfflineTodoRepository(todoDao = databaseObject.todoDao())) }
            val tasks by vm.todos.collectAsState()
            TodoScreen(
                tasks = tasks,
                vm = vm,
                globalPadding = innerPadding
            )
        }
        composable(route = POMODORO){
            PomodoroScreen(globalPadding = innerPadding, pomodoroViewModel = pomodoroViewModel, isFullScreen = isFullScreen,
                onFullScreenToggle = onFullScreenToggle)
        }
        composable(route = NOTES){
            val notes by notesViewModel.notes.collectAsState()
            NotesListScreen(
                notes = notes,
                onNoteClick = { note ->
                    navController.navigate("detail_note_route?noteId=${note.id}")
                },
                onAddNoteClick = {
                    navController.navigate("edit_note_route")
                },
                globalPadding = innerPadding
            )
        }
        composable(
            route = DETAIL_NOTE,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            val notesList by notesViewModel.notes.collectAsState()
            val note = notesList.find { it.id == noteId }

            NoteDetailScreen(
                note = note,
                onNavigateUp = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate("edit_note_route?noteId=$noteId")
                },
                onDeleteClick = {
                    if (note != null) {
                        notesViewModel.deleteNote(note)
                    }
                },
                globalPadding = innerPadding
            )
        }
        composable(
            route = EDIT_NOTE,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            val notesList by notesViewModel.notes.collectAsState()
            val note = notesList.find { it.id == noteId }

            NoteEditScreen(
                note = note,
                onSaveClick = { title, content ->
                    if (note == null) {
                        notesViewModel.addNote(title, content)
                    } else {
                        notesViewModel.updateNote(
                            note.copy(title = title, content = content, timestamp = System.currentTimeMillis())
                        )
                    }
                },
                onNavigateUp = {
                    navController.popBackStack()
                },
                globalPadding = innerPadding
            )
        }
    }
}