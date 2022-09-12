package com.example.todoApp

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var mTodoAdapter: TodoAdapter
    private lateinit var mMutableListOfToDos: MutableList<Todo>
    private lateinit var db: TodoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(applicationContext, TodoDatabase::class.java, "tododb").build()
        mMutableListOfToDos = mutableListOf()
        setContentView(R.layout.activity_main)
        mTodoAdapter = TodoAdapter(mMutableListOfToDos)
        mTodoAdapter.setDB(db)

        rvToDoItem.adapter = mTodoAdapter
        rvToDoItem.layoutManager = LinearLayoutManager(this)

        btnAddTodo.setOnClickListener {
            val todoTitle = etToDoTitle.text.toString()
            if (todoTitle.isNotEmpty()) {
                val todo = Todo(title = todoTitle)
                mTodoAdapter.addToDo(todo)
                etToDoTitle.text.clear()
            }
        }
        btnDeleteTodo.setOnClickListener {
            mTodoAdapter.doneToDos(window.decorView.rootView)
        }

        //load async from db and then show using adapter
        GlobalScope.launch(Dispatchers.IO) {
            mMutableListOfToDos = db.toDoDao().getAllTodo()
            withContext(Dispatchers.Main) {
                for (todo in mMutableListOfToDos) {
                    Log.i("Anukul", todo.title)
                }
                mTodoAdapter.updateList(mMutableListOfToDos)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        //load async from db and then show using adapter
        GlobalScope.launch(Dispatchers.IO) {
            mMutableListOfToDos = db.toDoDao().getAllTodo()
            withContext(Dispatchers.Main) {
                for (todo in mMutableListOfToDos) {
                    Log.i("Anukul", todo.title)
                }
                mTodoAdapter.updateList(mMutableListOfToDos)
            }
        }

        super.onConfigurationChanged(newConfig)
    }
}