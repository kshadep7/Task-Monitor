package com.akash.tasktimer

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
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

//        try {
//            setContentView(R.layout.activity_main)
//        } catch (e: Exception) {
//            Log.d(TAG, "onCreate: Exception: ${e.printStackTrace()}")
//        }
        setSupportActionBar(toolbar)

        mTwoPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val fragment = supportFragmentManager.findFragmentById(R.id.task_detail_container)
        // There was an existing fragment to edit a task, make sure the panes are set correctly

        if (fragment != null) {
            showEditPane()
        } else {
            mainFragment.view?.visibility = View.VISIBLE
            task_detail_container.visibility = if (mTwoPane) View.INVISIBLE else View.GONE
        }

        Log.d(TAG, "onCreate: Ends")
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

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.task_detail_container)
        if (fragment == null || mTwoPane) {
            super.onBackPressed()
        } else {
            removeEditPane(fragment)
        }
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
        // to hide the keyboard after button pressed
        hideKeyboard(this)
    }

    // to hide the keyboard
    private fun hideKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
