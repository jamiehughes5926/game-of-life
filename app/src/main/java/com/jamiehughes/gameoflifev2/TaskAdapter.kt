package com.jamiehughes.gameoflifev2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val tasks: List<TaskChallengesFragment.Task>,
                  private val onCompleteTask: (TaskChallengesFragment.Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val taskName: TextView = itemView.findViewById(R.id.taskNameTextView)
        val taskCategory: TextView = itemView.findViewById(R.id.taskCategoryTextView)
        val taskTime: TextView = itemView.findViewById(R.id.taskTimeTextView)
        val taskExp: TextView = itemView.findViewById(R.id.taskExpTextView)
        val completeButton: Button = itemView.findViewById(R.id.completeButton)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.taskName.text = currentTask.name
        holder.taskCategory.text = "${currentTask.statCategory}"
        holder.taskTime.text = "${currentTask.timeLength} mins"

        // Assuming EXP is calculated as (timeLength * 10), you can change this
        holder.taskExp.text = "EXP: ${currentTask.timeLength * 10}"

        holder.completeButton.setOnClickListener {  // Assuming you have a 'completeButton' in your ViewHolder
            onCompleteTask(currentTask)
        }




    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}

