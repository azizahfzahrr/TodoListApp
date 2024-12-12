package com.azizahfzahrr.todolistapp

import androidx.lifecycle.LiveData

class TodoRepository(private val todoDao: TodoDao) {

    val allTasks: LiveData<List<TodoModel>> = todoDao.getAllTasks()

    suspend fun insertTask(todo: TodoModel): Long {
        return todoDao.insertTask(todo)
    }

    suspend fun finishTask(id: Long) {
        todoDao.finishTask(id)
    }

    suspend fun deleteTask(id: Long) {
        todoDao.deleteTask(id)
    }

    fun searchTasks(query: String): LiveData<List<TodoModel>> {
        return todoDao.searchTasks("%$query%")
    }
}