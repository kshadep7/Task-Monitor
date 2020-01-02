package com.akash.tasktimer

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object TaskContract {

    internal const val TABLE_NAME = "task"

    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME)

    object Columns {
        const val TASK_ID = BaseColumns._ID
        const val TASK_NAME = "name"
        const val TASK_DESCRIPTION = "description"
        const val TASK_SORT_ORDER = "sortorder"
    }

    fun getId(uri: Uri): Long {
        return ContentUris.parseId(uri)
    }

    fun buildUriFromId(id: Long): Uri {
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }
}