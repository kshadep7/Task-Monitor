package com.akash.taskMonitor

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val TAG = "AppDatabase"
private const val DATABASE_NAME = "TaskMonitor.db"
private const val DATABASE_VERSION = 3

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
        addViewCurrentTimings(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> {
                addTimingsTable(db)
                addViewCurrentTimings(db)
            }
            2 -> addViewCurrentTimings(db)
            else ->
                throw IllegalStateException("onUpgrade() failed due to unknown newVersion: $newVersion")
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

    private fun addViewCurrentTimings(db: SQLiteDatabase) {
        // for current timings view
        /*
        Actual Query for creating a view -->
        * CREATE VIEW viewCurrentTimings
        *   AS
        *   SELECT  timings._id,
        *           timings.taskId,
        *           timings.startTime,
        *           task.name
        *   FROM timings
        *   JOIN task
        *   ON timings.taskId == task._id
        *   WHERE timings.duration = 0
        *   ORDER BY timings.startTime DESC
        */

        /*
        * val sSQLTimingView = """CREATE VIEW ${CurrentTimingContract.TABLE_NAME}
        AS SELECT ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.ID},
            ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_TASK_ID},
            ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_START_TIME},
            ${TasksContract.TABLE_NAME}.${TasksContract.Columns.TASK_NAME}
        FROM ${TimingsContract.TABLE_NAME}
        JOIN ${TasksContract.TABLE_NAME}
        ON ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_TASK_ID} = ${TasksContract.TABLE_NAME}.${TasksContract.Columns.ID}
        WHERE ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_DURATION} = 0
        ORDER BY ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_START_TIME} DESC;
        * */
        val viewCurrentTimings = """CREATE VIEW ${CurrentTimingContract.TABLE_NAME}
            |AS SELECT ${TimingContract.TABLE_NAME}.${TimingContract.Columns.ID},
            |       ${TimingContract.TABLE_NAME}.${TimingContract.Columns.TIMIMG_TASK_ID},
            |       ${TimingContract.TABLE_NAME}.${TimingContract.Columns.TIMING_START_TIME},
            |       ${TaskContract.TABLE_NAME}.${TaskContract.Columns.TASK_NAME}
            |FROM   ${TimingContract.TABLE_NAME} 
            |JOIN   ${TaskContract.TABLE_NAME}
            |ON     ${TimingContract.TABLE_NAME}.${TimingContract.Columns.TIMIMG_TASK_ID} = ${TaskContract.TABLE_NAME}.${TaskContract.Columns.ID}
            |WHERE ${TimingContract.TABLE_NAME}.${TimingContract.Columns.TIMIMG_DURATION} = 0
            |ORDER BY ${TimingContract.TABLE_NAME}.${TimingContract.Columns.TIMING_START_TIME} DESC;
        """.trimMargin().replaceIndent(" ")

        Log.d(TAG, "addViewCurrentTimings: $viewCurrentTimings")
        db.execSQL(viewCurrentTimings)
    }
}