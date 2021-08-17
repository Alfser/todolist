package com.example.todolist.addedittask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todolist.ADD_EDIT_RESULT_OK
import com.example.todolist.EDIT_RESULT_OK
import com.example.todolist.databinding.AddTaskFragmentBinding
import com.example.todolist.utilities.EventObserver
import com.example.todolist.utilities.dateDialog
import com.example.todolist.utilities.setupSnackBar
import com.example.todolist.utilities.timeDialog
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [AddEditTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEditTaskFragment : Fragment() {


    private var _binding: AddTaskFragmentBinding? = null
    private val binding:AddTaskFragmentBinding?
       get() = _binding

    private val viewModel: AddEditTaskViewModel by viewModels()

    private val args: AddEditTaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = AddTaskFragmentBinding.inflate(layoutInflater, container, false)

        _binding?.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this.viewLifecycleOwner
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.start(args.taskId)

        addListeners()

        setupSnackBar()

        setupNavigation()
    }

    private fun setupSnackBar() {
        view?.setupSnackBar(viewLifecycleOwner, viewModel.snackBarText, Snackbar.LENGTH_SHORT)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun addListeners(){

        binding?.run {
            //setup date listener
            textInputLayoutDate.setStartIconOnClickListener {
                dateDialog(textInputLayoutDate)
                    .show(childFragmentManager, "DATE_PICKER_TAG")
            }
            //setup time listener
            textInputLayoutTime.setStartIconOnClickListener {

                timeDialog(textInputLayoutTime)
                    .show(childFragmentManager, "TIME_PICKER_TAG")
            }
        }
    }

    private fun setupNavigation(){
        viewModel.addEditTaskEvent.observe(viewLifecycleOwner, EventObserver{
            navigateToHome()
        })
    }

    private fun navigateToHome(){

        val messageCode = if(args.taskId == null) ADD_EDIT_RESULT_OK else EDIT_RESULT_OK

        val action = AddEditTaskFragmentDirections
            .actionPopBackHome(messageCode)

        findNavController().navigate(action)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment
         *
         * @return A new instance of fragment AddTaskFragment.
         */
        fun newInstance() = AddEditTaskFragment()
    }
}