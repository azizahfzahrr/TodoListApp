package com.azizahfzahrr.todolistapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.azizahfzahrr.todolistapp.databinding.ActivityTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTaskBinding
    private lateinit var myCalendar: Calendar

    private var finalDate = 0L
    private var finalTime = 0L

    private val viewModel: TodoViewModel by viewModels {
        TodoViewModelFactory(TodoRepository(TodoDatabase.getDatabase(this).todoDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dateEdt.setOnClickListener(this)
        binding.timeEdt.setOnClickListener(this)
        binding.saveBtn.setOnClickListener(this)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAddTask)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbarAddTask.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dateEdt -> setDateListener()
            R.id.timeEdt -> setTimeListener()
            R.id.saveBtn -> saveTodo()
        }
    }

    private fun saveTodo() {
        val title = binding.titleInpLay.editText?.text.toString().trim()
        val description = binding.taskInpLay.editText?.text.toString().trim()

        if (title.isEmpty()) {
            binding.titleInpLay.error = "Title is required"
            return
        }

        if (description.isEmpty()) {
            binding.taskInpLay.error = "Description is required"
            return
        }

        if (finalDate == 0L) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        if (finalTime == 0L) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
            return
        }

        val todo = TodoModel(title = title, description = description, date = finalDate, time = finalTime)

        viewModel.insertTask(todo)
        Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun setTimeListener() {
        myCalendar = Calendar.getInstance()

        TimePickerDialog(this, { _, hour, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hour)
            myCalendar.set(Calendar.MINUTE, minute)
            updateTime()
        }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), false).show()
    }

    private fun updateTime() {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        finalTime = myCalendar.time.time
        binding.timeEdt.setText(sdf.format(myCalendar.time))
    }

    private fun setDateListener() {
        myCalendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
            updateDate()

            binding.timeInptLay.visibility = View.VISIBLE
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }


    private fun updateDate() {
        val sdf = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
        finalDate = myCalendar.time.time
        binding.dateEdt.setText(sdf.format(myCalendar.time))
    }
}