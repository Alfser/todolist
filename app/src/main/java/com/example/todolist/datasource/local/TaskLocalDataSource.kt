package com.example.todolist.datasource.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.todolist.datasource.Result
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.datasource.model.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Error
import java.lang.Exception

class TaskLocalDataSource internal constructor(
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TaskDataSource {

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return taskDao.observeTasks().map {
            Result.Success(it)
        }
    }

    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher){
        return@withContext try {
            Result.Success(taskDao.getTasks())
        }catch (e: Exception){
            Result.Error(e)
        }
    }

    override suspend fun refreshTasks() {
        //NOP
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return taskDao.observeTask(taskId).map {
            Result.Success(it)
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> = withContext(ioDispatcher){

        try {
            val task = taskDao.getTask(taskId)
            if (task != null){
                return@withContext Result.Success(task)
            }else{
                return@withContext Result.Error(Exception("Tarefa não encontrada"))
            }
        }
        catch (e: Exception){
            Result.Error(e)
        }
    }

    override suspend fun refreshTask(taskId: String) {
        //NOP
    }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher){
        taskDao.insertTask(task)
    }



    override suspend fun completeTask(task: Task) = withContext(ioDispatcher){
        taskDao.completeTask(true, task.id)
    }

    override suspend fun completeTask(taskId: String) = withContext(ioDispatcher){
        taskDao.completeTask(true, taskId)
    }

    override suspend fun activeTask(task: Task) {
        taskDao.completeTask(false, task.id)
    }

    override suspend fun activeTask(taskId: String) {
        taskDao.completeTask(false, taskId)
    }

    override suspend fun deleteAllTasks() = withContext(ioDispatcher){
        taskDao.deleteTasks()
    }

    override suspend fun deleteTask(taskId: String) = withContext(ioDispatcher){
        taskDao.deleteTask(taskId)
    }

    override suspend fun deleteTasksCompleted() = withContext<Unit>(ioDispatcher){
        taskDao.deleteCompletedTasks()
    }
}