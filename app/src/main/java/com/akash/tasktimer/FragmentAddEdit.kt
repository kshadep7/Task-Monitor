package com.akash.tasktimer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_add_edit.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TASK = "task"
private const val TAG = "Fragment Add Edit"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAddEdit.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class FragmentAddEdit : Fragment() {
    private var task: Task? = null
    private var listener: OnSaveClickListener? = null
    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(TaskTimerViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: starts")
        super.onAttach(context)
        if (context is OnSaveClickListener)
            listener = context
        else throw RuntimeException(context.toString() + "must implement OnSaveClickListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)

        arguments?.let {
            task = it.getParcelable(ARG_TASK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: starts")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val task = task
            if (task != null) {
                Log.d(TAG, "onViewCreated: savedInstanceState Bundle info -> $task")
                editTextAddName.setText(task.name)
                editTextDescription.setText(task.description)
                editTextSortorder.setText(task.sortOrder.toString())
            } else {
                //Do Nothing or add or edit the task.
                Log.d(TAG, "onViewCreated: No args, adding new task")
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: start")
        super.onActivityCreated(savedInstanceState)

        if (listener is AppCompatActivity) {
            val actionBar = (listener as AppCompatActivity).supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }

        btnSave.setOnClickListener {
            // saving data -> adding new task or editing existing one
            saveTask()
            /** Implementing method in [MainActivity.onSaveClicked]*/
            listener?.onSaveClicked()

        }
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach: starts")
        super.onDetach()
        listener = null
    }

    private fun saveTask() {
        // creating a new task using taskFromUi method

        val newTask = taskFromUi()
        // check if the newTask is equal to old task 
        if (newTask != task) {
            Log.d(TAG, "saveTask: saving a new task with id: ${newTask.id}")
            task = viewModel.saveTake(newTask)
            Log.d(TAG, "saveTask: task saved, id: ${task!!.id}")
        }
    }

    fun isDirty(): Boolean {
        val newTask = taskFromUi()
        return (newTask != task) && (newTask.name.isNotBlank()
                || newTask.description.isNotBlank()
                || newTask.sortOrder != 0)
    }

    private fun taskFromUi(): Task {
        val sortOrder =
            if (editTextSortorder.text.isNotEmpty())
                Integer.parseInt(editTextSortorder.text.toString())
            else 0

        val newTask =
            Task(editTextAddName.text.toString(), editTextDescription.text.toString(), sortOrder)
        newTask.id = task?.id ?: 0

        return newTask
    }

/*
    private fun saveTask() {
        // to save the task details or to update the existing one
        val values = ContentValues()
        val task = task
        // 1. checking if sort order is mentioned or not is (if yes -> convert to Int if no -> 0)
        var sortOrder =
            if (editTextSortorder.text.isNotEmpty()) Integer.parseInt(editTextSortorder.text.toString())
            else 0
        // updating the existing the record/task
        if (task != null) {
            //name:
            if (editTextAddName.text.toString() != task.name)
                values.put(TaskContract.Columns.TASK_NAME, editTextAddName.text.toString())
            // Description
            if (editTextDescription.text.toString() != task.description)
                values.put(
                    TaskContract.Columns.TASK_DESCRIPTION,
                    editTextDescription.text.toString()
                )
            // sort order
            if (sortOrder != task.sortOrder)
                values.put(TaskContract.Columns.TASK_SORT_ORDER, sortOrder)

            //update the database
            if (values.size() != 0) {
                Log.d(TAG, "saveTask: now updating values in database")

                */
    /** using update function from [AppContentProvider.update] *//*

                thread {
                    activity?.contentResolver?.update(
                        TaskContract.buildUriFromId(task.id),
                        values, null, null
                    )
                }
            }
        } else {
            // Adding a brand new the record/task
            Log.d(TAG, "saveTask: Adding a new task")
            if (editTextAddName.text.toString().isNotEmpty()) {
                values.put(TaskContract.Columns.TASK_NAME, editTextAddName.text.toString())
                if (editTextDescription.text.toString().isNotEmpty())
                    values.put(
                        TaskContract.Columns.TASK_DESCRIPTION,
                        editTextDescription.text.toString()
                    )
                values.put(
                    TaskContract.Columns.TASK_SORT_ORDER,
                    sortOrder // --> already checked at the start of the functions
                )
                */
    /** using insert function from [AppContentProvider.insert] *//*

                thread {
                    activity?.contentResolver?.insert(TaskContract.CONTENT_URI, values)
                }
            }
        }
    }
*/

    interface OnSaveClickListener {
        fun onSaveClicked()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param task To edit task and null to add new task.
         * @return A new instance of fragment [FragmentAddEdit].
         */
        @JvmStatic
        fun newInstance(task: Task?) =
            FragmentAddEdit().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK, task)
                }
            }
    }
}
