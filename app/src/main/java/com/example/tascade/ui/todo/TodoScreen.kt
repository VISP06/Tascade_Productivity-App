package com.example.tascade.ui.todo

import android.media.SoundPool
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tascade.R
import com.example.tascade.TodoViewModel
import com.example.tascade.model.Todo
import com.example.tascade.ui.todo.components.TodoCard
import com.example.tascade.ui.todo.components.TodoFAB
import com.example.tascade.ui.todo.components.TodoTopBar
import com.example.tascade.util.halftoneBackground

@Composable
fun TodoScreen(
    globalPadding: PaddingValues,
    vm: TodoViewModel,
    tasks: List<Todo>
){
    //Sound effects for clicking
    val context = LocalContext.current

    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(5)
            .build()
    }

    val buttonSoundId = remember {
        soundPool.load(context, R.raw.button_press, 1)
    }

    val scratchSoundId = remember {
        soundPool.load(context, R.raw.todo_scratch, 1)
    }

    Scaffold(
        topBar = { TodoTopBar(topBarHeadingId = R.string.top_bar_title1) },
        floatingActionButton = { TodoFAB(
            onAddClicked = {
                    incomingTitle:String ->
                vm.addTodo(titleInput = incomingTitle)
            },
            soundPool = soundPool,
            buttonSoundId = buttonSoundId,
            modifier = Modifier.padding(globalPadding),

        )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFACC15))
                .halftoneBackground()
                .padding(innerPadding),
            contentAlignment = Alignment.Center

        ) {
            val lazyListState = rememberLazyListState()

            TodoList(
                tasks = tasks,
                contentPaddingValues = globalPadding,
                onTaskChecked = { task ->
                    if (!task.isCompleted) {
                        soundPool.play(scratchSoundId, 1f, 1f, 1, 0, 1f)
                    }
                    vm.updateTask(task)
                },
                onTaskDeleted = { task -> vm.deleteTodo(task) },
                lazyListState = lazyListState,
                )

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoList(
    tasks: List<Todo>,
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues,
    onTaskChecked:(task:Todo)->Unit,
    onTaskDeleted: (task: Todo) -> Unit,
    lazyListState: LazyListState,

) {
    if (tasks.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.part_1_illustration),
                contentDescription = null
            )
            Image(
                painter = painterResource(R.drawable.part_2_illustration),
                contentDescription = null
            )
        }
    } else {
        LazyColumn(
            state = lazyListState,
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPaddingValues,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            itemsIndexed(items = tasks, key = { _, task -> task.id }) { _, task ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                            onTaskDeleted(task)
                            true
                        } else {
                            false
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        val color = when (dismissState.dismissDirection) {
                            SwipeToDismissBoxValue.EndToStart -> Color(0xFFF2C300).copy(alpha = 0.8f)
                            else -> Color.Transparent
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Black
                            )
                        }
                    },
                    modifier = Modifier.animateItem()
                ) {
                    TodoCard(
                        task = task,
                        onCheckedChange = { onTaskChecked(task) },
                        isCurrentlyChecked = task.isCompleted,
                    )
                }
            }
        }
    }
}

