package com.example.todoApp

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.*


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
        isChecked: Boolean
    ) {
        if (isChecked) {
            todoTextView.paintFlags = todoTextView.paintFlags or STRIKE_THRU_TEXT_FLAG
            tvTodoComplDate.paintFlags = tvTodoComplDate.paintFlags or STRIKE_THRU_TEXT_FLAG
            tvTodoPriority.paintFlags = tvTodoPriority.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            todoTextView.paintFlags = todoTextView.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            tvTodoComplDate.paintFlags = tvTodoComplDate.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            tvTodoPriority.paintFlags = tvTodoPriority.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.itemView.apply {
            tvTodoTitle.text = curTodo.title
            tvTodoComplDate.text = curTodo.completion_date
            tvTodoPriority.text = curTodo.priority.toString()
            cbTodoDone.isChecked = curTodo.isChecked
            toggleCompleteStatus(tvTodoTitle, tvTodoComplDate, tvTodoPriority, curTodo.isChecked)
            cbTodoDone.setOnCheckedChangeListener { _, isChecked ->
                toggleCompleteStatus(tvTodoTitle, tvTodoComplDate, tvTodoPriority, isChecked)
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
}