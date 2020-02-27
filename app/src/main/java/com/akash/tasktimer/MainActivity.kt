package com.akash.tasktimer

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), FragmentAddEdit.OnSaveClickListener {
    private var mTwoPane = false // for checking if screen is in landscape mode or in tablet screen.
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate Start")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mTwoPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val fragment = supportFragmentManager.findFragmentById(R.id.task_detail_container)
        if (fragment != null) {
            showEditPane()
        } else {
            mainFragment.view?.visibility = View.VISIBLE
            task_detail_container.visibility = if (mTwoPane) View.INVISIBLE else View.GONE
        }

        Log.d(TAG, "onCreate Ends")

/*
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
*/

    }

    private fun showEditPane() {
        //the edit task frame layout exits, so show it
        task_detail_container.visibility = View.VISIBLE
        // check orientation and show the main fragment (list of tasks)
        mainFragment.view?.visibility = if (mTwoPane) View.VISIBLE else View.GONE

    }

    private fun removeEditPane(fragment: Fragment? = null) {
        Log.d(TAG, "removeEditPane: starts")

        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
        //setting the visibility of right pane (frame layout)
        task_detail_container.visibility = if (mTwoPane) View.INVISIBLE else View.GONE
        //show the left pane --> (main fragment)
        mainFragment.view?.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu: Start")
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: Start")
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
//            R.id.menumain_settings -> true
            R.id.mainmenu_addTask -> taskEditRequest(null)
//            else -> super.onOptionsItemSelected(item)
            android.R.id.home -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.task_detail_container)
                removeEditPane(fragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun taskEditRequest(task: Task?) {
        Log.d(TAG, "taskEditRequest: Starts")

        // create a new fragment
        val newFragment = FragmentAddEdit.newInstance(task)
        supportFragmentManager.beginTransaction()
            .replace(R.id.task_detail_container, newFragment)
            .commit()

        showEditPane()
        Log.d(TAG, "taskEditRequest: Ends")
    }

    override fun onSaveClicked() {
        Log.d(TAG, "onSaveClicked: start")
        val fragment = supportFragmentManager.findFragmentById(R.id.task_detail_container)
        removeEditPane(fragment)
    }
}
/*
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
*/
