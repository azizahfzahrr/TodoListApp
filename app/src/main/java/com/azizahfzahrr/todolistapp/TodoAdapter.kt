package com.azizahfzahrr.todolistapp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azizahfzahrr.todolistapp.databinding.ItemTodoBinding
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(
    private val context: Context,
    private val listener: TodoClickListener
) : ListAdapter<TodoModel, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    data class PendingAction(val position: Int, val type: ActionType)

    enum class ActionType {
        DELETE,
        FINISH
    }

    var pendingAction: PendingAction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        when (pendingAction?.type) {
            ActionType.DELETE -> {
                if (pendingAction?.position == position) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#D32F2F"))
                } else {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            ActionType.FINISH -> {
                if (pendingAction?.position == position) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#388E3C"))
                } else {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            null -> {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    fun resetPendingAction() {
        pendingAction = null
        notifyDataSetChanged()
    }

    inner class TodoViewHolder(val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: TodoModel) {
            val colors = context.resources.getIntArray(R.array.random_color)
            val randomColor = colors[Random().nextInt(colors.size)]
            binding.viewColorTag.setBackgroundColor(randomColor)

            binding.txtShowTitle.text = todo.title
            binding.txtShowTask.text = todo.description

            updateDate(todo.date)
            updateTime(todo.time)

            binding.root.setOnClickListener {
                listener.onItemClicked(todo)
            }
        }

        private fun updateTime(time: Long) {
            val myFormat = "h:mm a"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            binding.txtShowTime.text = sdf.format(Date(time))
        }

        private fun updateDate(time: Long) {
            val myFormat = "EEE, d MMM yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            binding.txtShowDate.text = sdf.format(Date(time))
        }
    }

    interface TodoClickListener {
        fun onItemClicked(todo: TodoModel)
    }

    class TodoDiffCallback : DiffUtil.ItemCallback<TodoModel>() {
        override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
            return oldItem == newItem
        }
    }
}