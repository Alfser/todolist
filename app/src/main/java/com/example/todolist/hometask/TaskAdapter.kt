package com.example.todolist.hometask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.RecyclerViewTaskItemBinding
import com.example.todolist.datasource.model.Task

class TaskAdapter(
    private val viewModel: TasksViewModel
):ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(viewModel, task)
    }

    class TaskViewHolder private constructor(private val binding: RecyclerViewTaskItemBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(viewModel: TasksViewModel, item: Task){
            binding.task = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): TaskViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerViewTaskItemBinding.inflate(layoutInflater, parent, false)
                return TaskViewHolder(binding)
            }
        }
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class TaskDiffCallback : DiffUtil.ItemCallback<Task>(){
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}