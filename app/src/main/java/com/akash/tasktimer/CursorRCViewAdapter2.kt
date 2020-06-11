package com.akash.tasktimer

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.task_list_item.view.*

class TaskViewHolder2(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {
}

// Test Class RC Adapter =============================================

private const val TAG = "CursorRCViewAdapter2"

class CursorRCViewAdapter2(private var cursor: Cursor?) : RecyclerView.Adapter<TaskViewHolder2>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder2 {
        Log.d(TAG, "onCreateViewHolder: new view requested")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder2(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder2, position: Int) {
        Log.d(TAG, "onBindViewHolder: starts")

        var cursor = cursor
        if (cursor == null || cursor.count == 0) {
            Log.d(TAG, "onBindViewHolder: providing instructions...")
            holder.containerView.tvTaskName.setText("INSTRUCTIONS")
            holder.containerView.tvTaskDescription.setText("")
            holder.containerView.btnTaskEdit.visibility = View.GONE
            holder.containerView.btnTaskDelete.visibility = View.GONE
        } else {
            if (!cursor.moveToPosition(position))
                throw IllegalStateException("Couldn't move cursor to position $position")

            val task = Task(
                cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK_NAME)),
                cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(TaskContract.Columns.TASK_SORT_ORDER))
            )

            val id = cursor.getColumnIndex(TaskContract.Columns.ID)

            holder.containerView.tvTaskName.text = task.name
            holder.containerView.tvTaskName.text = task.description
            holder.containerView.btnTaskEdit.visibility = View.VISIBLE
            holder.containerView.btnTaskDelete.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: starts")

        val cursor = cursor
        // to show the instructions view holder if no tasks to display
        return if (cursor == null || cursor.count == 0) 1
        else cursor.count
    }

    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor == cursor)
            return null

        val numItems = itemCount
        val oldCursor = cursor
        cursor = newCursor
        if (newCursor != null) notifyDataSetChanged() // notify to observers about the data changed
        else notifyItemRangeRemoved(0, numItems) // notify observers lack of data

        return oldCursor
    }

}