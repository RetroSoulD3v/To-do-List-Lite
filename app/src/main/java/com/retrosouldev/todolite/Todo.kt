package com.retrosouldev.todolite

data class Todo(
    val id: Long = System.currentTimeMillis(),
    var title: String,
    var done: Boolean = false
)
