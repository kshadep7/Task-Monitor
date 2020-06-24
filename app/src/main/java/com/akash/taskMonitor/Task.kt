package com.akash.taskMonitor

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    val name: String, val description: String, val sortOrder: Int,
    var id: Long = 0
) : Parcelable {

    override fun toString(): String {
        return "Task(name='$name', description='$description', sortOrder=$sortOrder, id=$id)"
    }
}
