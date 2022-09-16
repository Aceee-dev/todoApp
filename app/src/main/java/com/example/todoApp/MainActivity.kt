package com.example.todoApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mTodoAdapter: TodoAdapter
    private lateinit var mTodoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTodoAdapter = TodoAdapter(mutableListOf())
        rvToDoItem.adapter = mTodoAdapter

        rvToDoItem.layoutManager = LinearLayoutManager(this)

        mTodoViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(TodoViewModel::class.java)

        btnAddTodo.setOnClickListener {
            val todoTitle = etToDoTitle.text.toString()
            if (todoTitle.isNotEmpty()) {
                val todo = Todo(title = todoTitle)
                mTodoViewModel.insert(todo)
                etToDoTitle.text.clear()
            }
        }
        btnDeleteTodo.setOnClickListener {
            val todoDeleteList = mTodoAdapter.getTodoDeleteList()
            for (todo in todoDeleteList) {
                mTodoViewModel.delete(todo)
            }
        }

        // put observer on live data
        mTodoViewModel.getAllTodos().observe(this, Observer { list ->
            list?.let {
                mTodoAdapter.updateList(it)
            }
        })
    }
}