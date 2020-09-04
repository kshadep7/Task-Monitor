package com.akash.taskMonitor.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.akash.taskMonitor.R
import com.akash.taskMonitor.adapters.TaskDurationsRVAdapter
import com.akash.taskMonitor.viewModels.DurationViewModel
import com.akash.taskMonitor.viewModels.SortColumns
import kotlinx.android.synthetic.main.task_durations.*

private const val TAG = "DurationReportActivity"


class DurationReportActivity : AppCompatActivity(),
    View.OnClickListener {

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

        textView_taskName_heading.setOnClickListener(this)
        textView_duration_heading.setOnClickListener(this)
        textView_startTime_heading.setOnClickListener(this)
        // "?" coz description is not present in the potrait orientation.
        textView_description_heading?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.textView_taskName_heading -> durationViewModel.sortOrder = SortColumns.NAME
            R.id.textView_description_heading -> durationViewModel.sortOrder =
                SortColumns.DESCRIPTION
            R.id.textView_startTime_heading -> durationViewModel.sortOrder = SortColumns.START_DATE
            R.id.textView_duration_heading -> durationViewModel.sortOrder = SortColumns.DURATION
        }
    }
}
