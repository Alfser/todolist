package com.example.todolist.taskdetails

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.example.todolist.R
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.datasource.TasksRepository
import com.example.todolist.datasource.model.Task
import com.example.todolist.utilities.Event
import com.example.todolist.datasource.Result
import kotlinx.coroutines.launch

class TaskDetailsViewModel(app: Application) : AndroidViewModel(app) {

    private val tasksRepository = TasksRepository.getRepository(app)
    private val _taskId = MutableLiveData<String>()

    private val _task = _taskId.switchMap { taskId ->
        tasksRepository.observeTask(taskId).map { computeResult(it) }
    }
    val task: LiveData<Task?> get() = _task

    val isDataAvailable: LiveData<Boolean> = _task.map { it != null }

    //When is loading the task
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading get() = _dataLoading

    //verify this task is completed
    val completed : LiveData<Boolean> = _task.map { task -> task?.completed ?: false }

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> get() = _snackBarText

    //navigation event
    private val _deleteEvent = MutableLiveData<Event<Unit>>()
    val deleteTaskEvent get() = _deleteEvent

    private val _editTaskEvent = MutableLiveData<Event<Unit>>()
    val editTaskEvent get() = _editTaskEvent

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        _task.value?.let {
            if(completed){
                tasksRepository.completeTask(it)
                showSnackBarMessage( R.string.task_completed)
            }else{
                tasksRepository.activeTask(it)
                showSnackBarMessage(R.string.task_active)
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
            viewModelScope.launch {
                tasksRepository.refreshTask(it.id)
                _dataLoading.value = false
            }
        }
    }

    fun deleteTask(){
        task.value?.let {
            _dataLoading.value = true
            viewModelScope.launch {
                tasksRepository.deleteTask(it.id)
                _dataLoading.value = false
                _deleteEvent.value = Event(Unit)
            }
        }
    }

    fun editTask(){
        _editTaskEvent.value = Event(Unit)
    }

    private fun computeResult(taskResult: Result<Task>): Task?{
        return if(taskResult is Result.Success){
            taskResult.data
        }else{
            showSnackBarMessage(R.string.error_loading_task)
            null
        }
    }

    private fun showSnackBarMessage(@StringRes message: Int) {
        _snackBarText.value = Event(message)
    }

}