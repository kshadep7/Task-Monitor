package com.akash.taskMonitor

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * A placeholder fragment containing a simple view.
 */

private const val TAG = "MainActivityFragment"
private const val DIALOG_ID_DELETE = 1
private const val DIALOG_TASK_ID = "task_id"

@Suppress("DEPRECATION")
class MainActivityFragment : Fragment(),
    CursorRecyclerViewAdapter.OnTaskClickListener,
    AppDialog.DialogEvents {

    interface OnTaskEdit {
        fun onTaskEdit(task: Task)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(TaskTimerViewModel::class.java)
    }
    private val rvAdapter = CursorRecyclerViewAdapter(null, this)

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: called")
        super.onAttach(context)

        if (context !is OnTaskEdit)
            throw RuntimeException("$context must implement OnTaskEdit interface!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: called")
        viewModel.cursor.observe(this, Observer { cursor -> rvAdapter.swapCursor(cursor)?.close() })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: inflating the fragment layout")
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // adding the recycler view adapter
        tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        tasksRecyclerView.adapter = rvAdapter
    }

    override fun onEditClick(task: Task) {
        (activity as OnTaskEdit).onTaskEdit(task)
    }

    override fun onDeleteClick(task: Task) {
        val args = Bundle().apply {
            putInt(DIALOG_ID, DIALOG_ID_DELETE)
            putString(
                DIALOG_MSG,
                getString(R.string.delete_dialog_message, task.id, task.name)
            )
            putInt(DIALOG_POSITIVE_RID, R.string.delete_dialog_positive_value)
            putLong(DIALOG_TASK_ID, task.id)
        }
        val dialog = AppDialog()
        dialog.arguments = args
        dialog.show(childFragmentManager, null)
    }

    override fun onTaskLongClick(task: Task) {
        TODO("Not yet implemented")
    }

    override fun onPositiveDialogResult(dialogId: Int, bundle: Bundle) {
        Log.d(TAG, "onPositiveDialogResult: called with dialogId: $dialogId")
        val taskId = bundle.getLong(DIALOG_TASK_ID)
        //check if dialog is same as App dialog class is a general purpose class
        if (dialogId == DIALOG_ID_DELETE) {
            if (BuildConfig.DEBUG && taskId == 0L) throw AssertionError("Task ID is zero")
            viewModel.deleteTask(taskId)
        }
    }

    override fun onNegativeDialogResult(dialogId: Int, bundle: Bundle) {
        Log.d(TAG, "onNegativeDialogResult: called with dialogId: $dialogId")
    }

}