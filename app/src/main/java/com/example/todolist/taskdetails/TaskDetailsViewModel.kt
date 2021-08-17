package com.example.todolist.taskdetails

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.example.todolist.R
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.datasource.model.Task
import com.example.todolist.utilities.Event

class TaskDetailsViewModel : ViewModel() {

    private val _taskId = MutableLiveData<String>()

    private val _task = _taskId.switchMap {
        val task = TaskDataSource.getTask(it as String)
        MutableLiveData<Task?>(task)
    }
    val task get() = _task

    //When is loading the task
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading get() = _dataLoading

    val isDataAvailable = task.map { it != null }
    //verify this task is completed
    val completed : LiveData<Boolean> = _task.map { task -> task?.completed ?: false }

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> get() = _snackBarText

    //navigation event
    private val _deleteEvent = MutableLiveData<Event<Unit>>()
    val deleteTaskEvent get() = _deleteEvent

    private val _editTaskEvent = MutableLiveData<Event<Unit>>()
    val editTaskEvent get() = _editTaskEvent

    fun setCompleted(completed: Boolean){
        _task.value?.let {
            if(completed){
                showSnackBarMessage( R.string.task_completed)
                TaskDataSource.completeTask(it)
            }else{
                showSnackBarMessage(R.string.task_active)
                TaskDataSource.activeTask(it)
            }
        }
    }

    fun start(taskId: String){
        if (_dataLoading.value == true && taskId == _taskId.value){
            return
        }
        //Trigger loading
        _taskId.value = taskId
    }

    fun refresh(){
        _task.value?.let {
            _dataLoading.value = true
            TaskDataSource.refreshTask(it.id)
            _dataLoading.value = false
        }
    }

    fun deleteTask(){
        task.value?.let {
            _dataLoading.value = true
            TaskDataSource.deleteTask(it)
            _deleteEvent.value = Event(Unit)
            _dataLoading.value = false
        }
    }

    fun editTask(){
        _editTaskEvent.value = Event(Unit)
    }

    private fun showSnackBarMessage(@StringRes message: Int) {
        _snackBarText.value = Event(message)
    }

}