package com.akash.taskMonitor.activities

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.akash.taskMonitor.R
import com.akash.taskMonitor.adapters.TaskDurationsRVAdapter
import com.akash.taskMonitor.singletons.TaskDurationsContract
import kotlinx.android.synthetic.main.task_durations.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "DurationReportActivity"

enum class SortColumns {
    NAME,
    DESCRIPTION,
    START_DATE,
    DURATION
}

class DurationReportActivity : AppCompatActivity() {

    private val durationsRVAdapter by lazy { TaskDurationsRVAdapter(this, null) }

    private var databaseCursor: Cursor? = null
    private val sortOrder = SortColumns.NAME

    private val selection = "${TaskDurationsContract.Columns.TIMING_START_TIME} Between ? and ?"
    private val selectionArgs = arrayOf("1556668800", "1559347199")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duration_report)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rv_taskDurations_list.layoutManager = LinearLayoutManager(this)
        rv_taskDurations_list.adapter = durationsRVAdapter

        loadData()
    }

    private fun loadData() {

        val order = when (sortOrder) {
            SortColumns.NAME -> TaskDurationsContract.Columns.TASK_NAME
            SortColumns.DESCRIPTION -> TaskDurationsContract.Columns.TASK_DESCRIPTION
            SortColumns.START_DATE -> TaskDurationsContract.Columns.TIMING_START_TIME
            SortColumns.DURATION -> TaskDurationsContract.Columns.DURATION
        }

        Log.d(TAG, "loadData: order is $order")

        GlobalScope.launch {
            val cursor = application.contentResolver.query(
                TaskDurationsContract.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                order
            )
            databaseCursor = cursor
            durationsRVAdapter.swapCursor(cursor)?.close()

        }
    }
}
