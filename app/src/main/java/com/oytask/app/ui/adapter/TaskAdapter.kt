package com.oytask.app.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oytask.app.R
import com.oytask.app.data.model.Priority
import com.oytask.app.data.model.Task
import com.oytask.app.databinding.ItemTaskBinding
import com.oytask.app.util.DateUtils

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskLongClick: (Task) -> Unit,
    private val onCheckChanged: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    private var lastAnimatedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(view: View, position: Int) {
        if (position > lastAnimatedPosition) {
            val animation = AnimationUtils.loadAnimation(view.context, R.anim.item_slide_in)
            view.startAnimation(animation)
            lastAnimatedPosition = position
        }
    }

    override fun onViewDetachedFromWindow(holder: TaskViewHolder) {
        holder.itemView.clearAnimation()
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                tvTitle.text = task.title
                tvDescription.text = task.description
                tvDescription.visibility = if (task.description.isBlank()) View.GONE else View.VISIBLE

                checkbox.isChecked = task.isCompleted

                if (task.isCompleted) {
                    tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tvTitle.alpha = 0.5f
                    tvDescription.alpha = 0.5f
                    cardTask.alpha = 0.7f
                } else {
                    tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tvTitle.alpha = 1f
                    tvDescription.alpha = 0.8f
                    cardTask.alpha = 1f
                }

                val dateText = DateUtils.getRelativeDate(task.dueDate)
                tvDueDate.text = dateText
                tvDueDate.visibility = if (task.dueDate != null) View.VISIBLE else View.GONE

                if (task.dueDate != null && !task.isCompleted) {
                    when {
                        DateUtils.isOverdue(task.dueDate) -> {
                            tvDueDate.setTextColor(ContextCompat.getColor(root.context, R.color.error))
                        }
                        DateUtils.isToday(task.dueDate) -> {
                            tvDueDate.setTextColor(ContextCompat.getColor(root.context, R.color.warning))
                        }
                        else -> {
                            tvDueDate.setTextColor(ContextCompat.getColor(root.context, R.color.text_secondary))
                        }
                    }
                } else {
                    tvDueDate.setTextColor(ContextCompat.getColor(root.context, R.color.text_secondary))
                }

                val priorityColor = when (task.priority) {
                    Priority.URGENT -> R.color.priority_urgent
                    Priority.HIGH -> R.color.priority_high
                    Priority.MEDIUM -> R.color.priority_medium
                    Priority.LOW -> R.color.priority_low
                }
                priorityIndicator.setBackgroundColor(
                    ContextCompat.getColor(root.context, priorityColor)
                )

                tvPriority.text = when (task.priority) {
                    Priority.URGENT -> "URGENTE"
                    Priority.HIGH -> "ALTA"
                    Priority.MEDIUM -> "MEDIA"
                    Priority.LOW -> "BAJA"
                }
                tvPriority.setTextColor(ContextCompat.getColor(root.context, priorityColor))

                checkbox.setOnClickListener { onCheckChanged(task) }
                root.setOnClickListener { onTaskClick(task) }
                root.setOnLongClickListener {
                    onTaskLongClick(task)
                    true
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }
}
