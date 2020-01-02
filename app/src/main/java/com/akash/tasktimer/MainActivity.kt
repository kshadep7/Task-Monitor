package com.akash.tasktimer

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

        val projection =
            arrayOf(TaskContract.Columns.TASK_NAME, TaskContract.Columns.TASK_SORT_ORDER)
        val sortOrder = TaskContract.Columns.TASK_SORT_ORDER

        val cursor = contentResolver.query(
            TaskContract.buildUriFromId(2),
            projection,
            null,
            null,
            sortOrder
        )

        Log.d(TAG, "**********************")

        cursor.use {
            if (it != null) {
                while (it.moveToNext()) {
                    with(it) {
                        //                        val id = getLong(0)
                        val name = getString(0)
//                        val description = getString(2)
                        val sortOrder = getLong(1)
                        val result =
                            "name: $name, sort order: $sortOrder"
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
