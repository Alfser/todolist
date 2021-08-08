package com.example.todolist.datasource.model

import java.util.*

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val date: String,
    val timer: String,
    val completed: Boolean = false,
    val archive: Boolean = false,
    val favorite: Boolean = false
)