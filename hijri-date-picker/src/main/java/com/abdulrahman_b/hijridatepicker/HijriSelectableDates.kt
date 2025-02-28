@file:OptIn(ExperimentalMaterial3Api::class)
package com.abdulrahman_b.hijridatepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import com.abdulrahman_b.hijridatepicker.datepicker.rememberHijriDatePickerState
import com.abdulrahman_b.hijridatepicker.rangedatepicker.rememberHijriDateRangePickerState
import java.time.chrono.HijrahDate

/**
 * An interface that defines the contract for determining whether a Hijri date or a specific year
 * is selectable in a Hijri date picker.
 *
 * An implementation of this class must be created and passed to [rememberHijriDatePickerState] or [rememberHijriDateRangePickerState] respectively.
 */
interface HijriSelectableDates {

    /**
     * Determines whether the given HijrahDate is selectable.
     *
     * @param date the HijrahDate to evaluate for selection eligibility
     * @return true if the specified date is selectable, otherwise false
     */
    fun isSelectableDate(date: HijrahDate): Boolean = true

    /**
     * Determines whether the given year is selectable in the calendar.
     *
     * @param year the year to be checked for selectability
     * @return true if the year is selectable, false otherwise
     */
    fun isSelectableYear(year: Int): Boolean = true

}
