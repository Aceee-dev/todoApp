package com.example.todoApp

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TodoAdapter(
    private val todos: MutableList<Todo>
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private lateinit var tododb: TodoDatabase

    fun setDB(db: TodoDatabase) {
        tododb = db
    }

    fun addToDo(todo: Todo) {
        todos.add(todo)
        GlobalScope.launch(Dispatchers.IO) { tododb.toDoDao().insert(todo) }
        notifyItemInserted(todos.size - 1)
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

    fun doneToDos(view: View) {
        val tidList: ArrayList<Todo> = arrayListOf()
        for (todo in todos) {
            if (todo.isChecked) {
                tidList.add(todo)
                GlobalScope.launch(Dispatchers.IO) {
                    tododb.toDoDao().delete(todo)
                }
            }
        }
        for (todo in tidList) {
            todos.remove(todo)
        }
        notifyDataSetChanged()
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_todo, parent, false
            )
        )
    }

    private fun toggleCompleteStatus(todoTextView: TextView, isChecked: Boolean) {
        if (isChecked) {
            todoTextView.paintFlags = todoTextView.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            todoTextView.paintFlags = todoTextView.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.itemView.apply {
            tvTodoTitle.text = curTodo.title
            cbTodoDone.isChecked = curTodo.isChecked
            toggleCompleteStatus(tvTodoTitle, curTodo.isChecked)
            cbTodoDone.setOnCheckedChangeListener { _, isChecked ->
                toggleCompleteStatus(tvTodoTitle, isChecked)
                curTodo.isChecked = !curTodo.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}