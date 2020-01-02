package com.akash.tasktimer

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

/**
 * This is only class which talks to class [AppDatabase]
 * */


private const val TAG = "AppContentProvider"

private const val CONTENT_AUTHORITY = "com.akash.tasktimer.provider"

private const val TASKS = 100
private const val TASKS_ID = 101

private const val TIMINGS = 200
private const val TIMINGS_ID = 201

private const val TASK_DURATIONS = 400
private const val TASK_DURATIONS_ID = 401

val CONTENT_AUTHORITY_URI = Uri.parse("content://$CONTENT_AUTHORITY")

class AppContentProvider : ContentProvider() {


    private val uriMatcher by lazy { buildUriMatcher() }

    private fun buildUriMatcher(): UriMatcher {
        Log.d(TAG, "buildUriMatcher: starts")
        val matcher = UriMatcher(UriMatcher.NO_MATCH)

        // for content://com.akash.taskmonitor.provider/tasks
        matcher.addURI(CONTENT_AUTHORITY, TaskContract.TABLE_NAME, TASKS)

        // for content://com.akash.taskmonitor.provider/tasks/4 --> specific task eg. running
        matcher.addURI(CONTENT_AUTHORITY, "${TaskContract.TABLE_NAME}/#", TASKS_ID)

        return matcher
    }


    override fun onCreate(): Boolean {
        Log.d(TAG, "onCreate: Start")
        return true
    }

    override fun getType(uri: Uri): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "query: called with Uri: $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "query: match with $match")

        val queryBuilder = SQLiteQueryBuilder()

        when (match) {

            TASKS -> queryBuilder.tables = TaskContract.TABLE_NAME

            TASKS_ID -> {
                queryBuilder.tables = TaskContract.TABLE_NAME
                val taskId = TaskContract.getId(uri)
                queryBuilder.appendWhere("${TaskContract.Columns.TASK_ID} == ")
                queryBuilder.appendWhereEscapeString("$taskId")
            }

            else -> throw IllegalArgumentException("Unknown Uri: $uri")
        }
        val db = AppDatabase.getInstance(context!!).readableDatabase
        val cursor =
            queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        Log.d(TAG, "query: rows in returned cursor = ${cursor.count}")

        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "insert called")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}