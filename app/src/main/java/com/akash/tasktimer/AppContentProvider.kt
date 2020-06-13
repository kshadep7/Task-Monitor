package com.akash.tasktimer

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
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

val CONTENT_AUTHORITY_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

class AppContentProvider : ContentProvider() {


    private val uriMatcher by lazy { buildUriMatcher() }

    private fun buildUriMatcher(): UriMatcher {
        Log.d(TAG, "buildUriMatcher: starts")
        val matcher = UriMatcher(UriMatcher.NO_MATCH)

        // for content://com.akash.taskmonitor.provider/tasks
        matcher.addURI(CONTENT_AUTHORITY, TaskContract.TABLE_NAME, TASKS)

        // for content://com.akash.taskmonitor.provider/tasks/4 --> specific task eg. running
        matcher.addURI(CONTENT_AUTHORITY, "${TaskContract.TABLE_NAME}/#", TASKS_ID)

        matcher.addURI(CONTENT_AUTHORITY, TimingContract.TABLE_NAME, TIMINGS)
        matcher.addURI(CONTENT_AUTHORITY, "${TimingContract.TABLE_NAME}/#", TIMINGS_ID)

        return matcher
    }


    override fun onCreate(): Boolean {
        Log.d(TAG, "onCreate: Start")
        return true
    }

    override fun getType(uri: Uri): String {

        val match = uriMatcher.match(uri)

        return when (match) {

            TASKS -> TaskContract.CONTENT_TYPE

            TASKS_ID -> TaskContract.CONTENT_ITEM_TYPE

            TIMINGS -> TimingContract.CONTENT_TYPE

            TIMINGS_ID -> TimingContract.CONTENT_ITEM_TYPE

            else -> throw IllegalArgumentException("Unknown uri : $uri")
        }
    }

    // Content resolver query function
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "query: called with uri: $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "query: match with $match")

        val queryBuilder = SQLiteQueryBuilder()

        when (match) {

            TASKS -> queryBuilder.tables = TaskContract.TABLE_NAME

            TASKS_ID -> {
                queryBuilder.tables = TaskContract.TABLE_NAME
                val taskId = TaskContract.getId(uri)
                queryBuilder.appendWhere("${TaskContract.Columns.ID} == ")
                queryBuilder.appendWhereEscapeString("$taskId")
            }

            TIMINGS -> queryBuilder.tables = TimingContract.TABLE_NAME

            TIMINGS_ID -> {
                queryBuilder.tables = TimingContract.TABLE_NAME
                val timingId = TimingContract.getId(uri)
                queryBuilder.appendWhere("${TimingContract.Columns.ID} ==")
                queryBuilder.appendWhereEscapeString("$timingId")
            }

            else -> throw IllegalArgumentException("Unknown Uri: $uri")
        }
        val db = AppDatabase.getInstance(context!!).readableDatabase
        val cursor =
            queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        Log.d(TAG, "query: cursor: $cursor")
        Log.d(TAG, "query: rows in returned cursor = ${cursor.count}")

        return cursor
    }
    // Content resolver insert function

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "insert called")

        Log.d(TAG, "insert: called with Uri: $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "insert: match with $match")

        val recordId: Long
        val returnUri: Uri


        when (match) {

            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                recordId = db.insert(TaskContract.TABLE_NAME, null, values)
                if (recordId != -1L) {
                    returnUri = TaskContract.buildUriFromId(recordId)
                } else {
                    throw SQLException("Failed to insert record, uri: $uri")
                }
            }

            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                recordId = db.insert(TimingContract.TABLE_NAME, null, values)
                if (recordId != -1L) {
                    returnUri = TimingContract.buildUriFromId(recordId)
                } else {
                    throw SQLException("Failed to insert record, uri: $uri")
                }
            }

            else -> throw IllegalArgumentException("Unknown uri $uri")

        }
        if (recordId > 0) {
            // something was inserted
            Log.d(TAG, "New Record inserted with uri: $uri")
            context?.contentResolver?.notifyChange(uri, null)
        }

        Log.d(TAG, "Exiting insert: returning uri: $returnUri")
        return returnUri

    }

    // Content resolver update function
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {

        Log.d(TAG, "update: called with Uri: $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "update: match with $match")

        val count: Int
        var selectionCriteria: String

        when (match) {

            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.update(TaskContract.TABLE_NAME, values, selection, selectionArgs)
            }

            TASKS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TaskContract.getId(uri)

                selectionCriteria = "${TaskContract.Columns.ID} == $id"

                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }

                count = db.update(TaskContract.TABLE_NAME, values, selectionCriteria, selectionArgs)

            }

            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.update(TimingContract.TABLE_NAME, values, selection, selectionArgs)
            }

            TIMINGS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TimingContract.getId(uri)

                selectionCriteria = "${TimingContract.Columns.ID} == $id"

                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }

                count =
                    db.update(TimingContract.TABLE_NAME, values, selectionCriteria, selectionArgs)

            }

            else -> throw IllegalArgumentException("Unknown uri $uri")

        }

        if (count > 0) {
            // record has been updated
            Log.d(TAG, " Record has been updated of uri: $uri")
            context?.contentResolver?.notifyChange(uri, null)
        }

        Log.d(TAG, "Exiting update: returning count: $count")
        return count
    }

    // Content resolver delete function
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "delete: called with Uri: $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "delete: match with $match")

        val count: Int
        var selectionCriteria: String

        when (match) {

            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.delete(TaskContract.TABLE_NAME, selection, selectionArgs)
            }

            TASKS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TaskContract.getId(uri)

                selectionCriteria = "${TaskContract.Columns.ID} == $id"

                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }

                count = db.delete(TaskContract.TABLE_NAME, selectionCriteria, selectionArgs)

            }

            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.delete(TimingContract.TABLE_NAME, selection, selectionArgs)
            }

            TIMINGS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TimingContract.getId(uri)

                selectionCriteria = "${TimingContract.Columns.ID} == $id"

                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }

                count = db.delete(TimingContract.TABLE_NAME, selectionCriteria, selectionArgs)

            }

            else -> throw IllegalArgumentException("Unknown uri $uri")

        }
        if (count > 0) {
            // record has been updated
            Log.d(TAG, " Record has been deleted of uri: $uri")
            context?.contentResolver?.notifyChange(uri, null)
        }

        Log.d(TAG, "Exiting delete: returning count: $count")
        return count
    }

}