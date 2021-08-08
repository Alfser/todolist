package com.example.todolist.hometask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.todolist.R
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.datasource.model.Task
import com.example.todolist.utilities.Event

class TasksViewModel: ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>(TaskDataSource.getTaskList())

    val items: LiveData<List<Task>> get() = _tasks

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> get() = _snackBarText


    fun openTask(id:String){

    }

    fun completeTask(task: Task, isChecked: Boolean){

        if(isChecked){
            TaskDataSource.completeTask(task)
            showSnackBarMessage(R.string.task_completed)
        }else{
            TaskDataSource.activeTask(task)
            showSnackBarMessage(R.string.task_active)
        }
    }

    fun showSnackBarMessage(message: Int){
        _snackBarText.value = Event(message)
    }
}