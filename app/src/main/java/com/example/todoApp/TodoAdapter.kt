package com.example.todoApp

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date


class TodoAdapter(
    private val todos: MutableList<Todo>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: Todo?)
    }

    // below method is use to update our list of notes.
    fun updateList(newList: MutableList<Todo>) {
        // on below line we are clearing
        // our notes array list
        todos.clear()
        // on below line we are adding a
        // new list to our all notes list.
        todos.addAll(newList)
        // on below line we are calling notify data
        // change method to notify our adapter.
        notifyDataSetChanged()
    }

    fun getTodoDeleteList(): ArrayList<Todo> {
        val list: ArrayList<Todo> = arrayListOf()
        for (todo in todos) {
            if (todo.isChecked) {
                list.add(todo)
            }
        }
        return list
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_todo, parent, false
            )
        )
    }

    private fun toggleCompleteStatus(
        todoTextView: TextView,
        tvTodoComplDate: TextView,
        tvTodoPriority: TextView,
        tvTodoDescription: TextView,
        isChecked: Boolean
    ) {
        if (isChecked) {
            todoTextView.paintFlags = todoTextView.paintFlags or STRIKE_THRU_TEXT_FLAG
            tvTodoDescription.paintFlags  = tvTodoDescription.paintFlags or STRIKE_THRU_TEXT_FLAG
            tvTodoComplDate.paintFlags = tvTodoComplDate.paintFlags or STRIKE_THRU_TEXT_FLAG
            tvTodoPriority.paintFlags = tvTodoPriority.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            todoTextView.paintFlags = todoTextView.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            tvTodoComplDate.paintFlags = tvTodoComplDate.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            tvTodoPriority.paintFlags = tvTodoPriority.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            tvTodoDescription.paintFlags  = tvTodoDescription.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.itemView.apply {
            tvTodoTitle.text = curTodo.title
            tvTodoComplDate.text = curTodo.completion_date
            tvTodoPriority.text = curTodo.priority.toString()
            cbTodoDone.isChecked = curTodo.isChecked
            tvTodoDescription.text = curTodo.description
            toggleCompleteStatus(tvTodoTitle, tvTodoComplDate, tvTodoPriority, tvTodoDescription, curTodo.isChecked)
            cbTodoDone.setOnCheckedChangeListener { _, isChecked ->
                toggleCompleteStatus(tvTodoTitle, tvTodoComplDate, tvTodoPriority,tvTodoDescription, isChecked)
                curTodo.isChecked = !curTodo.isChecked
            }
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(curTodo)
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    fun sortByPriority() {
        todos.sortBy { it.priority }
        notifyDataSetChanged()
    }

    fun sortByDate() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            todos.sortBy { getDate(it.completion_date) }
            notifyDataSetChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(text: String): Date {
        val df = SimpleDateFormat("dd/MM/yyyy")
        val localDate = LocalDate.now()
        var myDate: Date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        try {
            myDate = df.parse(text)
            return myDate
        } catch (e: ParseException) {
            Log.e("TodoAdapter", "Parse Exception while parsing text")
        }
        return myDate
    }
}