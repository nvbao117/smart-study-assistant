package hcmute.edu.vn.smartstudyassistant.presentation.task

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskEntity
import hcmute.edu.vn.smartstudyassistant.databinding.ItemTaskBinding
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.Priority
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskStatus

class TaskAdapter(
    private val onTaskClick: (TaskEntity) -> Unit,
    private val onTaskComplete: (TaskEntity) -> Unit
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskEntity) {
            binding.tvTaskTitle.text = task.title
            binding.tvTaskSubject.text = "Môn học (cần map theo ID môn)" // TODO
            
            binding.cbComplete.isChecked = task.status == TaskStatus.DONE
            
            if (binding.cbComplete.isChecked) {
                binding.tvTaskTitle.paintFlags = binding.tvTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvTaskTitle.alpha = 0.5f
                binding.tvTaskSubject.alpha = 0.5f
                binding.root.alpha = 0.7f
            } else {
                binding.tvTaskTitle.paintFlags = binding.tvTaskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvTaskTitle.alpha = 1.0f
                binding.tvTaskSubject.alpha = 1.0f
                binding.root.alpha = 1.0f
            }

            // Priority Color
            val priorityColor = when(task.priority) {
                Priority.URGENT -> R.color.accent_urgent
                Priority.HIGH -> R.color.accent_warning
                Priority.MEDIUM -> R.color.accent_primary
                Priority.LOW -> R.color.text_secondary_dark
            }
            binding.viewPriority.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, priorityColor)

            binding.cbComplete.setOnClickListener {
                onTaskComplete(task)
            }

            binding.root.setOnClickListener {
                onTaskClick(task)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}
