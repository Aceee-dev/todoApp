package com.example.todoApp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_edit.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class EditActivity : AppCompatActivity() {
    private lateinit var mTodoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val todoData: Todo = intent.getParcelableExtra("EDIT_DATA") ?: createTodoInstance("")

        mTodoViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )
            .get(TodoViewModel::class.java)

        val priorityArray = resources.getStringArray(R.array.Priority)
        val spinnerAdapter = object :
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, priorityArray) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView =
                    super.getDropDownView(position, convertView, parent) as TextView
                // Define colors for each priority level
                val backgroundColor = when (position) {
                    0 -> Color.LTGRAY // First item is a placeholder
                    1 -> ContextCompat.getColor(context, R.color.priority_color_high)
                    2 -> ContextCompat.getColor(context, R.color.priority_color_medium_high)
                    3 -> ContextCompat.getColor(context, R.color.priority_color_medium)
                    4 -> ContextCompat.getColor(context, R.color.priority_color_low)
                    5 -> ContextCompat.getColor(context, R.color.priority_color_very_low)
                    else -> Color.WHITE // Default background
                }

                // Set the background color
                view.setBackgroundColor(backgroundColor)

                // Set the text color (first item gray, others black)
                view.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)

                // Optional: Adjust padding to make it visually appealing
                view.setPadding(16, 16, 16, 16)
                return view
            }

        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriorityList.adapter = spinnerAdapter

        spinnerPriorityList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val value = parent!!.getItemAtPosition(position).toString()
                if (value == priorityArray[0]) {
                    (view as TextView).setTextColor(Color.GRAY)
                }
            }

        }

        if (todoData.title.isNotEmpty()) {
            editTextTodoTitle.setText(todoData.title)
            editTextDescription.setText(todoData.description)
            editTextDateTodo.setText(todoData.completion_date)
            spinnerPriorityList.setSelection(todoData.priority)
        } else {
            val calendar = Calendar.getInstance()
            val currentDate =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            editTextDateTodo.setText(currentDate);
        }

        btnSaveTodo.setOnClickListener {
            val title = editTextTodoTitle.text.toString()
            if (title.isNotEmpty()) {
                if (todoData.title.isEmpty()) {
                    mTodoViewModel.insert(createTodoInstance(title))
                } else {
                    // call update
                    mTodoViewModel.update(todoData,createTodoInstance(title))
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            } else {
                Snackbar.make(
                    this.findViewById(android.R.id.content),
                    "Title can not be empty!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        editTextDateTodo.setOnClickListener {
            // Get the current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Open DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Handle selected date
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    editTextDateTodo.setText(selectedDate);
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun createTodoInstance(title: String): Todo {
        val todo = Todo(title = title)
        todo.description = editTextDescription.text.toString()
        todo.priority = spinnerPriorityList.selectedItemPosition
        todo.completion_date = editTextDateTodo.text.toString()
        return todo
    }
}