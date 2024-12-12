package com.azizahfzahrr.todolistapp

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azizahfzahrr.todolistapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), TodoAdapter.TodoClickListener {

    private lateinit var binding: ActivityMainBinding
    private val todoRepository: TodoRepository by lazy {
        TodoRepository(TodoDatabase.getDatabase(application).todoDao())
    }
    private val todoViewModel: TodoViewModel by viewModels {
        TodoViewModelFactory(todoRepository)
    }
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        todoAdapter = TodoAdapter(this, this)
        binding.todoRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = todoAdapter
        }
        todoViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { todoAdapter.submitList(it) }
        })
        initSwipe()
        binding.fab.setOnClickListener {
            openNewTask(it)
        }
        setupSearchBar()
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = todoAdapter.currentList[position]

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        showDeleteConfirmationDialog(task, position, viewHolder)
                    }
                    ItemTouchHelper.RIGHT -> {
                        lifecycleScope.launch {
                            todoViewModel.finishTask(task.id)
                            todoAdapter.notifyItemChanged(position)
                        }
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val paint = Paint()

                    if (dX < 0) {
                        val deleteIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.baseline_check_24)
                        if (deleteIcon != null) {
                            val iconIntrinsicWidth = deleteIcon.intrinsicWidth
                            val iconIntrinsicHeight = deleteIcon.intrinsicHeight
                            val iconTop = itemView.top + (itemView.height - iconIntrinsicHeight) / 2
                            val iconMargin = 16
                            val iconLeft = itemView.right - iconMargin - iconIntrinsicWidth
                            val iconRight = itemView.right - iconMargin
                            val iconBottom = iconTop + iconIntrinsicHeight

                            paint.color = Color.parseColor("#388E3C")
                            c.drawRect(
                                itemView.right.toFloat() + dX, itemView.top.toFloat(),
                                itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                            )

                            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                            deleteIcon.draw(c)
                        }
                    } else {
                        val checkIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.baseline_delete_24)
                        if (checkIcon != null) {
                            val iconIntrinsicWidth = checkIcon.intrinsicWidth
                            val iconIntrinsicHeight = checkIcon.intrinsicHeight
                            val iconTop = itemView.top + (itemView.height - iconIntrinsicHeight) / 2
                            val iconMargin = 16
                            val iconLeft = itemView.left + iconMargin
                            val iconRight = itemView.left + iconMargin + iconIntrinsicWidth
                            val iconBottom = iconTop + iconIntrinsicHeight

                            paint.color = Color.parseColor("#D32F2F")
                            c.drawRect(
                                itemView.left.toFloat(), itemView.top.toFloat(),
                                itemView.left.toFloat() + dX, itemView.bottom.toFloat(), paint
                            )

                            checkIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                            checkIcon.draw(c)
                        }
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.todoRv)
    }

    private fun showDeleteConfirmationDialog(task: TodoModel, position: Int, viewHolder: RecyclerView.ViewHolder) {
        androidx.appcompat.app.AlertDialog.Builder(this).apply {
            setTitle("Delete Task")
            setMessage("Are you sure you want to delete this task?")
            setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    todoViewModel.deleteTask(task.id)
                }
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                // Reset state item ketika "Cancel" dipilih
                todoAdapter.notifyItemChanged(position)
            }
        }.show()
    }

    private fun setupSearchBar() {
        binding.svSearchTodo.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    todoViewModel.searchTasks(it)
                    observeSearchResults()
                }
                binding.svSearchTodo.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                todoViewModel.searchTasks(newText ?: "")
                observeSearchResults()
                return true
            }
        })
    }

    private fun observeSearchResults() {
        todoViewModel.searchedTasks.observe(this@MainActivity, Observer { tasks ->
            tasks?.let { todoAdapter.submitList(it) }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history -> {
                // Start HistoryActivity if needed
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun openNewTask(view: View) {
        startActivity(Intent(this, TaskActivity::class.java))
    }

    override fun onItemClicked(todo: TodoModel) {
        Toast.makeText(this, "Task clicked: ${todo.title}", Toast.LENGTH_SHORT).show()
    }
}