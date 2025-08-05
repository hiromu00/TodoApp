package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.ui.theme.TodoAppTheme

class MainActivity : ComponentActivity() {
    // ViewModelをインスタンス化
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // メイン画面のComposableを呼び出し
                    TaskScreen(viewModel = taskViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class) // combinedClickable を使うために必要
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    // ViewModelからタスクリストの状態を監視
    val tasks by viewModel.tasks.collectAsState()
    var newTitleText by remember { mutableStateOf("") }

    // 編集対象のタスクと、ダイアログの表示状態を管理
    var editingTask by remember { mutableStateOf<Task?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Todoリスト", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        // タスク作成UI
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTitleText,
                onValueChange = { newTitleText = it },
                label = { Text("新しいタスク") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                viewModel.addTask(newTitleText)
                newTitleText = ""
            }) {
                Text("追加")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // タスク一覧表示
        LazyColumn {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onTaskClicked = { viewModel.toggleTaskCompleted(task) },
                    onTaskLongClicked = { editingTask = task }, // 長押しで編集対象に設定
                    onDeleteClicked = { viewModel.deleteTask(task) }
                )
            }
        }
    }

    // 編集用ダイアログ
    if (editingTask != null) {
        EditTaskDialog(
            task = editingTask!!,
            onDismiss = { editingTask = null },
            onConfirm = { updatedTitle ->
                viewModel.editTask(editingTask!!, updatedTitle)
                editingTask = null // ダイアログを閉じる
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    task: Task,
    onTaskClicked: () -> Unit,
    onTaskLongClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onTaskClicked,
                onLongClick = onTaskLongClicked
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onTaskClicked() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = task.title,
            fontSize = 18.sp,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f) // テキストが残りのスペースを埋めるように
        )
        // 削除ボタン
        IconButton(onClick = onDeleteClicked) {
            Icon(Icons.Filled.Delete, contentDescription = "タスクを削除")
        }
    }
}

@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(task.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("タスクを編集") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("タスク名") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}