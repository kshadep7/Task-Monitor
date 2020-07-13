package com.akash.taskMonitor.singletons

import android.net.Uri
import com.akash.taskMonitor.utilities.CONTENT_AUTHORITY_URI

object TaskDurationsContract {
    internal const val TABLE_NAME = "viewTaskDurations"

//URI to access task durations view

    val CONTENT_URI: Uri = Uri.withAppendedPath(
        CONTENT_AUTHORITY_URI,
        TABLE_NAME
    )

    val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY_URI.$TABLE_NAME"
    val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY_URI.$TABLE_NAME"


    //task durations view fields --> columns
// no need of ID for this view
    object Columns {
        const val TASK_NAME = TaskContract.Columns.TASK_NAME
        const val TASK_DESCRIPTION = TaskContract.Columns.TASK_DESCRIPTION
        const val TIMING_START_TIME = TimingContract.Columns.TIMING_START_TIME
        const val START_DATE = "startDate"
        const val DURATION = TimingContract.Columns.TIMIMG_DURATION
    }
}