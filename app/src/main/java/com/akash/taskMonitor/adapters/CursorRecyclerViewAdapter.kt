package com.akash.taskMonitor.adapters

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akash.taskMonitor.R
import com.akash.taskMonitor.models.Task
import com.akash.taskMonitor.singletons.TaskContract
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.task_list_item.view.*

class TaskViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    fun bind(task: Task, listener: CursorRecyclerViewAdapter.OnTaskClickListener) {

        containerView.tvTaskName.text = task.name
        containerView.tvTaskDescription.text = task.description
        containerView.btnTaskEdit.visibility = View.VISIBLE
        containerView.btnTaskDelete.visibility = View.VISIBLE

        containerView.btnTaskEdit.setOnClickListener {
            listener.onEditClick(task)
        }
        containerView.btnTaskDelete.setOnClickListener {
            listener.onDeleteClick(task)
        }

        containerView.setOnLongClickListener {
            listener.onTaskLongClick(task)
            true
        }

    }

}

private const val TAG = "CursorRecyclerViewAdapt"

class CursorRecyclerViewAdapter(
    private var cursor: Cursor?,
    private val listener: OnTaskClickListener
) :
    RecyclerView.Adapter<TaskViewHolder>() {

    interface OnTaskClickListener {
        fun onEditClick(task: Task)
        fun onDeleteClick(task: Task)
        fun onTaskLongClick(task: Task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // avoiding smart cast issue
        val cursor = cursor
        //if cursor is null or has no entries display instructions.
        if (cursor == null || cursor.count == 0) {
            holder.containerView.tvTaskName.setText(R.string.new_task_instructions)
            holder.containerView.tvTaskDescription.setText(R.string.instructions)
            holder.containerView.btnTaskDelete.visibility = View.GONE
            holder.containerView.btnTaskEdit.visibility = View.GONE
        } else {
            // if position entry not found
            if (!cursor.moveToPosition(position)) {
                throw IllegalStateException("Couldn't move to position $position")
            }

            //creating Task object using cursor from database rows
            val task = Task(
                cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK_NAME)),
                cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(TaskContract.Columns.TASK_SORT_ORDER))
            )
            // as task id is not in task constructor
            task.id = cursor.getLong(cursor.getColumnIndex(TaskContract.Columns.ID))

            // setting the values to views
            holder.bind(task, listener)
        }
    }

    override fun getItemCount(): Int {

        val cursor = cursor
        val count = if (cursor == null || cursor.count == 0) 1 else cursor.count

        return count
    }

    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor == cursor) return null

        val numItems = itemCount
        val oldCursor = cursor
        cursor = newCursor
        if (newCursor != null) notifyDataSetChanged()
        else notifyItemRangeRemoved(0, numItems)

        return oldCursor
    }

}