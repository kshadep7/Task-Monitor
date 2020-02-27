package com.akash.tasktimer

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Task(val name: String, val description: String, val sortOrder: Int) : Parcelable {
    @IgnoredOnParcel
    var id: Long = 0

    override fun toString(): String {
        return "Task(name='$name', description='$description', sortOrder=$sortOrder, id=$id)"
    }
}
