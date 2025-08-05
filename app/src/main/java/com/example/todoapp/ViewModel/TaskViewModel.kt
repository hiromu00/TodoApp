package com.example.todoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
    fun addTask(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (title.isNotBlank()) {
                taskDao.insertTask(Task(title = title))
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

    fun editTask(task: Task, newTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (newTitle.isNotBlank()) {
                val updatedTask = task.copy(title = newTitle)
                taskDao.updateTask(updatedTask)
                loadTasks() // 更新後にリストを再読み込み
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