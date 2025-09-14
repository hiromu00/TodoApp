package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.ViewModel.TaskViewModel
import com.example.todoapp.ui.theme.TodoAppTheme

class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskScreen(viewModel = taskViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    var newTitleText by remember { mutableStateOf("") }
    var newDescriptionText by remember { mutableStateOf("") }
    var newDueDateText by remember { mutableStateOf("") }
    var newPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    val isTitleValid = newTitleText.isNotBlank()
    val isFormValid = isTitleValid

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // ヘッダーセクション
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✨ Todo App",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "タスクを管理して、生産性を向上させましょう",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // タスク作成UI
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "新しいタスク",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedTextField(
                        value = newTitleText,
                        onValueChange = { newTitleText = it },
                        label = { Text("タスク名 *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = !isTitleValid,
                        supportingText = if (!isTitleValid) {
                            { Text("タスク名は必須です", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newDescriptionText,
                        onValueChange = { newDescriptionText = it },
                        label = { Text("説明") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        placeholder = { Text("タスクの詳細を入力...") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newDueDateText,
                        onValueChange = { newDueDateText = it },
                        label = { Text("期限日") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("YYYY-MM-DD") },
                        supportingText = { Text("例: 2024-12-31") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "優先度",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Priority.entries.forEach { priority ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .combinedClickable(
                                        onClick = { newPriority = priority },
                                        onLongClick = {}
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (newPriority == priority) {
                                        when (priority) {
                                            Priority.HIGH -> MaterialTheme.colorScheme.errorContainer
                                            Priority.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                                            Priority.LOW -> MaterialTheme.colorScheme.tertiaryContainer
                                        }
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (newPriority == priority) 4.dp else 1.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (priority) {
                                            Priority.HIGH -> "🔴"
                                            Priority.MEDIUM -> "🟡"
                                            Priority.LOW -> "🟢"
                                        },
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = when (priority) {
                                            Priority.HIGH -> "高"
                                            Priority.MEDIUM -> "中"
                                            Priority.LOW -> "低"
                                        },
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isFormValid) {
                                viewModel.addTask(
                                    newTitleText.trim(),
                                    newDescriptionText.takeIf { it.isNotBlank() } ?: "",
                                    newDueDateText.takeIf { it.isNotBlank() },
                                    newPriority
                                )
                                newTitleText = ""
                                newDescriptionText = ""
                                newDueDateText = ""
                                newPriority = Priority.MEDIUM
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = isFormValid,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("タスクを追加", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // タスク一覧セクション
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "📋 タスク一覧",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${tasks.size}件",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (tasks.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🎉", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "タスクがありません",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "新しいタスクを作成しましょう！",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(tasks) { task ->
                                TaskItem(
                                    task = task,
                                    onTaskClicked = { viewModel.toggleTaskCompleted(task) },
                                    onTaskLongClicked = { editingTask = task },
                                    onDeleteClicked = { viewModel.deleteTask(task) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 編集用ダイアログ
    editingTask?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { editingTask = null },
            onConfirm = { title, description, dueDate, priority ->
                viewModel.editTask(task, title, description, dueDate, priority)
                editingTask = null
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
    val elevation by animateFloatAsState(
        targetValue = if (task.isCompleted) 1.dp.value else 4.dp.value,
        animationSpec = tween(300),
        label = "elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onTaskClicked,
                onLongClick = onTaskLongClicked
            )
            .shadow(elevation = elevation.dp, shape = RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onTaskClicked() },
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )

                task.description?.let { description ->
                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    task.dueDate?.let { dueDate ->
                        if (dueDate.isNotBlank()) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = "📅 $dueDate",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (task.priority) {
                                Priority.HIGH -> MaterialTheme.colorScheme.errorContainer
                                Priority.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                                Priority.LOW -> MaterialTheme.colorScheme.tertiaryContainer
                            }
                        )
                    ) {
                        Text(
                            text = when (task.priority) {
                                Priority.HIGH -> "🔴 高"
                                Priority.MEDIUM -> "🟡 中"
                                Priority.LOW -> "🟢 低"
                            },
                            fontSize = 12.sp,
                            color = when (task.priority) {
                                Priority.HIGH -> MaterialTheme.colorScheme.onErrorContainer
                                Priority.MEDIUM -> MaterialTheme.colorScheme.onSecondaryContainer
                                Priority.LOW -> MaterialTheme.colorScheme.onTertiaryContainer
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(onClick = onDeleteClicked) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "タスクを削除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String?, Priority) -> Unit
) {
    var titleText by remember { mutableStateOf(task.title) }
    var descriptionText by remember { mutableStateOf(task.description ?: "") }
    var dueDateText by remember { mutableStateOf(task.dueDate ?: "") }
    var selectedPriority by remember { mutableStateOf(task.priority) }

    val isTitleValid = titleText.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("タスクを編集") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("タスク名 *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isTitleValid,
                    supportingText = if (!isTitleValid) {
                        { Text("タスク名は必須です", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    label = { Text("説明") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    placeholder = { Text("タスクの詳細を入力...") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = dueDateText,
                    onValueChange = { dueDateText = it },
                    label = { Text("期限日") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("YYYY-MM-DD") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text("優先度", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Priority.entries.forEach { priority ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .combinedClickable(
                                    onClick = { selectedPriority = priority },
                                    onLongClick = {}
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPriority == priority) {
                                    when (priority) {
                                        Priority.HIGH -> MaterialTheme.colorScheme.errorContainer
                                        Priority.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                                        Priority.LOW -> MaterialTheme.colorScheme.tertiaryContainer
                                    }
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (selectedPriority == priority) 4.dp else 1.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = when (priority) {
                                        Priority.HIGH -> "🔴"
                                        Priority.MEDIUM -> "🟡"
                                        Priority.LOW -> "🟢"
                                    },
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (priority) {
                                        Priority.HIGH -> "高"
                                        Priority.MEDIUM -> "中"
                                        Priority.LOW -> "低"
                                    },
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isTitleValid) {
                        onConfirm(
                            titleText.trim(),
                            descriptionText.takeIf { it.isNotBlank() },
                            dueDateText.takeIf { it.isNotBlank() },
                            selectedPriority
                        )
                    }
                },
                enabled = isTitleValid
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
