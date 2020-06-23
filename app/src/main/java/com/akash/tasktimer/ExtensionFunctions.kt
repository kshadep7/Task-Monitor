package com.akash.tasktimer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

// to find fragment Id
fun FragmentActivity.findFragmentById(id: Int): Fragment? {
    return supportFragmentManager.findFragmentById(id)
}

// fun to show dialog with bundled arguments
fun FragmentActivity.showConfirmationDialog(
    id: Int,
    message: String,
    positiveCaption: Int = R.string.ok,
    negativeCaption: Int = R.string.cancel
) {
    val args = Bundle().apply {
        putInt(DIALOG_ID, id)
        putString(DIALOG_MSG, message)
        putInt(DIALOG_POSITIVE_RID, positiveCaption)
        putInt(DIALOG_NEGATIVE_RID, negativeCaption)
    }
    val dialog = AppDialog()
    dialog.arguments = args
    dialog.show(supportFragmentManager, null)
}

/*
        Extension based on article on medium by Dinesh Babuhunky
        link: https://medium.com/thoughts-overflow/how-to-add-a-fragment-in-kotlin-way-73203c5a450b
 */

// base function to perform fragment transaction
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

// to add a new fragment
fun FragmentActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}

//to replace fragment
fun FragmentActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}

// to remove fragment
fun FragmentActivity.removeFragment(fragment: Fragment) {
    supportFragmentManager.inTransaction { remove(fragment) }
}
