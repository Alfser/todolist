package com.example.todolist.datasource.remote

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.todolist.datasource.Result
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.datasource.model.Task
import kotlinx.coroutines.delay
import java.lang.Error
import java.lang.Exception

object TaskRemoteDataSource : TaskDataSource {

    //time to simulate network delay
    private const val NETWORK_LATENCY_IN_MILLIS: Long = 2000L

    private var tasksRemoteData =LinkedHashMap<String, Task>(3)

    init {
        addTask(
            Task(title = "Reunião do projeto vegas",
            description = "falar sobre os vanços no projeto e as mudanças",
            date = "06/05/2021",
            time = "19:00"
            )
        )

        addTask(
            Task(title = "Manutenção App Ford",
                description = "Realizar a manutenção no App forde",
                date = "03/04/2021",
                time = "18:00"
            )
        )

        addTask(
            Task(title = "Documentação da API arco",
                description = "Realizar a manutenção da Api do prjeto Arco",
                date = "02/06/2021",
                time = "15:00"
            )
        )
    }

    private val observableTasks = MutableLiveData<Result<List<Task>>>()
    
    private fun addTask(newTask: Task){
        tasksRemoteData[newTask.id] = newTask
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return observableTasks
    }

    override suspend fun getTasks(): Result<List<Task>> {
        val tasks = tasksRemoteData.values.toList()
        delay(NETWORK_LATENCY_IN_MILLIS)
        return Result.Success(tasks)
    }

    @SuppressLint("NullSafeMutableLiveData")
    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return observableTasks.map { tasks ->
            when(tasks){
                is Result.Loading -> Result.Loading
                is Result.Error -> Result.Error(Exception(tasks.exception))
                is Result.Success -> {
                    val task = tasks.data.firstOrNull(){it.id == taskId}
                        ?: return@map Result.Error(Exception("Tarefa não encontrada"))
                    Result.Success(task)
                }
            }
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        delay(NETWORK_LATENCY_IN_MILLIS/2)
        tasksRemoteData[taskId]?.let {
            return Result.Success(it)
        }

        return Result.Error(Exception("Tarefa não encontrada"))
    }

    override suspend fun refreshTask(taskId: String) {
        refreshTasks()
    }

    override suspend fun saveTask(task: Task) {
        delay(NETWORK_LATENCY_IN_MILLIS/2)
        tasksRemoteData[task.id] = task
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(
            task.id,
            task.title,
            task.description,
            task.date,
            task.time,
            true,
            task.archived,
            task.favorite
        )

        tasksRemoteData[completedTask.id] = completedTask
    }

    override suspend fun completeTask(taskId: String) {
        //NOP
    }

    override suspend fun activeTask(task: Task) {
        val activeTask = Task(
            task.id,
            task.title,
            task.description,
            task.date,
            task.time,
            false,
            task.archived,
            task.favorite
        )

        tasksRemoteData[activeTask.id] = activeTask
    }

    override suspend fun activeTask(taskId: String) {
        //NOP
    }

    override suspend fun deleteAllTasks() {
        delay(NETWORK_LATENCY_IN_MILLIS)
        tasksRemoteData.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        delay(NETWORK_LATENCY_IN_MILLIS/2)
        tasksRemoteData.remove(taskId)
    }

    override suspend fun deleteTasksCompleted() {
        delay(NETWORK_LATENCY_IN_MILLIS)
        tasksRemoteData = tasksRemoteData.filterValues { task ->
            !task.completed
        } as LinkedHashMap<String, Task>
    }
}