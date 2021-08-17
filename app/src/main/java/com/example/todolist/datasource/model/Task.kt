package com.example.todolist.datasource.model

import androidx.room.Entity
import java.util.*

@Entity(tableName = "task")
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val completed: Boolean = false,
    val archived: Boolean = false,
    val favorite: Boolean = false
)