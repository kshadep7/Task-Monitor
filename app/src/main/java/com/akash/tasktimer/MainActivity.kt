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
private const val DIALOG_CANCEL_EDIT_ID = 1

class MainActivity : AppCompatActivity(),
    FragmentAddEdit.OnSaveClickListener,
    MainActivityFragment.OnTaskEdit,
    AppDialog.DialogEvents {
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
        val fragment = findFragmentById(R.id.task_detail_container)
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
                val fragment = findFragmentById(R.id.task_detail_container)
//                removeEditPane(fragment)

                if ((fragment is FragmentAddEdit) && fragment.isDirty()) {
                    showConfirmationDialog(
                        DIALOG_CANCEL_EDIT_ID,
                        getString(R.string.cancel_edit_dialog_message),
                        R.string.cancel_edit_dialog_positive_caption,
                        R.string.cancel_edit_dialog_negative_caption
                    )
                } else
                    removeEditPane(fragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragment = findFragmentById(R.id.task_detail_container)
        if (fragment == null || mTwoPane) {
            super.onBackPressed()
        } else {
//            removeEditPane(fragment)
            if ((fragment is FragmentAddEdit) && fragment.isDirty()) {
                showConfirmationDialog(
                    DIALOG_CANCEL_EDIT_ID,
                    getString(R.string.cancel_edit_dialog_message),
                    R.string.cancel_edit_dialog_positive_caption,
                    R.string.cancel_edit_dialog_negative_caption
                )
            } else
                removeEditPane(fragment)
        }
    }

    override fun onTaskEdit(task: Task) {
        Log.d(TAG, "onTaskEdit: $task")
        taskEditRequest(task)
    }

    private fun taskEditRequest(task: Task?) {
        Log.d(TAG, "taskEditRequest: Starts")

        // create a new fragment
        val newFragment = FragmentAddEdit.newInstance(task)
/*
        supportFragmentManager.beginTransaction()
            .replace(R.id.task_detail_container, newFragment)
            .commit()
*/
        // extention func
        replaceFragment(newFragment, R.id.task_detail_container)

        showEditPane()
        Log.d(TAG, "taskEditRequest: Ends")
    }

    override fun onSaveClicked() {
        Log.d(TAG, "onSaveClicked: start")
        removeEditPane(findFragmentById(R.id.task_detail_container))
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
/*
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
*/
            //using the extension function
            removeFragment(fragment)
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

    override fun onPositiveDialogResult(dialogId: Int, bundle: Bundle) {
        Log.d(
            TAG,
            "onPositiveDialogResult: canceling the editing without saving changes with dialogId: $dialogId"
        )
        if (dialogId == DIALOG_CANCEL_EDIT_ID) {
            removeEditPane(findFragmentById(R.id.task_detail_container))
        }
    }

    override fun onNegativeDialogResult(dialogId: Int, bundle: Bundle) {
        // do nothing
        // dismiss the dialog
    }

}
