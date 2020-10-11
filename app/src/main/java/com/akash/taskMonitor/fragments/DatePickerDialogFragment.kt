package com.akash.taskMonitor.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatDialogFragment
import java.util.*

private const val TAG = "DatePickerDialogFragmen"

const val DATE_PICKER_ID = "id"
const val DATE_PICKER_TITLE = "title"
const val DATE_PICKER_DATE = "date"

class DatePickerDialogFragment : AppCompatDialogFragment(), DatePickerDialog.OnDateSetListener {

    private var dialogId = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        //using the current date when dialog gets created
        val cal = GregorianCalendar()
        var title: String? = null

        //smart cast of arguments from bundle
        val arguments = getArguments()
        if (arguments != null) {
            dialogId = arguments.getInt(DATE_PICKER_ID)
            title = arguments.getString(DATE_PICKER_TITLE)

            // get already set date from arguments
            val givenDate = arguments.getSerializable(DATE_PICKER_DATE) as Date?
            if (givenDate != null) {
                cal.time = givenDate
                Log.d(TAG, "onCreateDialog: passed date: $givenDate")
            }
        }

        val year = cal.get(GregorianCalendar.YEAR)
        val month = cal.get(GregorianCalendar.MONTH)
        val day = cal.get(GregorianCalendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
        if (title != null)
            datePickerDialog.setTitle(title)

        return datePickerDialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context !is DatePickerDialog.OnDateSetListener)
            throw ClassCastException("$context muct implement DatePickerDialog.OnDateSetListner interface")
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        Log.d(TAG, "onDateSet: Entered")
        // when dialog is created specific to context i.e. which activity has requested to
        // use this dialog fragment -> setting the same id to current date picker view
        view.tag = dialogId
        // updating the date picker listener with new values of year, month, and year
        (context as DatePickerDialog.OnDateSetListener).onDateSet(view, year, month, dayOfMonth)
    }
}