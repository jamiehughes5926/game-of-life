package com.jamiehughes.gameoflifev2

import SharedViewModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json



class TaskChallengesFragment : Fragment() {

    private var tasks = mutableListOf<Task>()
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var sharedViewModel: SharedViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragement_tasks_challenges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadTasks()

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)


        // Setup RecyclerView for task list here

        taskRecyclerView = view.findViewById(R.id.taskRecyclerView)
        taskAdapter = TaskAdapter(tasks) { task: TaskChallengesFragment.Task ->
            completeTask(task)
        }


        taskRecyclerView.adapter = taskAdapter
        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())



        val taskNameEditText: EditText = view.findViewById(R.id.taskNameEditText)
        val timeLengthEditText: EditText = view.findViewById(R.id.timeLengthEditText)
        val statCategorySpinner: Spinner = view.findViewById(R.id.statCategorySpinner)

        // Populate the Spinner with stat categories
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.stat_categories, // Define this array in your strings.xml
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statCategorySpinner.adapter = adapter

        val createButton: Button = view.findViewById(R.id.createTaskButton)
        createButton.setOnClickListener {
            // Get values from the UI
            val name = taskNameEditText.text.toString()
            val timeLength = timeLengthEditText.text.toString().toLong()
            val statCategory = statCategorySpinner.selectedItem.toString()

            // Generate an id for the task
            val id = UUID.randomUUID().toString()

            val task = Task(id, name, statCategory, timeLength)
            addTask(task)
        }
    }
    private fun saveTasks() {
        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val editor = sharedPreferences.edit()
        val jsonTasks = Json.encodeToString(tasks)
        editor.putString("tasks", jsonTasks)
        editor.apply()
    }

    private fun loadTasks() {
        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val jsonTasks = sharedPreferences.getString("tasks", null)
        if (jsonTasks != null) {
            tasks = Json.decodeFromString<List<Task>>(jsonTasks).toMutableList()
        }
    }


    private fun addTask(task: Task) {
        tasks.add(task)
        taskAdapter.notifyDataSetChanged()
        saveTasks()
    }


    private fun completeTask(task: Task) {
        tasks.remove(task)
        taskAdapter.notifyDataSetChanged()
        saveTasks()

        // Calculate XP to add, assuming 10 XP per minute of task time
        val xpToAdd = (task.timeLength * 10).toInt()

        // Add XP to user's level (assuming addXP updates both level and XP)
        sharedViewModel.addXP(xpToAdd)

        // Add a stat point for every 100 minutes in the task
        val statPoints = (task.timeLength / 100).toInt()

        // Update the specific stat category (assuming updateStat is a function that does this)
        sharedViewModel.updateStat(task.statCategory, statPoints)
    }

    @Serializable
    data class Task(
        val id: String,
        val name: String,
        val statCategory: String,
        val timeLength: Long,
        val isCompleted: Boolean = false,
        val xp: Int = ((timeLength * 10).toInt()),  // Assuming 10 XP per minute
        val statPoints: Int = (timeLength / 100).toInt()  // 1 point for every 100 minutes
    )
}
