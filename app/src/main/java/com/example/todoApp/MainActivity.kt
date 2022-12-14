package com.example.todoApp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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

        //set toolbar as app bar with functionality
        setSupportActionBar(tbMenuAppBar)
        supportActionBar?.title = getString(R.string.Todo)

        mTodoAdapter = TodoAdapter(mutableListOf(), object : TodoAdapter.OnItemClickListener {
            override fun onItemClick(item: Todo?) {
                // send data to edit screen using bundle
                val intent = Intent(this@MainActivity, EditActivity::class.java)
                intent.putExtra("EDIT_DATA", item)
                startActivity(intent)
            }
        })
        rvToDoItem.adapter = mTodoAdapter

        rvToDoItem.layoutManager = LinearLayoutManager(this)

        mTodoViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(TodoViewModel::class.java)

        btnAddTodo.setOnClickListener {
            // Trigger edit activity
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            startActivity(intent)
        }

        btnDeleteTodo.setOnClickListener {
            val todoDeleteList = mTodoAdapter.getTodoDeleteList()
            for (todo in todoDeleteList) {
                mTodoViewModel.delete(todo)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateRecyclerViewWithData()
    }

    private fun updateRecyclerViewWithData() {
        // put observer on live data
        mTodoViewModel.getAllTodos().observe(this, Observer { list ->
            list?.let {
                mTodoAdapter.updateList(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the tool bar if it is present.
        menuInflater.inflate(R.menu.mainactivity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.search -> {
                true
            }
            R.id.about_info -> {
                // Show about window
                PopupWindowUtil.showPopWindow(window.decorView.rootView)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

}