package com.akash.taskMonitor

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.akash.taskMonitor.utilities.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.akash.tasktimer", appContext.packageName)
    }

    @Test
    fun taskDurationViewQuery() {
        // TODO: Check if the query formed is correct or not
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = AppDatabase.getInstance(context)
//        val query = database.addViewTaskDurations()
    }
}
