package com.example.todoApp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TodoRepository(private val context: Context) {
    private var mtodoDao: TodoDao
    private var mAllTodos: LiveData<MutableList<Todo>>

    init {
        val db = Room.databaseBuilder(context, TodoDatabase::class.java, "to_do").build()
        mtodoDao = db.toDoDao()
        mAllTodos = mtodoDao.getAllTodo()
    }

    fun getAllTodos(): LiveData<MutableList<Todo>> {
        return mAllTodos
    }

    fun insert(todo: Todo) {
        GlobalScope.launch { mtodoDao.insert(todo) }
    }

    fun delete(todo: Todo) {
        GlobalScope.launch { mtodoDao.delete(todo) }
    }
}