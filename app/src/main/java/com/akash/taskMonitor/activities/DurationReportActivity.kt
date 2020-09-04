package com.akash.taskMonitor.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.akash.taskMonitor.R
import com.akash.taskMonitor.adapters.TaskDurationsRVAdapter
import com.akash.taskMonitor.viewModels.DurationViewModel
import kotlinx.android.synthetic.main.task_durations.*

private const val TAG = "DurationReportActivity"


class DurationReportActivity : AppCompatActivity() {

    private val durationViewModel by lazy {
        ViewModelProvider(this).get(DurationViewModel::class.java)
    }
    private val durationsRVAdapter by lazy {
        TaskDurationsRVAdapter(this, null)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duration_report)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rv_taskDurations_list.layoutManager = LinearLayoutManager(this)
        rv_taskDurations_list.adapter = durationsRVAdapter

        // observe the database cursor changes from the view model
        durationViewModel.cursor.observe(this, Observer { cursor ->
            durationsRVAdapter.swapCursor(cursor)?.close()
        })
    }
}
