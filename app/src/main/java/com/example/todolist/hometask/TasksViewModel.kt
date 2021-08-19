package com.example.todolist.hometask

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.example.todolist.ADD_EDIT_RESULT_OK
import com.example.todolist.DELETE_RESULT_OK
import com.example.todolist.EDIT_RESULT_OK
import com.example.todolist.R
import com.example.todolist.datasource.TasksRepository
import com.example.todolist.datasource.model.Task
import com.example.todolist.utilities.Event
import kotlinx.coroutines.launch
import com.example.todolist.datasource.Result

class TasksViewModel(app: Application): AndroidViewModel(app) {

    private val tasksRepository = TasksRepository.getRepository(app)

    private val _forceUpdate = MutableLiveData(false)
    private val _tasks = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate){
            _dataLoading.value = true

            viewModelScope.launch {
                tasksRepository.refreshTasks()
                _dataLoading.value = false
            }
        }
        tasksRepository.observeTask().switchMap { filterTasks(it) }
    }

    val items: LiveData<List<Task>> get() = _tasks

    private val _dataLoading = MutableLiveData(false)
    val dataLoading: LiveData<Boolean> get() = _dataLoading

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> get() = _currentFilteringLabel

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> get() = _snackBarText

    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent: LiveData<Event<String>> get() = _openTaskEvent

    private val _addNewTaskEvent = MutableLiveData<Event<Unit>>()
    val addNewTaskEvent:LiveData<Event<Unit>> get() = _addNewTaskEvent

    private var currentFiltering = TaskFilterType.ALL_TASKS

    private var resultMessageShow: Boolean = false

    val listOfItemsIsEmpty: LiveData<Boolean> = Transformations.map(_tasks){
        it?.isEmpty()
    }

    init {
        //initial state
        loadTasks(true)
    }

    fun openTask(taskId:String){
        _openTaskEvent.value = Event(taskId)
    }

    fun addNewTask(){
        _addNewTaskEvent.value = Event(Unit)
    }

    fun completeTask(task: Task, isChecked: Boolean) = viewModelScope.launch{
        if(isChecked){
            tasksRepository.completeTask(task)
            showSnackBarMessage(R.string.task_completed)
        }else{
            tasksRepository.activeTask(task)
            showSnackBarMessage(R.string.task_active)
        }
    }


    private fun filterTasks(resultTasks: Result<List<Task>>):LiveData<List<Task>>{
        val result = MutableLiveData<List<Task>>()

        if (resultTasks is Result.Success){
            viewModelScope.launch {
                result.value = filterItems(resultTasks.data, currentFiltering)
            }
        }else{
            result.value = emptyList()
            showSnackBarMessage(R.string.error_obtain_tasks)
        }

        return result
    }

    private fun filterItems(tasks:List<Task>, filterType: TaskFilterType):List<Task>{
        val filteredTasks = arrayListOf<Task>()


        when(filterType){
            TaskFilterType.ALL_TASKS -> filteredTasks.addAll(tasks)

            TaskFilterType.ACTIVE_TASKS -> tasks.forEach{ task ->
                if (task.isActive()){
                    filteredTasks.add(task)
                }
            }

            TaskFilterType.COMPLETED_TASKS -> tasks.forEach{ task ->
                if (task.isComplete()){
                    filteredTasks.add(task)
                }
            }
        }

        return filteredTasks
    }

    //extension function to verify task behavior
    private fun Task.isComplete() = this.completed

    private fun Task.isActive() = !this.completed


    private fun loadTasks(forceUpdate: Boolean){
        _forceUpdate.value = forceUpdate
    }

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