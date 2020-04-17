package com.akash.tasktimer

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.task_list_item.view.*

class TaskViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {
}

private const val TAG = "CursorRecyclerViewAdapt"

class CursorRecyclerViewAdapter(private var cursor: Cursor?) :
    RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(TAG, "onCreateViewHolder: new View created in RV")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: starts")
        // avoiding smart cast issue
        val cursor = cursor
        //if cursor is null or has no entries display instructions.
        if (cursor == null || cursor.count == 0) {
            Log.d(TAG, "onBindViewHolder: providing instructions")
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
            holder.containerView.tvTaskName.text = task.name
            holder.containerView.tvTaskDescription.text = task.description
            holder.containerView.btnTaskEdit.visibility = View.VISIBLE     // TODO: add Onclick
            holder.containerView.btnTaskDelete.visibility = View.VISIBLE   // TODO: add Onclick
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: start")

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