package com.example.todoapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.todoapp.Task
import com.example.todoapp.Priority
import com.example.todoapp.TaskDao
import com.example.todoapp.TaskDatabase

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao: TaskDao

    // UIに公開するタスクリストの状態
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        // ViewModelが作成された時にDBインスタンスとDAOを取得
        taskDao = TaskDatabase.getDatabase(application).taskDao()
        // タスク一覧を読み込む
        loadTasks()
    }

    // F-03: タスク一覧表示
    private fun loadTasks() {
        // IOスレッドでDBからデータを取得し、UIの状態を更新
        viewModelScope.launch(Dispatchers.IO) {
            _tasks.value = taskDao.loadALLTask().sortedBy { it.isCompleted }
        }
    }

    // F-02: タスク作成
    fun addTask(title: String, description: String = "", dueDate: String? = null, priority: Priority = Priority.MEDIUM) {
        viewModelScope.launch(Dispatchers.IO) {
            if (title.isNotBlank()) {
                val task = Task(
                    title = title,
                    description = description.takeIf { it.isNotBlank() },
                    dueDate = dueDate,
                    priority = priority
                )
                taskDao.insertTask(task)
                loadTasks()
            }
        }
    }

    // F-04: タスク完了/未完了の切り替え
    fun toggleTaskCompleted(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            taskDao.updateTask(updatedTask)
            loadTasks()
        }
    }

    fun editTask(
        task: Task,
        newTitle: String,
        newDescription: String?,
        newDueDate: String?,
        newPriority: Priority
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (newTitle.isNotBlank()) {
                val updatedTask = task.copy(
                    title = newTitle,
                    description = newDescription,
                    dueDate = newDueDate,
                    priority = newPriority
                )
                taskDao.updateTask(updatedTask)
                loadTasks()
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteTask(task)
            loadTasks()
        }
    }
}
