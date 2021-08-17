package com.example.todolist.addedittask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.R
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.datasource.model.Task
import com.example.todolist.utilities.Event

class AddEditTaskViewModel: ViewModel() {

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    private var completed = false
    private var favorited = false
    private var archived = false
    private var taskId: String = ""

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _addEditTaskEvent = MutableLiveData<Event<Unit>>()
    val addEditTaskEvent: LiveData<Event<Unit>> get() = _addEditTaskEvent

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> get() = _snackBarText

    private var isNewTask = false

    private var isDataLoaded = false

    fun start(taskId: String?){
        val isLoading = _loading.value ?: false

        if (isLoading || isDataLoaded)
            return

        if(taskId == null){
            isNewTask = true
            return
        }

        this.taskId = taskId

        isNewTask = false
        _loading.value = true

        TaskDataSource.getTask(taskId)?.let {
            onTaskLoaded(it)
        }

    }

    private fun onTaskLoaded(task: Task) {
        title.value = task.title
        description.value = task.description
        date.value = task.date
        time.value = task.time
        completed = task.completed
        favorited = task.favorite
        archived = task.archived

        _loading.value = false
        isDataLoaded = true
    }

    private fun isThisTaskContentsNull(
        title:String?,
        description: String?,
        date: String?,
        time: String?
    ): Boolean{


        val isTitleNull = title.isNullOrBlank()
        val isDescriptionNull = description.isNullOrBlank()
        val isDateNull = date.isNullOrBlank()
        val isTimeNull = time.isNullOrBlank()

        return isTitleNull || isDescriptionNull || isTimeNull || isDateNull
    }

    fun saveTask(){
        val title = title.value ?: ""
        val description = description.value ?: ""
        val date = date.value ?: ""
        val time = time.value ?: ""

        if(isThisTaskContentsNull(title, description, date, time)){
            _snackBarText.value = Event(R.string.task_content_not_void)
            return
        }

        _loading.value = true
        if(isNewTask){
            val newTask = Task(
                title = title,
                description = description,
                date = date,
                time = time
            )
            addNewTask(newTask)
        }
        else{

            if (isNewTask) {
                throw RuntimeException("updateTask() foi chamado mas a tarefa é nova.")
            }

            val updatedTask = Task(
                title = title,
                description = description,
                date = date,
                time = time,
                favorite = favorited,
                archived = archived,
                id = taskId
            )

            updateTask(updatedTask)
        }
    }

    private fun addNewTask(task: Task){
        TaskDataSource.insertNewTask(task)
        _addEditTaskEvent.value = Event(Unit)
        _loading.value = false
    }

    private fun updateTask(task: Task){
        TaskDataSource.updateTask(task)
        _addEditTaskEvent.value = Event(Unit)
        _loading.value = false
    }

    private fun onDataNotAvailable(){
        _loading.value = false
    }
}