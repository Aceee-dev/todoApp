package com.example.todoApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mTodoAdapter: TodoAdapter
    private lateinit var mMutableListOfToDos: MutableList<Todo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMutableListOfToDos =
            if (savedInstanceState != null) savedInstanceState.getParcelableArrayList("todolist")!!
            else mutableListOf()
        setContentView(R.layout.activity_main)
        mTodoAdapter = TodoAdapter(mMutableListOfToDos)

        rvToDoItem.adapter = mTodoAdapter
        rvToDoItem.layoutManager = LinearLayoutManager(this)

        btnAddTodo.setOnClickListener {
            val todoTitle = etToDoTitle.text.toString()
            if (todoTitle.isNotEmpty()) {
                val todo = Todo(todoTitle)
                mTodoAdapter.addToDo(todo)
                etToDoTitle.text.clear()
            }
        }
        btnDeleteTodo.setOnClickListener {
            mTodoAdapter.doneToDos()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("todolist", ArrayList(mMutableListOfToDos));
    }
}