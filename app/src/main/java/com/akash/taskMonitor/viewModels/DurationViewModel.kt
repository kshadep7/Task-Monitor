package com.akash.taskMonitor.viewModels

import android.app.Application
import android.database.Cursor
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.akash.taskMonitor.singletons.TaskDurationsContract
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

enum class SortColumns {
    NAME,
    DESCRIPTION,
    START_DATE,
    DURATION
}

private const val TAG = "DurationViewModel"

class DurationViewModel(application: Application) : AndroidViewModel(application) {

    private val databaseCursor = MutableLiveData<Cursor>()
    val cursor: LiveData<Cursor>
        get() = databaseCursor

    private var sortOrder = SortColumns.NAME
        // setting order if user wants to change the order of the reports based on name, duration etc
        set(order) {
            if (field != order) {
                field = order
                loadData()
            }
        }

    init {
        loadData()
    }

    private val selection = "${TaskDurationsContract.Columns.TIMING_START_TIME} Between ? and ?"
    private val selectionArgs = arrayOf("1556668800", "1559347199")

    private fun loadData() {

        val order = when (sortOrder) {
            SortColumns.NAME -> TaskDurationsContract.Columns.TASK_NAME
            SortColumns.DESCRIPTION -> TaskDurationsContract.Columns.TASK_DESCRIPTION
            SortColumns.START_DATE -> TaskDurationsContract.Columns.TIMING_START_TIME
            SortColumns.DURATION -> TaskDurationsContract.Columns.DURATION
        }

        Log.d(TAG, "loadData: order is $order")

        GlobalScope.launch {
            val cursor = getApplication<Application>().contentResolver.query(
                TaskDurationsContract.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                order
            )
            databaseCursor.postValue(cursor)
        }
    }
}