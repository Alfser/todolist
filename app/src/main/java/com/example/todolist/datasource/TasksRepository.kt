package com.example.todolist.datasource

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.todolist.datasource.local.TaskDatabase
import com.example.todolist.datasource.local.TaskLocalDataSource
import com.example.todolist.datasource.model.Task
import com.example.todolist.datasource.remote.TaskRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class TasksRepository private constructor( application: Application) {

    private val taskRemoteDataSource: TaskRemoteDataSource
    private val taskLocalDataSource: TaskLocalDataSource
    private val ioDispatcher = Dispatchers.IO

    companion object{
        @Volatile
        private var instance: TasksRepository? = null

        fun getRepository(application: Application): TasksRepository{
            return instance ?: synchronized(this){
                TasksRepository(application).also {
                    instance = it
                }
            }
        }
    }


    init {
        val database = Room.databaseBuilder(
            application.applicationContext,
            TaskDatabase::class.java,
            "task.db"
        ).build()

        taskRemoteDataSource = TaskRemoteDataSource
        taskLocalDataSource = TaskLocalDataSource(database.taskDao())
    }

    suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>>{
        if(forceUpdate){
            try {
                updateTasksFromRemoteDataSource()
            }
            catch (e: Exception){
                Result.Error(e)
            }
        }

        return taskLocalDataSource.getTasks()
    }

    suspend fun refreshTasks(){
        updateTasksFromRemoteDataSource()
    }

    fun observeTask(): LiveData<Result<List<Task>>>{
        return taskLocalDataSource.observeTasks()
    }

    suspend fun refreshTask(taskId: String){
        updateTaskFromRemoteDataSource(taskId)
    }

    fun observeTask(taskId: String):LiveData<Result<Task>>{
        return taskLocalDataSource.observeTask(taskId)
    }

    suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task>{
        if (forceUpdate){
            updateTaskFromRemoteDataSource(taskId)
        }

        return taskLocalDataSource.getTask(taskId)
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        val remoteTask = taskRemoteDataSource.getTask(taskId)

        if (remoteTask is Result.Success){
            taskLocalDataSource.saveTask(remoteTask.data)
        }else if (remoteTask is Result.Error){
            throw remoteTask.exception
        }
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        val remoteTask = taskRemoteDataSource.getTasks()

        if(remoteTask is Result.Success){
            taskLocalDataSource.deleteAllTasks()
            remoteTask.data.forEach { task -> taskLocalDataSource.saveTask(task)}
        }else if (remoteTask is Result.Error){
            throw remoteTask.exception
        }
    }

    //Put and fetch same data to local and remote data source

    suspend fun saveTask(task: Task){
        coroutineScope {
            launch { taskRemoteDataSource.saveTask(task) }
            launch { taskLocalDataSource.saveTask(task) }
        }
    }

    suspend fun completeTask(task: Task){
        coroutineScope {
            launch { taskRemoteDataSource.completeTask(task) }
            launch { taskLocalDataSource.completeTask(task) }
        }
    }

    suspend fun completeTask(taskId: String){
        withContext(ioDispatcher){
            (getTaskWithId(taskId) as? Result.Success)?.let {
                completeTask(it.data)
            }
        }
    }

    suspend fun activeTask(task: Task){
        coroutineScope {
            launch { taskRemoteDataSource.activeTask(task) }
            launch { taskLocalDataSource.activeTask(task) }
        }
    }

    suspend fun activeTask(taskId: String){
        withContext(ioDispatcher){
            (getTaskWithId(taskId) as? Result.Success)?.data?.let{
                activeTask(it)
            }
        }
    }

    private suspend fun getTaskWithId(taskId: String): Result<Task> {
        return taskLocalDataSource.getTask(taskId)
    }

    suspend fun clearCompletedTasks(){
        coroutineScope {
            launch { taskRemoteDataSource.deleteTasksCompleted() }
            launch { taskLocalDataSource.deleteTasksCompleted() }
        }
    }

    suspend fun clearAllTasks(){
        withContext(ioDispatcher){
            coroutineScope {
                launch { taskRemoteDataSource.deleteAllTasks() }
                launch { taskLocalDataSource.deleteAllTasks() }
            }
        }
    }

    suspend fun deleteTask(taskId: String){
        coroutineScope {
            launch { taskRemoteDataSource.deleteTask(taskId) }
            launch { taskLocalDataSource.deleteTask(taskId) }
        }
    }

}