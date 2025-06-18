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

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.unit.IntOffset
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.dayOfMonth
import com.abdulrahman_b.hijridatepicker.calculateDaysFromStartOfWeekToFirstOfMonth
import com.abdulrahman_b.hijridatepicker.datepicker.DAYS_IN_WEEK
import java.time.DayOfWeek
import java.time.chrono.HijrahDate
import java.time.temporal.TemporalAdjusters

/**
 * a helper class for drawing a range selection. The class holds information about the selected
 * start and end dates as coordinates within the 7 x 6 calendar month grid, as well as information
 * regarding the first and last selected items.
 *
 */
internal class SelectedRangeInfo(
    val gridStartCoordinates: IntOffset,
    val gridEndCoordinates: IntOffset,
    val firstIsSelectionStart: Boolean,
    val lastIsSelectionEnd: Boolean
) {
    companion object {
        /**
         * Calculates the selection coordinates within the current month's grid. The returned [Pair]
         * holds the actual item x & y coordinates within the LazyVerticalGrid, and is later used to
         * calculate the exact offset for drawing the selection rectangles when in range-selection
         * mode.
         */
        @OptIn(ExperimentalMaterial3Api::class)
        fun calculateRangeInfo(
            displayedMonth: HijrahDate,
            startDate: HijrahDate,
            endDate: HijrahDate,
            firstDayOfWeek: DayOfWeek
        ): SelectedRangeInfo? {

            val displayedMonthStart = displayedMonth.with(TemporalAdjusters.firstDayOfMonth())
            val displayedMonthEnd = displayedMonth.with(TemporalAdjusters.lastDayOfMonth())
            val displayedMonthNumberOfDays = displayedMonth.lengthOfMonth()
            val displayedMonthDaysFromStartOfWeekToFirstOfMonth =
                calculateDaysFromStartOfWeekToFirstOfMonth(displayedMonth, firstDayOfWeek)

            if (
                startDate > displayedMonthEnd ||
                endDate < displayedMonthStart
            ) {
                return null
            }
            val firstIsSelectionStart = startDate >= displayedMonthStart
            val lastIsSelectionEnd = endDate <= displayedMonthEnd
            val startGridItemOffset =
                if (firstIsSelectionStart) {
                    displayedMonthDaysFromStartOfWeekToFirstOfMonth + startDate.dayOfMonth - 1
                } else {
                    displayedMonthDaysFromStartOfWeekToFirstOfMonth
                }
            val endGridItemOffset =
                if (lastIsSelectionEnd) {
                    displayedMonthDaysFromStartOfWeekToFirstOfMonth + endDate.dayOfMonth - 1
                } else {
                    displayedMonthDaysFromStartOfWeekToFirstOfMonth + displayedMonthNumberOfDays - 1
                }

            // Calculate the selected coordinates within the cells grid.
            val gridStartCoordinates =
                IntOffset(
                    x = startGridItemOffset % DAYS_IN_WEEK,
                    y = startGridItemOffset / DAYS_IN_WEEK
                )
            val gridEndCoordinates =
                IntOffset(x = endGridItemOffset % DAYS_IN_WEEK, y = endGridItemOffset / DAYS_IN_WEEK)
            return SelectedRangeInfo(
                gridStartCoordinates,
                gridEndCoordinates,
                firstIsSelectionStart,
                lastIsSelectionEnd
            )
        }
    }
}