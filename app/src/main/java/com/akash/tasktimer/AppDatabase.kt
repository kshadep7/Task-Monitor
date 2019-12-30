package com.akash.tasktimer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val TAG = "AppDatabase"
private const val DATABASE_NAME = "TaskMonitor.db"
private const val DATABASE_VERSION = 1

internal class AppDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    init {
        Log.d(TAG, "AppDatabase Initialize")
    }

    companion object : SingletonHolder<AppDatabase, Context>(::AppDatabase)

    override fun onCreate(db: SQLiteDatabase) {

        Log.d(TAG, "onCreate starts")
        val query = """CREATE TABLE ${TaskContract.TABLE_NAME}(
            |${TaskContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
            |${TaskContract.Columns.NAME} TEXT NOT NULL,
            |${TaskContract.Columns.DESCRIPTION} TEXT,
            |${TaskContract.Columns.SORT_ORDER} INTEGER
            |);""".trimMargin().replaceIndent(" ")
        Log.d(TAG, query)
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}