package com.akash.taskMonitor

import android.util.Log
import java.util.*

private const val TAG = "Timing"

class Timing(val taskId: Long, val startTime: Long = Date().time / 1000, var id: Long = 0) {

    var duration: Long = 0
        private set

    fun setDuration() {
        // calculating total duration = current time - start time
        duration = Date().time / 1000 - startTime
        Log.d(TAG, "setDuration: start time: $startTime, duration: $duration")
    }
}