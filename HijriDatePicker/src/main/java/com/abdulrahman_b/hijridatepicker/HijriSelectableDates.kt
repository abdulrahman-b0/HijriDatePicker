@file:OptIn(ExperimentalMaterial3Api::class)
package com.abdulrahman_b.hijridatepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.time.chrono.HijrahDate

internal interface HijriSelectableDates : SelectableDates {

    fun isSelectableDate(date: HijrahDate): Boolean

    /**
     * This method is not supported for [HijriSelectableDates].
     * Calling this method will throw an [UnsupportedOperationException].
     *
     * Use [isSelectableDate] overload with [HijrahDate] parameter instead.
     */
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        throw UnsupportedOperationException("This method is not supported for HijriSelectableDates")
    }

}


fun hijriSelectableDates(
    isSelectableDate: (HijrahDate) -> Boolean = { true },
    isSelectableYear: (Int) -> Boolean = { true }
): SelectableDates = object : HijriSelectableDates {

    override fun isSelectableDate(date: HijrahDate): Boolean = isSelectableDate(date)

    override fun isSelectableYear(year: Int): Boolean = isSelectableYear(year)

}
