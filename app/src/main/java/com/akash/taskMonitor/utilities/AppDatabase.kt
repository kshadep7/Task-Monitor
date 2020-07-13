package com.akash.taskMonitor.utilities

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.akash.taskMonitor.singletons.CurrentTimingContract
import com.akash.taskMonitor.singletons.TaskContract
import com.akash.taskMonitor.singletons.TaskDurationsContract
import com.akash.taskMonitor.singletons.TimingContract

private const val TAG = "AppDatabase"
private const val DATABASE_NAME = "TaskMonitor.db"
private const val DATABASE_VERSION = 4

internal class AppDatabase private constructor(context: Context) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME, null,
        DATABASE_VERSION
    ) {

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
        addViewTaskDurations(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> {
                addTimingsTable(db)
                addViewCurrentTimings(db)
                addViewTaskDurations(db)
            }
            2 -> {
                addViewCurrentTimings(db)
                addViewTaskDurations(db)
            }
            3 -> {
                addViewTaskDurations(db)
            }
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

    private fun addViewTaskDurations(db: SQLiteDatabase) {
        /*
        CREATE VIEW viewTaskDurations AS
        SELECT
                task.name,
                task.description,
                Timings.startTime,
                DATE(Timings.startTime, 'unixepoch', 'localtime') AS startDate,
                SUM(Timings.duration) AS duration
        FROM task INNER JOIN Timings
        ON task._id == Timings.taskId
        GROUP BY task._id, startDate
        */

        val viewTaskDuratiosns = """CREATE VIEW ${TaskDurationsContract.TABLE_NAME} AS
            |SELECT
            |       ${TaskContract.TABLE_NAME}.${TaskContract.Columns.TASK_NAME},
            |       ${TaskContract.TABLE_NAME}.${TaskContract.Columns.TASK_DESCRIPTION},
            |       ${TimingContract.TABLE_NAME}.${TimingContract.Columns.TIMING_START_TIME},
            |       DATE(${TimingContract.TABLE_NAME}.${TimingContract.Columns.TIMING_START_TIME}, 'unixepoch', 'localtime') AS ${TaskDurationsContract.Columns.START_DATE},
            |       SUM(${TimingContract.TABLE_NAME}.${TimingContract.Columns.TIMIMG_DURATION}) AS ${TaskDurationsContract.Columns.DURATION}
            |FROM ${TaskContract.TABLE_NAME} INNER JOIN ${TimingContract.TABLE_NAME}
            |ON ${TaskContract.TABLE_NAME}.${TaskContract.Columns.ID} == ${TimingContract.TABLE_NAME}.${TimingContract.Columns.TIMIMG_TASK_ID}
            |GROUP BY ${TaskContract.TABLE_NAME}.${TaskContract.Columns.ID}, ${TaskDurationsContract.Columns.START_DATE};
        """.trimMargin().replaceIndent(" ")

        Log.d(TAG, "addViewTaskDurations: $viewTaskDuratiosns")
        db.execSQL(viewTaskDuratiosns)
    }
}