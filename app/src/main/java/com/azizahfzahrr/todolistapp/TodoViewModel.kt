package com.azizahfzahrr.todolistapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    val allTasks: LiveData<List<TodoModel>> = repository.allTasks
    var searchedTasks: LiveData<List<TodoModel>> = repository.allTasks

    fun insertTask(todo: TodoModel) {
        viewModelScope.launch {
            repository.insertTask(todo)
        }
    }

    fun finishTask(id: Long) {
        viewModelScope.launch {
            repository.finishTask(id)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    fun searchTasks(query: String) {
        searchedTasks = if (query.isNotEmpty()) {
            repository.searchTasks(query)
        } else {
            repository.allTasks
        }
    }
}