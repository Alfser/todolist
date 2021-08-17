package com.example.todolist.taskdetails

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todolist.R
import com.example.todolist.DELETE_RESULT_OK
import com.example.todolist.databinding.TaskDetailsFragmentBinding
import com.example.todolist.utilities.EventObserver
import com.example.todolist.utilities.setupSnackBar
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [TaskDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskDetailsFragment : Fragment() {

    private var _binding: TaskDetailsFragmentBinding? = null
    val binding get() = _binding

    private val viewModel: TaskDetailsViewModel by viewModels()

    private val args: TaskDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = TaskDetailsFragmentBinding.inflate(layoutInflater, container, false)

        _binding?.let {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        viewModel.start(args.taskId)

        setHasOptionsMenu(true)

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFab()

        view.setupSnackBar(this, viewModel.snackBarText, Snackbar.LENGTH_SHORT)

        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.deleteTaskEvent.observe(viewLifecycleOwner, EventObserver{
            val action = TaskDetailsFragmentDirections
                .actionDetailsToHomeDest(DELETE_RESULT_OK)
            findNavController().navigate(action)
        })

        viewModel.editTaskEvent.observe(viewLifecycleOwner, EventObserver{
            val action = TaskDetailsFragmentDirections.actionDetailsToAddTaskDest(
                args.taskId,
                resources.getString(R.string.edit_task)
            )
            findNavController().navigate(action)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_task, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.delete_task -> {
                viewModel.deleteTask()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupFab(){
        _binding?.fabEditTask?.setOnClickListener { viewModel.editTask() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment TaskDetailsFragment.
         */

        fun newInstance() =
            TaskDetailsFragment()
    }
}