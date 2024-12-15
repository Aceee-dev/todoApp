package com.example.todoApp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var mTodoAdapter: TodoAdapter
    private lateinit var mTodoViewModel: TodoViewModel
    private lateinit var searchContainer: LinearLayout
    private lateinit var searchEditText: EditText
    private lateinit var searchCloseButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set toolbar as app bar with functionality
        setSupportActionBar(tbMenuAppBar)
        supportActionBar?.title = getString(R.string.Todo)

        searchContainer = findViewById(R.id.search_container)
        searchEditText = findViewById(R.id.search_edit_text)
        searchCloseButton = findViewById(R.id.search_close_button)

        // Close search mode on close button click
        searchCloseButton.setOnClickListener {
            exitSearchMode()
        }

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
        mTodoViewModel.getAllTodos().observe(this) { list ->
            list?.let {
                mTodoAdapter.updateList(it)
            }
        }
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
                enterSearchMode()
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

    private fun enterSearchMode() {
        // Show the search container with an animation
        searchContainer.visibility = LinearLayout.VISIBLE
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 300
            fillAfter = true
        }
        searchContainer.startAnimation(fadeIn)

        // Hide toolbar title and menu items
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun exitSearchMode() {
        // Hide the search container with an animation
        val fadeOut = AlphaAnimation(1f, 0f).apply {
            duration = 300
            fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    searchContainer.visibility = LinearLayout.GONE
                    supportActionBar?.setDisplayShowTitleEnabled(true)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        searchContainer.startAnimation(fadeOut)
    }

}