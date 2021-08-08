package com.example.todolist.hometask

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.databinding.MainTaskFragmentBinding
import com.example.todolist.datasource.TaskDataSource
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

        //setup FAB
        binding?.fabAddTask?.setOnClickListener { addTask() }

        setupListAdapter()

        setupSnackBar()
    }

    private fun addTask(){
        findNavController().navigate(R.id.action_add_task)
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
        view?.setupSnackBar(this, viewModel.snackBarText, Snackbar.LENGTH_SHORT)
        //TODO: add message when event saved, updated and excluded has happened

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