package com.akash.taskMonitor

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val TAG = "AppDatabase"
private const val DATABASE_NAME = "TaskMonitor.db"
private const val DATABASE_VERSION = 2

internal class AppDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    init {
        Log.d(TAG, "AppDatabase Initialize")
    }

    companion object : SingletonHolder<AppDatabase, Context>(::AppDatabase)

    override fun onCreate(db: SQLiteDatabase) {

        Log.d(TAG, "onCreate starts")
        // for task table
        val query = """CREATE TABLE ${TaskContract.TABLE_NAME}(
            |${TaskContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
            |${TaskContract.Columns.TASK_NAME} TEXT NOT NULL,
            |${TaskContract.Columns.TASK_DESCRIPTION} TEXT,
            |${TaskContract.Columns.TASK_SORT_ORDER} INTEGER
            |);""".trimMargin().replaceIndent(" ")
        Log.d(TAG, query)
        db.execSQL(query)

        addTimingsTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> addTimingsTable(db)
            else -> throw IllegalStateException("onUpgrade() failed due to unknown newVersion: $newVersion")
        }
    }

    private fun addTimingsTable(db: SQLiteDatabase) {
        // for timings table
        val queryTimings = """CREATE TABLE ${TimingContract.TABLE_NAME}(
            |${TimingContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
            |${TimingContract.Columns.TIMIMG_TASK_ID} TEXT NOT NULL,
            |${TimingContract.Columns.TIMING_START_TIME} TEXT,
            |${TimingContract.Columns.TIMIMG_DURATION} INTEGER
            |);""".trimMargin().replaceIndent(" ")
        Log.d(TAG, queryTimings)
        db.execSQL(queryTimings)

        // as I have not used auto increment to generate IDs (expensive overheads)
        // the deleted task IDs can be used and asscociated to new timings records
        // so it might create problems
        // instead, we can create a trigger so as to compensate not using auto increament

        val sqlTrigger = """CREATE TRIGGER remove_task
            |AFTER DELETE ON ${TaskContract.TABLE_NAME}
            |FOR EACH ROW
            |BEGIN
            |DELETE FROM ${TimingContract.TABLE_NAME}
            |WHERE ${TimingContract.Columns.TIMIMG_TASK_ID} = OLD.${TaskContract.Columns.ID};
            |END;
        """.trimMargin().replaceIndent(" ")
        Log.d(TAG, sqlTrigger)
        db.execSQL(sqlTrigger)
    }
}