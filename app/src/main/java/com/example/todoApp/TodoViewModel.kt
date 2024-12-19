package com.example.todoApp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val mtodoRepository: TodoRepository
    private val mAllTodos: LiveData<MutableList<Todo>>
    private val _searchResults = MutableStateFlow<MutableList<Todo>>(mutableListOf())
    val searchResults: StateFlow<MutableList<Todo>> = _searchResults

    init {
        mtodoRepository = TodoRepository(application)
        mAllTodos = mtodoRepository.getAllTodos()
    }

    fun insert(todo: Todo) {
        mtodoRepository.insert(todo)
    }

    fun delete(todo: Todo) {
        mtodoRepository.delete(todo)
    }

    fun update(todo: Todo) {
        mtodoRepository.update(todo)
    }

    fun getAllTodos(): LiveData<MutableList<Todo>> {
        return mAllTodos;
    }

    fun searchTodos(query:String) {
       viewModelScope.launch {
            mtodoRepository.searchTodos(query).collect { todos ->
                _searchResults.value = todos
            }
        }
    }
}