package com.akash.taskMonitor.utilities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment
import com.akash.taskMonitor.R

private const val TAG = "AppDialog"

const val DIALOG_ID = "id"
const val DIALOG_MSG = "message"
const val DIALOG_POSITIVE_RID = "positive_rid"
const val DIALOG_NEGATIVE_RID = "negative_rid"

class AppDialog : AppCompatDialogFragment() {
    private var dialogEvents: DialogEvents? = null

    internal interface DialogEvents {
        fun onPositiveDialogResult(dialogId: Int, bundle: Bundle)
        fun onNegativeDialogResult(dialogId: Int, bundle: Bundle)
//        fun onDialogCancelled(dialogId: Int)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: fragment attached with context: $context")
        super.onAttach(context)

        // Activities/fragments containing this dialog fragment show implement callbacks of interface
        dialogEvents = try {
            // check for parent fragment presence if yes -->
            parentFragment as DialogEvents
        } catch (e: TypeCastException) {
            try {
                // if no parent fragment, then parent activity of this fragment dialog should
                // implement the callbacks
                context as DialogEvents
            } catch (e: ClassCastException) {
                throw ClassCastException("Activity $context must implement [AppDialog.DialogEvents] interface")
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("Fragment $parentFragment must implement [AppDialog.DialogEvents] interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        Log.d(TAG, "onCreateDialog: creating a new dialog")
        val builder = AlertDialog.Builder(context)

        // bundle cannot be smart casted
        // smart casting "arguments" as it is mutable and sent from bundle sent to create the dialog

        val arguments = arguments
        val dialogId: Int
        val dialogMessage: String?
        var dialogPositiveStringId: Int
        var dialogNegativeStringId: Int

        if (arguments != null) {
            dialogId = arguments.getInt(DIALOG_ID)
            dialogMessage = arguments.getString(DIALOG_MSG)

            if (dialogId == 0 || dialogMessage == null) {
                throw IllegalArgumentException("Dialog ID and/or Dialog message are not present in the bundle")
            }

            dialogPositiveStringId = arguments.getInt(DIALOG_POSITIVE_RID)
            if (dialogPositiveStringId == 0)
                dialogPositiveStringId = R.string.ok

            dialogNegativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID)
            if (dialogNegativeStringId == 0)
                dialogNegativeStringId = R.string.cancel

        } else {
            throw java.lang.IllegalArgumentException("Dialog ID and dialog msg must be passed in bundle")
        }

        return builder.setMessage(dialogMessage)
            .setPositiveButton(dialogPositiveStringId) { dialogInterface, which ->
                //callback for positive result
                dialogEvents?.onPositiveDialogResult(dialogId, arguments)
            }
            .setNegativeButton(dialogNegativeStringId) { dialogInterface, which ->
                //callback for negative result
                dialogEvents?.onNegativeDialogResult(dialogId, arguments)
            }
            .create()
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach: fragment detached")
        super.onDetach()

        // making the interface null because we are no more attached to the activity
        dialogEvents = null
    }
}