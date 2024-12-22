package com.example.todoApp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TodoRepository(private val context: Context) {
    private var mtodoDao: TodoDao

    init {
        val db = Room.databaseBuilder(context, TodoDatabase::class.java, "to_do").build()
        mtodoDao = db.toDoDao()
    }

    fun getAllTodos(): Flow<MutableList<Todo>> {
        return mtodoDao.getAllTodo()
    }

    fun searchTodos(query:String): Flow<MutableList<Todo>> {
        return mtodoDao.searchTodos(query)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun insert(todo: Todo) {
        GlobalScope.launch { mtodoDao.insert(todo.title,todo.isChecked,todo.description,todo.priority,todo.completion_date) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun delete(todo: Todo) {
        GlobalScope.launch { mtodoDao.delete(todo.rowid) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun update(oldtodo:Todo, todo: Todo) {
        GlobalScope.launch {
            mtodoDao.updateEntry(oldtodo, todo)
        }
    }
}