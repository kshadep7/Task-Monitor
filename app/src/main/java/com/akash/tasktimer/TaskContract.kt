package com.akash.tasktimer

import android.provider.BaseColumns

object TaskContract {

    internal const val TABLE_NAME = "task"

    object Columns {
        const val ID = BaseColumns._ID
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val SORT_ORDER = "sortorder"
    }
}