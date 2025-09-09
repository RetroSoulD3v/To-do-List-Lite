package com.retrosouldev.todolite

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.retrosouldev.todolite.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var adapter: TodoAdapter
    private val todos = mutableListOf<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Load saved todos
        todos.addAll(loadTodos())

        adapter = TodoAdapter(
            items = todos,
            onChanged = { saveTodos(todos) },
            onLongPressDelete = { item, pos ->
                adapter.removeAt(pos)
                saveTodos(todos)
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            }
        )

        b.rvTodos.layoutManager = LinearLayoutManager(this)
        b.rvTodos.adapter = adapter

        b.btnAdd.setOnClickListener {
            val text = b.edtTask.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter a task", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val t = Todo(title = text)
            adapter.insertTop(t)
            b.edtTask.setText("")
            saveTodos(todos)
        }
    }

    // ---- Persistence with SharedPreferences (JSON) ----

    private fun prefs() = getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

    private fun saveTodos(list: List<Todo>) {
        val arr = JSONArray()
        list.forEach {
            val o = JSONObject()
            o.put("id", it.id)
            o.put("title", it.title)
            o.put("done", it.done)
            arr.put(o)
        }
        prefs().edit().putString("todos_json", arr.toString()).apply()
    }

    private fun loadTodos(): List<Todo> {
        val json = prefs().getString("todos_json", null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        Todo(
                            id = o.getLong("id"),
                            title = o.getString("title"),
                            done = o.getBoolean("done")
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
