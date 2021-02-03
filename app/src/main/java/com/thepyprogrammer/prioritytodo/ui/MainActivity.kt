package com.thepyprogrammer.prioritytodo.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thepyprogrammer.prioritytodo.R
import com.thepyprogrammer.prioritytodo.model.Todo
import com.thepyprogrammer.prioritytodo.model.TodoAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.PrintWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private var dueDateSelected: LocalDate = LocalDate.now()

    companion object {
        val dTF: DateTimeFormatter = DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd/MM/yyyy").toFormatter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todoAdapter = TodoAdapter(this, readFile())

        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)

        priorityBar.rating = 2.5F

        dateSelector.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this,
                    { _, year, monthOfYear, dayOfMonth ->
                        dueDateSelected = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                        dateSelector.text = dTF.format(dueDateSelected)
                    }, dueDateSelected.year, dueDateSelected.monthValue - 1, dueDateSelected.dayOfMonth)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        btnAddTodo.setOnClickListener {
            val todoTitle = todoTitleText.text.toString()
            val priority: Float = priorityBar.rating

            if (todoTitle.isNotEmpty()) {
                val todo = Todo(todoTitle, priority, dueDateSelected)
                todoAdapter.addTodo(todo)
                todoTitleText.text.clear()
            }
        }
    }

    fun readFile(): MutableList<Todo> {
        val dbFile = File(filesDir.path.toString() + "/todos.txt")
        if (!dbFile.exists()) dbFile.createNewFile()
        val sc = Scanner(dbFile)
        val list = mutableListOf<Todo>()
        while (sc.hasNext()) {
            val s = sc.nextLine().split(" ")
            list.add(Todo(s[0], s[1].toFloat(), dTF.parse(s[2]) as LocalDate, s[3].toBoolean()))
        }
        sc.close()
        return list
    }

    fun updateFile() {
        val dbFile = File(filesDir.path.toString() + "/todos.txt")
        if (!dbFile.exists()) dbFile.createNewFile()
        val pw = PrintWriter(dbFile)
        todoAdapter.todos.forEach {
            pw.println(it)
        }
        pw.close()
    }

    override fun onDestroy() {
        updateFile()
        super.onDestroy()
    }
}