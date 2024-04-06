package com.example.todoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert
    fun insertTask(task: Task)
    @Query("SELECT * FROM Task")
    fun loadALLTask() : List<Task>
    @Update
    fun updateTask(task: Task)
    @Delete
    fun deleteTask(task: Task)
}