package com.example.todolist.datasource

import androidx.lifecycle.LiveData
import com.example.todolist.datasource.model.Task

interface TaskDataSource {

    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun getTasks(): Result<List<Task>>

    suspend fun refreshTasks()

    fun observeTask(taskId: String):LiveData<Result<Task>>

    suspend fun getTask(taskId: String):Result<Task>

    suspend fun refreshTask(taskId: String)

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activeTask(task: Task)

    suspend fun activeTask(taskId: String)

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)

    suspend fun deleteTasksCompleted()
}