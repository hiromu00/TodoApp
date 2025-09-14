package com.example.todoapp

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority {
    LOW, MEDIUM, HIGH
}

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    var isCompleted: Boolean = false,
    val dueDate: String? = null,
    val priority: Priority = Priority.MEDIUM
)
