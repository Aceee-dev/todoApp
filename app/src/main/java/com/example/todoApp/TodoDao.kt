package com.example.todoApp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT rowid, * FROM todo")
    fun getAllTodo(): Flow<MutableList<Todo>>

    @Query("SELECT rowid, * FROM todo WHERE todo.title MATCH :searchQuery || '*'")
    fun searchTodos(searchQuery: String): Flow<MutableList<Todo>>

    @Query("DELETE FROM todo WHERE rowid = :rowId")
    suspend fun delete(rowId: Long)

    @Query("INSERT INTO todo(title,isChecked,description,priority,completion_date) VALUES(:title, :ischecked, :desc, :pr, :date)")
    suspend fun insert(title:String, ischecked:Boolean,desc:String, pr:Int,date:String)

    @Transaction
    suspend fun updateEntry(oldTodo:Todo, todo:Todo) {
        val rowid = getRowIdForNote(oldTodo.title)
        delete(rowid)
        insert(todo.title,todo.isChecked,todo.description,todo.priority,todo.completion_date)
    }

    @Query("SELECT rowid FROM todo WHERE todo.title = :title LIMIT 1")
    suspend fun getRowIdForNote(title: String): Long
}