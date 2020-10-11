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
import java.text.DateFormat
import java.util.*

enum class SortColumns {
    NAME,
    DESCRIPTION,
    START_DATE,
    DURATION
}

private const val TAG = "DurationViewModel"

class DurationViewModel(application: Application) : AndroidViewModel(application) {

    private var calender = GregorianCalendar()

    private val databaseCursor = MutableLiveData<Cursor>()
    val cursor: LiveData<Cursor>
        get() = databaseCursor

    var sortOrder = SortColumns.NAME    // default sort order
        // setting order if user wants to change the order of the reports based on name, duration etc
        set(order) {
            if (field != order) {
                field = order
                loadData()
            }
        }

    private var _displayWeek = true
    val displayWeek: Boolean
        get() = _displayWeek

    private val selection = "${TaskDurationsContract.Columns.TIMING_START_TIME} Between ? and ?"

    //    private var selectionArgs = arrayOf("1556668800", "1559347199")
    // setting selection args by day or week in applyFilter() method
    private var selectionArgs = emptyArray<String>()

    init {
//        loadData()
        // now we can call apply filter here instead of loading data directly
        // so that user can toggle day or week accordingly
        applyFilter()
    }

    fun toggleDisplayWeek() {
        _displayWeek = !_displayWeek
        applyFilter()
    }

    fun getFilterDate(): Date {
        return calender.time
    }

    private fun applyFilter() {
        Log.d(TAG, "applyFilter: started")
        val dateFormat = DateFormat.getDateInstance()
        val currentCalenderTime = calender.timeInMillis
        if (displayWeek) {
            // for week -> default
            // first we have to check which day of the week is it
            val weekStart = calender.firstDayOfWeek
            Log.d(TAG, "applyFilter: first day of calendar week is $weekStart")
            Log.d(TAG, "applyFilter: dayOfWeek is ${calender.get(GregorianCalendar.DAY_OF_WEEK)}")
            Log.d(TAG, "applyFilter: date is " + calender.time)

            calender.set(GregorianCalendar.DAY_OF_WEEK, weekStart)
            calender.set(GregorianCalendar.HOUR_OF_DAY, 0)
            calender.set(GregorianCalendar.MINUTE, 0)
            calender.set(GregorianCalendar.SECOND, 0)
            val startDate = calender.timeInMillis / 1000
            val start = dateFormat.format(startDate * 1000)
            // for end date move 6 days ahead
            calender.add(GregorianCalendar.DATE, 6)     // add 6 days to
            calender.set(GregorianCalendar.HOUR_OF_DAY, 23)
            calender.set(GregorianCalendar.MINUTE, 59)
            calender.set(GregorianCalendar.SECOND, 59)
            val endDate = calender.timeInMillis / 1000
            val end = dateFormat.format(endDate * 1000)

            selectionArgs = arrayOf(startDate.toString(), endDate.toString())
            Log.d(TAG, "applyFilter: for week(7) startDate is $startDate, endDate is $endDate")
            Log.d(TAG, "applyFilter: startDate: $start, endDate: $end")
        } else {
            // for single day -> re-query
            // for start of the day
            calender.set(GregorianCalendar.HOUR_OF_DAY, 0)
            calender.set(GregorianCalendar.MINUTE, 0)
            calender.set(GregorianCalendar.SECOND, 0)
            val startDate = calender.timeInMillis / 1000 // converting in sec
            val start = dateFormat.format(startDate * 1000) // date more readable format

            // for end of the day
            calender.set(GregorianCalendar.HOUR_OF_DAY, 23)
            calender.set(GregorianCalendar.MINUTE, 59)
            calender.set(GregorianCalendar.SECOND, 59)
            val endDate = calender.timeInMillis / 1000
            val end = dateFormat.format(endDate * 1000)

            // updating selection args
            selectionArgs = arrayOf(startDate.toString(), endDate.toString())
            Log.d(TAG, "applyFilter: for day(1): startDate is $startDate, endDate is $endDate")
            Log.d(TAG, "applyFilter: startDate: $start, endDate: $end")

        }

        // putting time as it was  before toggling
        calender.timeInMillis = currentCalenderTime
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

    fun setReportDate(year: Int, month: Int, dayOfMonth: Int) {
        //checking if the date has changed or not
        if (calender.get(GregorianCalendar.YEAR) != year
            || calender.get(GregorianCalendar.MONTH) != month
            || calender.get(GregorianCalendar.DAY_OF_MONTH) != dayOfMonth
        ) {
            calender.set(year, month, dayOfMonth, 0, 0, 0)
            applyFilter()
        }
    }
}