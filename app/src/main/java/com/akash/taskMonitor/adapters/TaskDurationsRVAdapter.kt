package com.akash.taskMonitor.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akash.taskMonitor.R
import com.akash.taskMonitor.singletons.TaskDurationsContract
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.task_durations_item.view.*
import java.text.DateFormat
import java.util.*


class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer

private const val TAG = "TaskDurationsRVAdapter"

class TaskDurationsRVAdapter(context: Context, private var cursor: Cursor?) :
    RecyclerView.Adapter<ViewHolder>() {

    private var dateFormat = DateFormat.getDateInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_durations_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // smart cast
        val cursor = cursor

        if (cursor != null || cursor?.count != 0) {
            if (!cursor?.moveToPosition(position)!!) {
                throw IllegalStateException("Cannot move to given position: $position")
            }

            val name =
                cursor.getString(cursor.getColumnIndex(TaskDurationsContract.Columns.TASK_NAME))
            val description =
                cursor.getString(cursor.getColumnIndex(TaskDurationsContract.Columns.TASK_DESCRIPTION))
            val startTime =
                cursor.getLong(cursor.getColumnIndex(TaskDurationsContract.Columns.TIMING_START_TIME))
            val duration =
                cursor.getLong(cursor.getColumnIndex(TaskDurationsContract.Columns.DURATION))

            val date = dateFormat.format(startTime * 1000) // converting to millisec
            val formatedDuration = formatDuration(duration)

            holder.containerView.tv_tditem_taskName.text = name
            // as description is not in potrait that's why nullable
            holder.containerView.tv_tditem_description?.text = description
            holder.containerView.tv_tditem_startTime.text = date
            holder.containerView.tv_tditem_duration.text = formatedDuration
        }
    }

    private fun formatDuration(duration: Long): String {
        val hours = duration / 3600
        val remaining = duration - hours * 3600
        val minutes = remaining / 60
        val seconds = remaining - minutes * 60

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
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