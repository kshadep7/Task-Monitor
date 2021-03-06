package com.akash.taskMonitor.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.akash.taskMonitor.BuildConfig
import com.akash.taskMonitor.R
import com.akash.taskMonitor.debug.TestData
import com.akash.taskMonitor.fragments.FragmentAddEdit
import com.akash.taskMonitor.fragments.MainActivityFragment
import com.akash.taskMonitor.models.Task
import com.akash.taskMonitor.utilities.*
import com.akash.taskMonitor.viewModels.TaskTimerViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_main.*

private const val TAG = "MainActivity"
private const val DIALOG_CANCEL_EDIT_ID = 1

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),
    FragmentAddEdit.OnSaveClickListener,
    MainActivityFragment.OnTaskEdit,
    AppDialog.DialogEvents {
    private var mTwoPane = false // for checking if screen is in landscape mode or in tablet screen.

    // To respect the scope of activity to avoid memory leaks
    // remember to remove any references of aboutDialog in onStop()
    // eg. Orientation change
    private var aboutDialog: AlertDialog? = null

    private val viewModel by lazy {
        ViewModelProvider(this).get(TaskTimerViewModel::class.java)
    }

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

        viewModel.timing.observe(this, Observer { timing ->
            currentTask.text = if (timing != null) {
                getString(R.string.current_timing_task, timing)
            } else {
                getString(R.string.no_task_message)
            }
        })
        Log.d(TAG, "onCreate: Ends")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu: Start")
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        // generate data menu item will only show in debug version of the app.
        if (BuildConfig.DEBUG) {
            val generateData = menu.findItem(R.id.mainmenu_generate)
            generateData.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: Start")
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menumain_settings -> {
                val dialog = SettingDialog()
                dialog.show(supportFragmentManager, null)
            }
            R.id.mainmenu_addTask -> taskEditRequest(null)
            R.id.mainmenu_showDurations -> startActivity(
                Intent(
                    this,
                    DurationReportActivity::class.java
                )
            )
//            else -> super.onOptionsItemSelected(item)
            R.id.mainmenu_about -> showAboutDialog()
            R.id.mainmenu_generate -> TestData.genrateTestData(contentResolver)
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
                supportActionBar?.setTitle(getString(R.string.action_bar_all_tasks_title))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAboutDialog() {
        val aboutView = layoutInflater.inflate(R.layout.about, null, false)
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(R.string.app_name)
        alertBuilder.setIcon(R.mipmap.ic_launcher)

        aboutDialog = alertBuilder.setView(aboutView).create()
        aboutDialog?.setCanceledOnTouchOutside(true)


        // not using directly using sythetic import for about_version textview
        // it doesnt work in alert dialogs
        val aboutVersion = aboutView.findViewById<TextView>(R.id.about_version)
        //setting the version name
//        about_version.text = BuildConfig.VERSION_NAME --> Doesn't work!!!
        aboutVersion.text = BuildConfig.VERSION_NAME
        val weburl: TextView? = aboutView.findViewById(R.id.about_url)
        // Adding click listner to the web url for backward compability
        // Using nullable weburl as it might not be present for API 29 and higher
        weburl?.setOnClickListener { v ->
            val intent = Intent(Intent.ACTION_VIEW)
            val url = (v as TextView).text.toString()
            intent.data = Uri.parse(url)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    R.string.no_application_found_err_msg, Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        aboutDialog?.show()
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
        supportActionBar?.title = getString(R.string.action_bar_all_tasks_title)
    }

    override fun onTaskEdit(task: Task) {
        Log.d(TAG, "onTaskEdit: $task")
        taskEditRequest(task)
    }

    private fun taskEditRequest(task: Task?) {
        Log.d(TAG, "taskEditRequest: Starts")

        // create a new fragment
        val newFragment =
            FragmentAddEdit.newInstance(task)
/*
        supportFragmentManager.beginTransaction()
            .replace(R.id.task_detail_container, newFragment)
            .commit()
*/
        // extension func
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

    override fun onStop() {
        super.onStop()
        if (aboutDialog?.isShowing == true)
            aboutDialog?.dismiss()
    }
}
