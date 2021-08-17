package com.example.todolist.hometask

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.todolist.ADD_EDIT_RESULT_OK
import com.example.todolist.DELETE_RESULT_OK
import com.example.todolist.EDIT_RESULT_OK
import com.example.todolist.R
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.datasource.model.Task
import com.example.todolist.utilities.Event

class TasksViewModel: ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>(TaskDataSource.getTaskList())

    val items: LiveData<List<Task>> get() = _tasks

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> get() = _snackBarText

    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent get() = _openTaskEvent

    private val _addNewTaskEvent = MutableLiveData<Event<Unit>>()
    val addNewTaskEvent get() = _addNewTaskEvent

    private var resultMessageShow: Boolean = false

    fun openTask(taskId:String){
        _openTaskEvent.value = Event(taskId)
    }

    fun addNewTask(){
        _addNewTaskEvent.value = Event(Unit)
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

    //TODO: Setting filtering modes

    fun showEditResultMessage(result: Int){

        if(resultMessageShow) return

        when(result){
            EDIT_RESULT_OK -> showSnackBarMessage(R.string.task_updated)
            ADD_EDIT_RESULT_OK -> showSnackBarMessage(R.string.task_created)
            DELETE_RESULT_OK -> showSnackBarMessage(R.string.task_deleted)
        }

        resultMessageShow = true
    }

    private fun showSnackBarMessage(@StringRes message: Int){
        _snackBarText.value = Event(message)
    }
}