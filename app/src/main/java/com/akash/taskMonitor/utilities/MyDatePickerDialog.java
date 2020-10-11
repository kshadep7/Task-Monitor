package com.akash.taskMonitor.utilities;

import android.app.DatePickerDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*  Reference to resolve Issue #2

    https://issuetracker.google.com/issues/36951008#comment18
*/
public class MyDatePickerDialog extends DatePickerDialog {

    public MyDatePickerDialog(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    @Override
    protected void onStop() {
        // by doing nothing resolves the uncessary call to onDateSet() method when dialog is cancelled
        // by using back press or cancel button.
//        super.onStop();

    }
}
