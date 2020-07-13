package com.akash.taskMonitor.debug

import android.content.ContentResolver
import android.content.ContentValues
import com.akash.taskMonitor.singletons.TaskContract
import com.akash.taskMonitor.singletons.TimingContract
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

internal class TestTiming internal constructor(var taskId: Long, date: Long, var duration: Long) {

    var startTime: Long = 0

    init {
        this.startTime = date / 1000
    }
}

object TestData {

    private const val SEC_IN_DAY = 86400 // --> 24hrs x 60 min x 60 sec
    private const val UPPER_BOUND = 500
    private const val LOWER_BOUND = 100
    private const val MAX_DURATION = SEC_IN_DAY / 6 // --> 0 to 4 hrs

    fun genrateTestData(contentResolver: ContentResolver) {
        val projection = arrayOf(TaskContract.Columns.ID)
        val uri = TaskContract.CONTENT_URI
        val cursor = contentResolver.query(uri, projection, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val taskId = cursor.getLong(cursor.getColumnIndex(TaskContract.Columns.ID))

                val count = LOWER_BOUND + getRandomInt(UPPER_BOUND - LOWER_BOUND)
                for (i in 0 until count) {
                    // get random date and time
                    val randomDateAndTime = getRandomDateTime()

                    //get random duration
                    val randomDuration = getRandomInt(MAX_DURATION).toLong()

                    //creating new TestTiming object
                    val testTiming = TestTiming(taskId, randomDateAndTime, randomDuration)

                    //inserting testTiming record
                    saveCurrentTimings(contentResolver, testTiming)
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

    private fun getRandomInt(max: Int): Int {
        return (Math.random() * max).roundToInt()
    }

    private fun getRandomDateTime(): Long {
        val startYear = 2019
        val endYear = 2020

        val sec = getRandomInt(59)
        val min = getRandomInt(59)
        val hour = getRandomInt(23)
        val month = getRandomInt(11)
        val year = startYear + getRandomInt(endYear - startYear)

        val cal = GregorianCalendar(year, month, 1)
        val day = 1 + getRandomInt(cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - 1)

        cal.set(year, month, day, hour, min, sec)

        return cal.timeInMillis
    }

    private fun saveCurrentTimings(contentResolver: ContentResolver, currentTimings: TestTiming) {

        val values = ContentValues()
        values.put(TimingContract.Columns.TIMIMG_TASK_ID, currentTimings.taskId)
        values.put(TimingContract.Columns.TIMING_START_TIME, currentTimings.startTime)
        values.put(TimingContract.Columns.TIMIMG_DURATION, currentTimings.duration)

        GlobalScope.launch {
            contentResolver.insert(TimingContract.CONTENT_URI, values)
        }
    }
}