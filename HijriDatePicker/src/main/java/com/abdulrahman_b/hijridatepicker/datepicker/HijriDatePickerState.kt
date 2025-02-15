package com.abdulrahman_b.hijridatepicker.datepicker

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
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate

/**
 * Represents the state of a Hijri date picker, which can be observed and controlled.
 * Use [rememberHijriDatePickerState] to create and remember an instance of this state.
 */
@ExperimentalMaterial3Api
@Stable
interface HijriDatePickerState {

    /**
     * The currently selected date, represented as a [HijrahDate].
     * Throws [IllegalArgumentException] if the date is outside the [yearRange].
     */
    var selectedDate: HijrahDate?

    /**
     * The currently displayed month, represented as a [HijrahDate].
     * Throws [IllegalArgumentException] if the date is outside the [yearRange].
     */
    var displayedMonth: HijrahDate

    /**
     * The current display mode of the date picker, either picker or input.
     */
    var displayMode: DisplayMode

    /**
     * The range of years that the date picker is limited to.
     */
    val yearRange: IntRange

    /**
     * Defines which dates are selectable. Disabled dates will appear grayed out in the UI.
     */
    val selectableDates: SelectableDates

}

@OptIn(ExperimentalMaterial3Api::class)
private class HijriDatePickerStateImpl(
    initialSelectedDate: HijrahDate?,
    initialDisplayedMonth: HijrahDate,
    initialDisplayMode: DisplayMode,
    override val yearRange: IntRange,
    override val selectableDates: SelectableDates,
) : HijriDatePickerState {

    override var selectedDate by mutableStateOf(initialSelectedDate)


    override var displayedMonth by mutableStateOf(initialDisplayedMonth)


    override var displayMode by mutableStateOf(initialDisplayMode)


    companion object {

        fun Saver(
            selectableDates: SelectableDates,
        ): Saver<HijriDatePickerState, *> = listSaver(
            save = {
                listOf(
                    it.selectedDate?.toEpochDay(),
                    it.displayedMonth.toEpochDay(),
                    it.yearRange.first,
                    it.yearRange.last,
                    it.displayMode.toString(),
                )
            },
            restore = { value ->
                HijriDatePickerStateImpl(
                    initialSelectedDate = (value[0] as? Long)?.let(HijrahChronology.INSTANCE::dateEpochDay),
                    initialDisplayedMonth = HijrahChronology.INSTANCE.dateEpochDay(value[1] as Long),
                    initialDisplayMode = when ((value[4] as String)) {
                        "Picker" -> DisplayMode.Picker
                        "Input" -> DisplayMode.Input
                        else -> throw IllegalArgumentException("Invalid DisplayMode")
                    },
                    yearRange = IntRange(value[2] as Int, value[3] as Int),
                    selectableDates = selectableDates,
                )
            }
        )

    }

}

/**
 * Creates a [HijriDatePickerState] that can be remembered across compositions.
 *
 * @param initialSelectedDate The initially selected date, represented as a [HijrahDate], or null if no date is selected.
 * @param initialDisplayedMonth The initially displayed month, represented as a [HijrahDate]. Defaults to the current month.
 * @param initialDisplayMode The initial display mode of the date picker, either picker or input. Defaults to [DisplayMode.Picker].
 * @param yearRange The range of years that the date picker is limited to, excluded dates doesn't appear in the picker UI. Defaults to [HijriDatePickerDefaults.YearRange].
 * @param selectableDates Defines which dates are selectable. Disabled dates will appear grayed out in the UI. Defaults to [HijriDatePickerDefaults.AllDates], which allows all dates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberHijriDatePickerState(
    initialSelectedDate: HijrahDate? = null,
    initialDisplayedMonth: HijrahDate = HijrahDate.now(),
    initialDisplayMode: DisplayMode = DisplayMode.Picker,
    yearRange: IntRange = HijriDatePickerDefaults.YearRange,
    selectableDates: SelectableDates = HijriDatePickerDefaults.AllDates,
): HijriDatePickerState {
    return rememberSaveable(
        saver = HijriDatePickerStateImpl.Saver(selectableDates)
    ) {
        HijriDatePickerStateImpl(
            initialSelectedDate,
            initialDisplayedMonth,
            initialDisplayMode,
            yearRange,
            selectableDates,
        )
    }
}