package com.akash.tasktimer

import android.app.Application
import android.database.Cursor
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "TaskTimerVIewModel"

class TaskTimerViewModel(application: Application) : AndroidViewModel(application) {
    private val databaseCursor = MutableLiveData<Cursor>()

    val cursor: LiveData<Cursor>
        get() = databaseCursor

    init {
        Log.d(TAG, "init: Android ViewModel Created")
        loadTasks()
    }

    private fun loadTasks() {
        Log.d(TAG, "loadTasks: called")

        val projection = arrayOf(
            TaskContract.Columns.ID,
            TaskContract.Columns.TASK_NAME,
            TaskContract.Columns.TASK_DESCRIPTION,
            TaskContract.Columns.TASK_SORT_ORDER
        )
        // order by selection criteria
        val sortOrder = "${TaskContract.Columns.TASK_SORT_ORDER}, ${TaskContract.Columns.TASK_NAME}"

        /** get cursor of a particular task by calling [AppContentProvider.query] */

        val cursor = getApplication<Application>().contentResolver.query(
            TaskContract.CONTENT_URI,
            projection, null, null, sortOrder
        )

        databaseCursor.postValue(cursor)

    }
}