package com.akash.taskMonitor.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.akash.taskMonitor.R
import com.akash.taskMonitor.adapters.TaskDurationsRVAdapter
import com.akash.taskMonitor.fragments.DATE_PICKER_DATE
import com.akash.taskMonitor.fragments.DATE_PICKER_ID
import com.akash.taskMonitor.fragments.DATE_PICKER_TITLE
import com.akash.taskMonitor.fragments.DatePickerDialogFragment
import com.akash.taskMonitor.viewModels.DurationViewModel
import com.akash.taskMonitor.viewModels.SortColumns
import kotlinx.android.synthetic.main.task_durations.*

private const val TAG = "DurationReportActivity"
private const val DIALOG_FILTER = 1
private const val DIALOG_DELETE = 2

class DurationReportActivity : AppCompatActivity(),
    DatePickerDialog.OnDateSetListener,
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.rm_title_period -> {
                durationViewModel.toggleDisplayWeek() // method to change the data according to period (7 or 1) selected
                invalidateOptionsMenu() // forcing to call onPreparedOptionMenu() to change the icons and text
            }
            R.id.rm_title_date -> {
                showDatePickerDialog(getString(R.string.date_filter_dialog_title), DIALOG_FILTER)
                return true
            }
            R.id.rm_delete -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        val item = menu.findItem(R.id.rm_title_period)

        if (item != null) {
            if (durationViewModel.displayWeek) {
                item.setIcon(R.drawable.ic_baseline_filter_7_24)
                item.setTitle(R.string.rm_title_filter_week)
            } else {
                item.setIcon(R.drawable.ic_baseline_filter_1_24)
                item.setTitle(R.string.rm_title_filter_day)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun showDatePickerDialog(title: String, dialogId: Int) {
        val dialogFragment = DatePickerDialogFragment()

        val arguments = Bundle()
        arguments.putInt(DATE_PICKER_ID, dialogId)
        arguments.putString(DATE_PICKER_TITLE, title)
        arguments.putSerializable(DATE_PICKER_DATE, durationViewModel.getFilterDate())
        dialogFragment.arguments = arguments
        dialogFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Log.d(TAG, "onDateSet: called from DurationReportActivity")

        val dialogId = view?.tag as Int
        when (dialogId) {
            DIALOG_FILTER -> {
                durationViewModel.setReportDate(year, month, dayOfMonth)
            }
            DIALOG_DELETE -> {
            }
            else -> throw IllegalArgumentException("Invalid argument when receiving the DatePickerDialog result")
        }
    }
}
