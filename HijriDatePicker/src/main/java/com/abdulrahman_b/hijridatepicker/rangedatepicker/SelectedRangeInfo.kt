package com.abdulrahman_b.hijridatepicker.rangedatepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import com.abdulrahman_b.hijridatepicker.calculateDaysFromStartOfWeekToFirstOfMonth
import com.abdulrahman_b.hijridatepicker.datepicker.DAYS_IN_WEEK
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters

/**
 * a helper class for drawing a range selection. The class holds information about the selected
 * start and end dates as coordinates within the 7 x 6 calendar month grid, as well as information
 * regarding the first and last selected items.
 *
 * A SelectedRangeInfo is created when a [Month] is composed with an `rangeSelectionEnabled` flag.
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
            endDate: HijrahDate
        ): SelectedRangeInfo? {

            val displayedMonthStart = displayedMonth.with(TemporalAdjusters.firstDayOfMonth())
            val displayedMonthEnd = displayedMonth.with(TemporalAdjusters.lastDayOfMonth())
            val displayedMonthNumberOfDays = displayedMonth.lengthOfMonth()
            val displayedMonthDaysFromStartOfWeekToFirstOfMonth =
                calculateDaysFromStartOfWeekToFirstOfMonth(displayedMonth)

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
                    displayedMonthDaysFromStartOfWeekToFirstOfMonth + startDate.get(ChronoField.DAY_OF_MONTH) - 1
                } else {
                    displayedMonthDaysFromStartOfWeekToFirstOfMonth
                }
            val endGridItemOffset =
                if (lastIsSelectionEnd) {
                    displayedMonthDaysFromStartOfWeekToFirstOfMonth + endDate.get(ChronoField.DAY_OF_MONTH) - 1
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