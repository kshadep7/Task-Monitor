package com.akash.tasktimer

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

    private val databaseCursor = MutableLiveData<Cursor>()

    val cursor: LiveData<Cursor>
        get() = databaseCursor

    init {
        Log.d(TAG, "init: Android ViewModel Created")
        Log.d(TAG, "Registering Content Observer")
        getApplication<Application>().contentResolver.registerContentObserver(
            TaskContract.CONTENT_URI,
            true,
            contentObserver
        )
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
                        newTask.id = TaskContract.getId(uri);
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

    override fun onCleared() {
        Log.d(TAG, "Android view model onCleared called")
        Log.d(TAG, "onCleared: Unregistering Content Observer")

        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }

}