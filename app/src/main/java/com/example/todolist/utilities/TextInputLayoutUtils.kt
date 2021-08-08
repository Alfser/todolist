package com.example.todolist.utilities

import com.google.android.material.textfield.TextInputLayout

var TextInputLayout.text
    get() = this.editText?.text.toString()
    set(value) {
        this.editText?.setText(value)
    }