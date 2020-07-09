package com.akash.taskMonitor

import android.app.Application
import android.content.ContentValues
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "TaskTimerVIewModel"

class TaskTimerViewModel(application: Application) : AndroidViewModel(application) {

    private val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            Log.d(TAG, "onChange: content observer called with uri: $uri")
            loadTasks()
        }
    }

    private var currentTiming: Timing? = null
    private val databaseCursor = MutableLiveData<Cursor>()
    private val taskTiming = MutableLiveData<String>()

    val cursor: LiveData<Cursor>
        get() = databaseCursor

    val timing: LiveData<String>
        get() = taskTiming

    init {
        Log.d(TAG, "init: Android ViewModel Created")
        Log.d(TAG, "Registering Content Observer")
        getApplication<Application>().contentResolver.registerContentObserver(
            TaskContract.CONTENT_URI,
            true,
            contentObserver
        )
        currentTiming = retrieveCurrentTimings()
        loadTasks()
    }


    private fun loadTasks() {
        Log.d(TAG, "loadTasks: Loading tasks")

        val projection = arrayOf(
            TaskContract.Columns.ID,
            TaskContract.Columns.TASK_NAME,
            TaskContract.Columns.TASK_DESCRIPTION,
            TaskContract.Columns.TASK_SORT_ORDER
        )
        // order by selection criteria
        val sortOrder = "${TaskContract.Columns.TASK_SORT_ORDER}, ${TaskContract.Columns.TASK_NAME}"

        /** get cursor of a particular task by calling [AppContentProvider.query] */
        GlobalScope.launch {
            val cursor = getApplication<Application>().contentResolver.query(
                TaskContract.CONTENT_URI,
                projection, null, null, sortOrder
            )
            databaseCursor.postValue(cursor)
        }
    }

    fun deleteTask(taskId: Long) {
        /** get cursor to delete of a particular task by calling [AppContentProvider.delete] */
        Log.d(TAG, "deleteTask: deleting task using different thread")
        GlobalScope.launch {
            getApplication<Application>().contentResolver.delete(
                TaskContract.buildUriFromId(taskId),
                null,
                null
            )
        }
    }

    fun saveTake(newTask: Task): Task? {
        Log.d(TAG, "saveTake: ViewModels saveTask method-->")
        val values = ContentValues()

        // Not saving a task record without name
        if (newTask.name.isNotEmpty()) {
            values.put(TaskContract.Columns.TASK_NAME, newTask.name)
            values.put(TaskContract.Columns.TASK_DESCRIPTION, newTask.description)
            values.put(TaskContract.Columns.TASK_SORT_ORDER, newTask.sortOrder)
            // if task id is 0 then create new task record
            if (newTask.id == 0L) {
                GlobalScope.launch {
                    val uri = getApplication<Application>().contentResolver.insert(
                        TaskContract.CONTENT_URI,
                        values
                    )
                    if (uri != null) {
                        newTask.id = TaskContract.getId(uri)
                        Log.d(TAG, "saveTake: new task record created with id: ${newTask.id}")
                    }
                }
            } else {
                // updating the task record
                GlobalScope.launch {
                    Log.d(TAG, "saveTake: updating task record")
                    getApplication<Application>().contentResolver.update(
                        TaskContract.buildUriFromId(newTask.id),
                        values, null, null
                    )
                }
            }
        }
        return newTask
    }

    fun timeTask(task: Task) {
        Log.d(TAG, "timeTask: called")
        //smart casting
        val timingRecord = currentTiming

        //check if its a new task or existing task
        if (timingRecord == null) {
            // no task being timed, so time this task
            currentTiming = Timing(task.id)
            saveTiming(currentTiming!!)
        } else {
            // task is being timed, so save it
            timingRecord.setDuration()
            saveTiming(timingRecord)
            // if same task is clicked then stop timing that task
            // else update that record
            currentTiming = if (task.id == timingRecord.taskId) {
                null
            } else {
                // instead of using !! on currTimnig --> saveTiming(currtiming!!)
                val newTiming = Timing(task.id)
                saveTiming(newTiming)
                newTiming
            }
        }

        //updating the live data of taskTiming
        taskTiming.value = if (currentTiming != null) task.name else null
    }

    private fun saveTiming(currentTiming: Timing) {
        Log.d(TAG, "saveTiming: called")
        //check if new task is inserted or existed task is updated
        val isInserting = currentTiming.duration == 0L

        // add values
        val values = ContentValues().apply {
            if (isInserting) {
                put(TimingContract.Columns.TIMIMG_TASK_ID, currentTiming.taskId)
                put(TimingContract.Columns.TIMING_START_TIME, currentTiming.startTime)
            }
            put(TimingContract.Columns.TIMIMG_DURATION, currentTiming.duration)
        }
        GlobalScope.launch {
            if (isInserting) {
                val uri = getApplication<Application>().contentResolver.insert(
                    TimingContract.CONTENT_URI,
                    values
                )
                if (uri != null)
                    currentTiming.id = TimingContract.getId(uri)
            } else {
                getApplication<Application>().contentResolver.update(
                    TimingContract.buildUriFromId(currentTiming.id),
                    values, null, null
                )
            }
        }
    }

    private fun retrieveCurrentTimings(): Timing? {
        Log.d(TAG, "retrieveCurrentTimings: starts")
        val timing: Timing?

        val timingCursor = getApplication<Application>().contentResolver.query(
            CurrentTimingContract.CONTENT_URI,
            null, null, null, null
        )

        if (timingCursor != null && timingCursor.moveToFirst()) {
            val id =
                timingCursor.getLong(timingCursor.getColumnIndex(CurrentTimingContract.Columns.TIMIMG_ID))
            val startTime =
                timingCursor.getLong(timingCursor.getColumnIndex(CurrentTimingContract.Columns.TIMING_START_TIME))
            val taskId =
                timingCursor.getLong(timingCursor.getColumnIndex(CurrentTimingContract.Columns.TIMING_TASK_ID))
            val taskName =
                timingCursor.getString(timingCursor.getColumnIndex(CurrentTimingContract.Columns.TASK_NAME))
            timing = Timing(taskId, startTime, id)
            // updating the UI with the current task name being timed
            taskTiming.value = taskName
        } else {
            // no timings record with zero duration
            timing = null
        }
        timingCursor?.close()
        return timing
    }

    override fun onCleared() {
        Log.d(TAG, "Android view model onCleared called")
        Log.d(TAG, "onCleared: Unregistering Content Observer")

        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }

}