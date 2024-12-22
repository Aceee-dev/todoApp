package com.example.todoApp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.util.query
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var mTodoAdapter: TodoAdapter
    private lateinit var mTodoViewModel: TodoViewModel
    private lateinit var searchContainer: LinearLayout
    private lateinit var searchEditText: EditText
    private lateinit var searchCloseButton: ImageView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set toolbar as app bar with functionality
        setSupportActionBar(tbMenuAppBar)
        supportActionBar?.title = getString(R.string.Todo)

        searchContainer = findViewById(R.id.search_container)
        searchEditText = findViewById(R.id.search_edit_text)
        searchCloseButton = findViewById(R.id.search_close_button)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)

        swipeRefreshLayout.setOnRefreshListener {
            // Your code to refresh data
            refreshData()
        }

        // Close search mode on close button click
        searchCloseButton.setOnClickListener {
            exitSearchMode()
        }

        // Debounce mechanism
        // Create a flow to listen for text changes in the EditText
        val searchFlow = searchEditText.textChangesFlow()
            .debounce(500) // Wait for 500ms after the last keystroke (debounce)
            .distinctUntilChanged() // Only emit if the text is different from the last emitted text

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

        lifecycleScope.launch { searchFlow.collect {
                query -> mTodoViewModel.searchTodos(query)
            }
        }

        lifecycleScope.launch { mTodoViewModel.searchResults.collect{
                result ->
                if(result.isEmpty()) {
                    queryAllTodoList()
                    return@collect
                }
                mTodoAdapter.updateList(result)
        } }


        lifecycleScope.launch { mTodoViewModel.allResults.collect{
                result ->
                mTodoAdapter.updateList(result)
                swipeRefreshLayout.isRefreshing = false
        } }
    }

    fun queryAllTodoList() {
        mTodoViewModel.getAllTodos()
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
        queryAllTodoList()
    }

    // Extension function to create a Flow of text changes from EditText
    private fun EditText.textChangesFlow(): Flow<String> = callbackFlow {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        addTextChangedListener(textWatcher)
        awaitClose { removeTextChangedListener(textWatcher) }
    }

    override fun onResume() {
        super.onResume()
        queryAllTodoList()
    }


    private fun refreshData() {
        swipeRefreshLayout.postDelayed({
            // Update your data here
           queryAllTodoList()
            swipeRefreshLayout.isRefreshing = false
        }, 2000)
    }
}