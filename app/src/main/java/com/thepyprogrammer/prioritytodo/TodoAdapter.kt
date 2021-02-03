package com.thepyprogrammer.prioritytodo

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class TodoAdapter(
    private val todos: MutableList<Todo>
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    private val dTF: DateTimeFormatter = DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MMM-yyyy").toFormatter()

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_todo,
                parent,
                false
            )
        )
    }

    fun addTodo(todo: Todo) {
        todos.add(todo)
        notifyItemInserted(todos.size - 1)
        todos.sortWith { p1, p2 ->
            when {
                p1.dueDate > p2.dueDate -> 1
                p1.dueDate == p2.dueDate -> 0
                else -> -1
            }
        }
        notifyDataSetChanged()
    }

    private fun toggleStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean, dateView: Button) {
        if(isChecked) {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG

        } else {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
        dateView.isEnabled = !isChecked
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.itemView.apply {
            todoTitleView.setText(curTodo.title)
            cbDone.isChecked = curTodo.isChecked
            dateView.text = dTF.format(curTodo.dueDate)
            priorityBar.numStars = curTodo.priority
            toggleStrikeThrough(todoTitleView, curTodo.isChecked, dateView)
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(todoTitleView, isChecked, dateView)
                curTodo.isChecked = !curTodo.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}


















