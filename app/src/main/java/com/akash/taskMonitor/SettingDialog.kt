package com.akash.taskMonitor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.settings_dialog.*
import java.util.*

private const val TAG = "SettingDialog"
const val SETTING_FIRST_DAY_OF_WEEK = "firstDay"
const val SETTING_IGNORE_LESS_THAN = "ignoreLessThan"
const val SETTING_DEFAULT_IGNORE_LESS_THAN = 0

// indexs -> 0 - 24: 60(1 min), 600(10 min), 900(20 min), 1800(40 min), 2700(1 hr)
private val deltas = intArrayOf(
    0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 120, 180, 240, 300, 360, 420, 480, 540, 600,
    900, 1800, 2700
)

class SettingDialog : AppCompatDialogFragment() {

    private val defaultFirstDayOfWeek = GregorianCalendar(Locale.getDefault()).firstDayOfWeek
    private var firstDay = defaultFirstDayOfWeek
    private var ignoreLessThan = SETTING_DEFAULT_IGNORE_LESS_THAN

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: inflating the settings dialog")
        return inflater.inflate(R.layout.settings_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: setting dailog is inflated")
        super.onViewCreated(view, savedInstanceState)

        //setting click listners for ok and cancel buttons
        buttonOk.setOnClickListener {
            saveValues()
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        readValues()
        firstDaySpinner.setSelection(firstDay - GregorianCalendar.SUNDAY)
        //converting the seconds value to index values of deltas array
        val seekBarValue = deltas.binarySearch(ignoreLessThan)
        if (seekBarValue < 0)
            throw IndexOutOfBoundsException("No $seekBarValue present in the deltas array")
        ignoreSeconds.max = deltas.size - 1
        ignoreSeconds.progress = seekBarValue
        Log.d(TAG, "onViewStateRestored: Seekbar value set to $seekBarValue")

        if (ignoreLessThan < 60) {
            ignoreSecondsTitle.text = getString(
                R.string.settingsIgnoreSecondsTitle, ignoreLessThan,
                resources.getQuantityString(R.plurals.settingsLittleUnits, ignoreLessThan)
            )
        } else {
            val min = ignoreLessThan / 60
            ignoreSecondsTitle.text = getString(
                R.string.settingsIgnoreSecondsTitle, min,
                resources.getQuantityString(R.plurals.settingsBigUnits, min)
            )
        }
    }

    private fun readValues() {
        Log.d(TAG, "readValues: reading the saved Settings")
        with(PreferenceManager.getDefaultSharedPreferences(context)) {
            firstDay = getInt(SETTING_FIRST_DAY_OF_WEEK, defaultFirstDayOfWeek)
            ignoreLessThan = getInt(SETTING_IGNORE_LESS_THAN, SETTING_DEFAULT_IGNORE_LESS_THAN)
        }
        Log.d(
            TAG,
            "readValues: retrived values: firstDay: $firstDay, ignoreLessThan: $ignoreLessThan"
        )
    }

    private fun saveValues() {
        Log.d(TAG, "saveValues: saving setting")
        val newFirstDay = firstDaySpinner.selectedItemPosition + GregorianCalendar.SUNDAY
        // saving the actual seconds using seekbar index value from deltas array
        val newIgnoreLessThan = deltas[ignoreSeconds.progress]

        Log.d(TAG, "saveValues: first day: $newFirstDay and ignore less than: $newIgnoreLessThan")
        with(PreferenceManager.getDefaultSharedPreferences(context).edit()) {
            if (newFirstDay != firstDay)
                putInt(SETTING_FIRST_DAY_OF_WEEK, newFirstDay)
            if (newIgnoreLessThan != ignoreLessThan)
                putInt(SETTING_IGNORE_LESS_THAN, newIgnoreLessThan)

            apply()
        }
    }
}