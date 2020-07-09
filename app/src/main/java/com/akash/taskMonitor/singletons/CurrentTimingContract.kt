package com.akash.taskMonitor.singletons

import android.net.Uri
import com.akash.taskMonitor.utilities.CONTENT_AUTHORITY_URI

object CurrentTimingContract {

    internal const val TABLE_NAME = "viewCurrentTimingsContract"

    //URI to access current timings view

    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI,
        TABLE_NAME
    )

    val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY_URI.$TABLE_NAME"
    val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY_URI.$TABLE_NAME"


    //current timings view fields --> columns
    // no need of ID for this view
    object Columns {
        const val TIMIMG_ID = TimingContract.Columns.ID
        const val TIMING_TASK_ID = TimingContract.Columns.TIMIMG_TASK_ID
        const val TIMING_START_TIME = TimingContract.Columns.TIMING_START_TIME
        const val TASK_NAME = TaskContract.Columns.TASK_NAME
    }
}