package com.example.todolist.datasource

import android.os.Build
import android.os.Debug
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.todolist.datasource.model.Task

object TaskDataSource {

    private val taskList = arrayListOf<Task>(
        Task(title = "Reunião do projeto vegas",
            description = "falar sobre os vanços no projeto e as mudanças",
            date = "06/05/2021",
            timer = "19:00"
        ),
        Task(title = "Manutenção App Ford",
            description = "Realizar a manutenção no App forde",
            date = "03/04/2021",
            timer = "18:00"
        ),
        Task(title = "Documentação da API arco",
            description = "Realizar a manutenção da Api do prjeto Arco",
            date = "02/06/2021",
            timer = "15:00"
        )
    )

    fun getTaskList() = taskList

    fun insertNewTask(task: Task){
        taskList.add(task)
        Log.i("TaskDataSource", task.toString())
    }

    fun getTask(id: String) = taskList.find { task -> task.id == id }

    fun removeTask(task: Task) = taskList.remove(task)

    @RequiresApi(Build.VERSION_CODES.N)
    fun removeTask(id: String) = taskList.removeIf{ task -> task.id == id }

    fun completeTask(task: Task){

    }

    fun activeTask(task: Task){

    }
}