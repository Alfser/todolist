package com.example.todolist.addtask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.AddTaskFragmentBinding
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.datasource.model.Task
import com.example.todolist.utilities.dateDialog
import com.example.todolist.utilities.text
import com.example.todolist.utilities.timeDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * A simple [Fragment] subclass.
 * Use the [AddTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddTaskFragment : Fragment() {


    private var _binding: AddTaskFragmentBinding? = null
    private val binding:AddTaskFragmentBinding?
       get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = AddTaskFragmentBinding.inflate(layoutInflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //hidden bottom navigator
        setBottomNavVisible(false)

        addListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        setBottomNavVisible(true)
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

                buttonAddNewTask.setOnClickListener { saveNewTask() }
        }
    }

    private fun saveNewTask(){
        binding?.apply {
            val task = Task(
                title = textInputLayoutTitle.text,
                description = textInputLayoutDescription.text,
                date = textInputLayoutDate.text,
                timer = textInputLayoutTime.text
            )

            TaskDataSource.insertNewTask(task)
            findNavController().navigate(R.id.action_pop_back_home)
        }
    }

    private fun setBottomNavVisible(isVisible: Boolean){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        val visibility = if(isVisible) View.VISIBLE else View.GONE

        bottomNavigationView?.visibility = visibility
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment
         *
         * @return A new instance of fragment AddTaskFragment.
         */
        fun newInstance() = AddTaskFragment()
    }
}