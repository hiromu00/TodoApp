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

// フィルタリングで使用する完了状態の列挙型
enum class CompletionFilter {
    ALL, COMPLETED, NOT_COMPLETED
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao: TaskDao

    // 全タスクデータ（フィルタリング前）
    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())

    // UIに公開するタスクリストの状態（フィルタリング済み）
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    // フィルタリング用の状態
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _completionFilter = MutableStateFlow(CompletionFilter.ALL)
    val completionFilter: StateFlow<CompletionFilter> = _completionFilter

    private val _priorityFilters = MutableStateFlow(setOf<Priority>())
    val priorityFilters: StateFlow<Set<Priority>> = _priorityFilters

    init {
        // ViewModelが作成された時にDBインスタンスとDAOを取得
        taskDao = TaskDatabase.getDatabase(application).taskDao()
        // タスク一覧を読み込む
        loadTasks()
    }

    // F-03: タスク一覧表示
    private fun loadTasks() {
        // IOスレッドでDBからデータを取得し、全タスクの状態を更新
        viewModelScope.launch(Dispatchers.IO) {
            _allTasks.value = taskDao.loadALLTask().sortedBy { it.isCompleted }
            applyFilters()
        }
    }

    // フィルタリングを適用してタスクリストを更新
    private fun applyFilters() {
        val filteredTasks = _allTasks.value.filter { task ->
            // テキスト検索フィルタ
            val matchesSearch = if (_searchQuery.value.isBlank()) {
                true
            } else {
                task.title.contains(_searchQuery.value, ignoreCase = true) ||
                task.description?.contains(_searchQuery.value, ignoreCase = true) == true
            }

            // 完了状態フィルタ
            val matchesCompletion = when (_completionFilter.value) {
                CompletionFilter.ALL -> true
                CompletionFilter.COMPLETED -> task.isCompleted
                CompletionFilter.NOT_COMPLETED -> !task.isCompleted
            }

            // 優先度フィルタ（複数選択可）
            val matchesPriority = if (_priorityFilters.value.isEmpty()) {
                true
            } else {
                _priorityFilters.value.contains(task.priority)
            }

            matchesSearch && matchesCompletion && matchesPriority
        }

        _tasks.value = filteredTasks
    }

    // フィルタリング関数群
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun updateCompletionFilter(filter: CompletionFilter) {
        _completionFilter.value = filter
        applyFilters()
    }

    fun togglePriorityFilter(priority: Priority) {
        val currentFilters = _priorityFilters.value.toMutableSet()
        if (currentFilters.contains(priority)) {
            currentFilters.remove(priority)
        } else {
            currentFilters.add(priority)
        }
        _priorityFilters.value = currentFilters
        applyFilters()
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _completionFilter.value = CompletionFilter.ALL
        _priorityFilters.value = emptySet()
        applyFilters()
    }

    // F-02: タスク作成
    fun addTask(title: String, description: String? = null, dueDate: String? = null, priority: Priority = Priority.MEDIUM) {
        viewModelScope.launch(Dispatchers.IO) {
            if (title.isNotBlank()) {
                val task = Task(
                    title = title,
                    description = description,
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
