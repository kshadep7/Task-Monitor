package com.akash.tasktimer

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object TimingContract {

    internal const val TABLE_NAME = "timing"

    //URI to access timing table

    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME)

    val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY_URI.$TABLE_NAME"
    val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY_URI.$TABLE_NAME"


    //timing fields --> columns
    object Columns {
        const val ID = BaseColumns._ID
        const val TIMIMG_TASK_ID = "taskId"
        const val TIMING_START_TIME = "startTime"
        const val TIMIMG_DURATION = "duration"

    }

    fun getId(uri: Uri): Long {
        return ContentUris.parseId(uri)
    }

    fun buildUriFromId(id: Long): Uri {
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

}