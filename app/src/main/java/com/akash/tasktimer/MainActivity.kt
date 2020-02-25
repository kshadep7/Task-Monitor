package com.akash.tasktimer

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate Start")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        val appDatabase = AppDatabase.getInstance(this)
//        val db = appDatabase.readableDatabase
//        val cursor = db.rawQuery("SELECT * FROM task", null)

//        testInsert()
//        testUpdate()
        testDelete()
        val projection =
            arrayOf(TaskContract.Columns.TASK_NAME, TaskContract.Columns.TASK_SORT_ORDER)
        val sortOrder = TaskContract.Columns.TASK_SORT_ORDER

        val cursor = contentResolver.query(
            TaskContract.CONTENT_URI,
            null,
            null,
            null,
            sortOrder
        )
//        val cursor = contentResolver.query(
//            TaskContract.buildUriFromId(2),
//            projection,
//            null,
//            null,
//            sortOrder
//        )

        Log.d(TAG, "**********************")

        cursor.use {
            if (it != null) {
                while (it.moveToNext()) {
                    with(it) {
                        val id = getLong(0)
                        val name = getString(1)
                        val description = getString(2)
                        val sortOrder = getLong(3)
                        val result = """
                            ID: $id
                            Name: $name
                            Description: $description
                            Sort Order: $sortOrder
                        """.trimIndent()
                        Log.d(TAG, "onCreate: reading data: $result")
                    }
                }
            }
        }

        Log.d(TAG, "**********************")

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        Log.d(TAG, "onCreate Ends")
    }

    private fun testDelete() {

// to delete only one record
//        val taskUri = TaskContract.buildUriFromId(3)
// to delete multiple records
        val selection =
            TaskContract.Columns.TASK_SORT_ORDER + " == 3" // --> where clause ("sortorder == 3")
        val rowsAffected = contentResolver.delete(TaskContract.CONTENT_URI, selection, null)
        Log.d(TAG, "Testing delete: rows deleted --> $rowsAffected")
    }


    private fun testUpdate() {

        val values = ContentValues().apply {
            put(TaskContract.Columns.TASK_NAME, "Leetcode")
            put(TaskContract.Columns.TASK_DESCRIPTION, "Solving problems")
        }

// to delete only one record
//        val taskUri = TaskContract.buildUriFromId(4)
// to delete multiple records
        val selection =
            TaskContract.Columns.TASK_SORT_ORDER + " == ?" // --> where clause ("sortorder == 3")
        val selectionArgs = arrayOf("3") // mainly used to prevent SQL injections
        // The ? from selection checks the each values from selectionArgs array

        val rowsAffected =
            contentResolver.update(TaskContract.CONTENT_URI, values, selection, selectionArgs)
        Log.d(TAG, "Testing update: rows Affected --> $rowsAffected")
    }

    private fun testInsert() {

        val values = ContentValues().apply {
            put(TaskContract.Columns.TASK_NAME, "New Task 1")
            put(TaskContract.Columns.TASK_DESCRIPTION, "New Description")
            put(TaskContract.Columns.TASK_SORT_ORDER, 3)
        }

        val uri = contentResolver.insert(TaskContract.CONTENT_URI, values)
        Log.d(TAG, "Test Insertion with uri $uri")
        Log.d(TAG, "task inserted with id ${TaskContract.getId(uri!!)}")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menumain_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
