package com.example.todolist.datasource.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolist.datasource.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun observeTasks():LiveData<List<Task>>

    @Query("SELECT * FROM task")
    suspend fun getTasks():List<Task>

    @Query("SELECT * FROM task WHERE id = :taskId")
    fun observeTask(taskId:String): LiveData<Task>

    @Query("SELECT * FROM task WHERE id = :taskId")
    suspend fun getTask(taskId: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("UPDATE task SET completed = :completed WHERE id = :taskId")
    suspend fun completeTask(completed: Boolean, taskId: String)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM task")
    suspend fun deleteTasks()


    @Query("DELETE FROM task WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("DELETE FROM task WHERE completed = 1")
    suspend fun deleteCompletedTasks(): Int

}