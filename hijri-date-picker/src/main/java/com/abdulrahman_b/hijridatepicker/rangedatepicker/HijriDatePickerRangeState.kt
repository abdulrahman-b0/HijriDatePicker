package com.abdulrahman_b.hijridatepicker.rangedatepicker

/*
* Copyright 2023 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.datepicker.HijriDatePickerDefaults
import com.abdulrahman_b.hijridatepicker.valueOf
import java.time.chrono.HijrahDate

/**
 * A state object that can be hoisted to observe the date picker state. See
 * [rememberHijriDateRangePickerState].
 */
@ExperimentalMaterial3Api
@Stable
interface HijriDateRangePickerState {

    /**
     * A timestamp that represents the selected date _start_ of the day in _UTC_ milliseconds from
     * the epoch.
     *
     * @throws IllegalArgumentException in case the value is set with a timestamp that does not fall
     *   within the [yearRange].
     */
    var selectedStartDate: HijrahDate?

    var selectedEndDate: HijrahDate?

    /**
     * A timestamp that represents the currently displayed month _start_ date in _UTC_ milliseconds
     * from the epoch.
     *
     * @throws IllegalArgumentException in case the value is set with a timestamp that does not fall
     *   within the [yearRange].
     */
    var displayedMonth: HijrahDate


    /** A [DisplayMode] that represents the current UI mode (i.e. picker or input). */
    var displayMode: DisplayMode

    /** An [IntRange] that holds the year range that the date picker will be limited to. */
    val yearRange: IntRange

    /**
     * A [HijriSelectableDates] that is consulted to check if a date is allowed.
     *
     * In case a date is not allowed to be selected, it will appear disabled in the UI.
     */
    val selectableDates: HijriSelectableDates


    @Suppress("unused")
    fun getSelectedDateRange(): SelectedDateRange? {
        val startDate = selectedStartDate
        val endDate = selectedEndDate
        if (startDate == null || endDate == null)
            return null
        return SelectedDateRange(startDate, endDate)
    }

}

/**
 * Represents a selected date range.
 * @param startDate The start date of the range.
 * @param endDate The end date of the range.
 */
data class SelectedDateRange(val startDate: HijrahDate, val endDate: HijrahDate)


@OptIn(ExperimentalMaterial3Api::class)
internal class HijriDateRangePickerStateImpl(
    initialSelectedStartDate: HijrahDate?,
    initialSelectedEndDate: HijrahDate?,
    initialDisplayedMonth: HijrahDate,
    initialDisplayMode: DisplayMode,
    override val yearRange: IntRange,
    override val selectableDates: HijriSelectableDates,
) : HijriDateRangePickerState {

    override var selectedStartDate by mutableStateOf(initialSelectedStartDate)

    override var selectedEndDate by mutableStateOf(initialSelectedEndDate)

    override var displayedMonth by mutableStateOf(initialDisplayedMonth)

    override var displayMode by mutableStateOf(initialDisplayMode)


    companion object {

        fun Saver(
            selectableDates: HijriSelectableDates,
        ): Saver<HijriDateRangePickerState, *> = listSaver(
            save = {
                listOf(
                    it.selectedStartDate?.toEpochDay(),
                    it.selectedEndDate?.toEpochDay(),
                    it.displayedMonth.toEpochDay(),
                    it.yearRange.first,
                    it.yearRange.last,
                    it.displayMode.toString(),
                )
            },
            restore = { value ->
                HijriDateRangePickerStateImpl(
                    initialSelectedStartDate = (value[0] as? Long)?.let(HijrahDates::ofEpochDay),
                    initialSelectedEndDate = (value[1] as? Long)?.let(HijrahDates::ofEpochDay),
                    initialDisplayedMonth = HijrahDates.ofEpochDay(value[2] as Long),
                    yearRange = IntRange(value[3] as Int, value[4] as Int),
                    initialDisplayMode = DisplayMode.valueOf(value[5] as String),
                    selectableDates = selectableDates,
                )
            }
        )

    }

}

/**
 * Remembers the state of a Hijri date range picker.
 *
 * @param initialSelectedStartDate The initially selected start date, or null if no date is selected.
 * @param initialSelectedEndDate The initially selected end date, or null if no date is selected.
 * @param initialDisplayedMonth The initially displayed month in the date picker.
 * @param initialDisplayMode The initial display mode of the date picker (Picker or Input).
 * @param yearRange The range of years that the date picker will be limited to.
 * @param selectableDates A SelectableDates object that determines which dates are selectable.
 * @return A [HijriDateRangePickerState] object that holds the state of the date range picker.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun rememberHijriDateRangePickerState(
    initialSelectedStartDate: HijrahDate? = null,
    initialSelectedEndDate: HijrahDate? = null,
    initialDisplayedMonth: HijrahDate = HijrahDate.now(),
    initialDisplayMode: DisplayMode = DisplayMode.Picker,
    yearRange: IntRange = HijriDatePickerDefaults.YearRange,
    selectableDates: HijriSelectableDates = HijriDatePickerDefaults.AllDates,
): HijriDateRangePickerState {
    return rememberSaveable(
        saver = HijriDateRangePickerStateImpl.Saver(selectableDates)
    ) {
        HijriDateRangePickerStateImpl(
            initialSelectedStartDate,
            initialSelectedEndDate,
            initialDisplayedMonth,
            initialDisplayMode,
            yearRange,
            selectableDates,
        )
    }
}