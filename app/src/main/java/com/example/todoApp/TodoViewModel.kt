package com.example.todoApp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val mtodoRepository: TodoRepository
    private val _searchResults = MutableStateFlow<MutableList<Todo>>(mutableListOf())
    val searchResults: StateFlow<MutableList<Todo>> = _searchResults
    private val _allResults = MutableStateFlow<MutableList<Todo>>(mutableListOf())
    val allResults: StateFlow<MutableList<Todo>> = _allResults

    init {
        mtodoRepository = TodoRepository(application)
    }

    fun insert(todo: Todo) {
        mtodoRepository.insert(todo)
    }

    fun delete(todo: Todo) {
        mtodoRepository.delete(todo)
    }

    fun update(oldtodo:Todo, todo: Todo) {
        mtodoRepository.update(oldtodo, todo)
    }

    fun getAllTodos() {
        viewModelScope.launch {
            mtodoRepository.getAllTodos().collect { todos ->
                _allResults.value = todos
            }
        }
    }

    fun searchTodos(query:String) {
       viewModelScope.launch {
            mtodoRepository.searchTodos(query).collect { todos ->
                _searchResults.value = todos
            }
        }
    }
}