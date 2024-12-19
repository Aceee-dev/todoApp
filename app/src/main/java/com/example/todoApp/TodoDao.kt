package com.example.todoApp

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun getAllTodo(): LiveData<MutableList<Todo>>

    @Query("SELECT * FROM todo WHERE todo.title MATCH :searchQuery")
    fun searchTodos(searchQuery: String): Flow<MutableList<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Update
    suspend fun update(todo: Todo)
}