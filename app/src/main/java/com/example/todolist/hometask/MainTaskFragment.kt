package com.example.todolist.hometask

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.databinding.MainTaskFragmentBinding
import com.example.todolist.utilities.EventObserver
import com.example.todolist.utilities.setupSnackBar
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [MainTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainTaskFragment : Fragment() {

    private var _binding: MainTaskFragmentBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<TasksViewModel>()

    private val args by navArgs<MainTaskFragmentArgs>()

    private val adapter by lazy { TaskAdapter(viewModel) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        _binding = MainTaskFragmentBinding
            .inflate(layoutInflater, container, false).apply {
                viewModel = this@MainTaskFragment.viewModel
                lifecycleOwner = this@MainTaskFragment
            }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListAdapter()

        setupSnackBar()

        setupEventNavigation()

        binding?.fabAddTask?.setOnClickListener { viewModel.addNewTask() }
    }

    private fun setupEventNavigation(){
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver{
            navigateToTaskDetails(it)
        })

        viewModel.addNewTaskEvent.observe(viewLifecycleOwner, EventObserver{
            navigateToAddNewTask()
        })
    }

    private fun navigateToAddNewTask(){
        val action = MainTaskFragmentDirections
            .actionHomeToAddTask(
            null,
            resources.getString(R.string.new_task)
        )
        findNavController().navigate(action)
    }

    private fun navigateToTaskDetails(taskId: String){
        val action = MainTaskFragmentDirections
            .actionTaskDetailsDest(
                taskId
            )

        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        val result = when(item.itemId){
            R.id.all_tasks_item -> {
                //TODO:get all tasks
                Log.i("FILTER_MENU", "get All tasks")
                true
            }
            R.id.tasks_opened_item -> {
                //TODO: get tasks opened
                Log.i("FILTER_MENU", "get tasks opened")
                true
            }
            R.id.tasks_done_item -> {
                //TODO: get tasks done
                Log.i("FILTER_MENU", "get tasks done")
                true
            }
            else -> false
        }

        //TODO: filtering vu viwModel

        return result
    }

    private fun setupListAdapter(){
        binding?.run {
            if(viewModel != null){
                recyclerViewTask.layoutManager = LinearLayoutManager(requireContext())
                recyclerViewTask.adapter = adapter
            }else {
                Log.i(
                    "MainTaskFragment",
                    "ViewModel not initialized when attempting to set up adapter."
                )
            }
        }
    }

    private fun setupSnackBar(){
        //show snackBar message
        view?.setupSnackBar(this, viewModel.snackBarText, Snackbar.LENGTH_SHORT)

        //set result message returned from edit screen
        viewModel.showEditResultMessage(args.messageCode)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MainTaskFragment.
         */
        fun newInstance() = MainTaskFragment()
    }
}