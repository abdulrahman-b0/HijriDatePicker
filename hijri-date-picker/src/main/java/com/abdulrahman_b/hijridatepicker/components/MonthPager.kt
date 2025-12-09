package com.abdulrahman_b.hijridatepicker.components

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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.withDayOfMonth
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.year
import com.abdulrahman_b.hijridatepicker.*
import com.abdulrahman_b.hijridatepicker.datepicker.DAYS_IN_WEEK
import com.abdulrahman_b.hijridatepicker.datepicker.RecommendedSizeForAccessibility
import com.abdulrahman_b.hijridatepicker.rangedatepicker.SelectedRangeInfo
import com.abdulrahman_b.hijridatepicker.rangedatepicker.drawRangeBackground
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import java.time.chrono.HijrahDate

/** Composes a horizontal pageable list of months. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HorizontalMonthsPager(
    pagerState: PagerState,
    selectedDate: HijrahDate?,
    onDateSelectionChange: (date: HijrahDate) -> Unit,
    onDisplayedMonthChange: (month: HijrahDate) -> Unit,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    val today = HijrahDate.now()

    ProvideTextStyle(DatePickerModalTokens.DateLabelTextFont) {
        HorizontalPager(
            state = pagerState,
        ) { currentPage ->

            val displayedMonth = calculateDateFromPage(currentPage, yearRange)


            Box(modifier = Modifier.fillMaxWidth()) {
                Month(
                    displayedMonth = displayedMonth,
                    onDateSelectionChange = onDateSelectionChange,
                    today = today,
                    startDate = selectedDate,
                    endDate = null,
                    rangeSelectionInfo = null,
                    selectableDates = selectableDates,
                    colors = colors
                )
            }

        }
    }

    LaunchedEffect(pagerState) {
        updateDisplayedMonth(
            pagerState = pagerState,
            onDisplayedMonthChange = onDisplayedMonthChange,
            yearRange = yearRange
        )
    }
}


/** A composable that renders a calendar month and displays a date selection. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun Month(
    displayedMonth: HijrahDate,
    onDateSelectionChange: (date: HijrahDate) -> Unit,
    today: HijrahDate,
    startDate: HijrahDate?,
    endDate: HijrahDate?,
    rangeSelectionInfo: SelectedRangeInfo?,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    val dateFormatter = LocalPickerFormatter.current
    val firstDayOfWeek = LocalFirstDayOfWeek.current
    val daysFromStartOfWeekToFirstOfMonth = remember(displayedMonth) {
        calculateDaysFromStartOfWeekToFirstOfMonth(displayedMonth, firstDayOfWeek)
    }

    val numberOfDays = remember(displayedMonth) {
        displayedMonth.lengthOfMonth()
    }

    var cellIndex = 0
    Column(
        modifier =
            Modifier
                .requiredHeight(RecommendedSizeForAccessibility * MAX_CALENDAR_ROWS)
                .run {
                    if (rangeSelectionInfo != null) {
                        Modifier.drawWithContent {
                            drawRangeBackground(rangeSelectionInfo, colors.dayInSelectionRangeContainerColor)
                            drawContent()
                        }
                    } else {
                        Modifier
                    }
                },
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(MAX_CALENDAR_ROWS) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(DAYS_IN_WEEK) {
                    if (
                        cellIndex < daysFromStartOfWeekToFirstOfMonth ||
                        cellIndex >=
                        (daysFromStartOfWeekToFirstOfMonth + numberOfDays)
                    ) {
                        // Empty cell
                        Spacer(
                            modifier =
                                Modifier.requiredSize(
                                    width = RecommendedSizeForAccessibility,
                                    height = RecommendedSizeForAccessibility
                                )
                        )
                    } else {
                        val dayNumber = cellIndex - daysFromStartOfWeekToFirstOfMonth + 1
                        val date = displayedMonth.withDayOfMonth(dayNumber)
                        val isToday = date == today
                        val startDateSelected = date == startDate
                        val endDateSelected = date == endDate
                        val inRange =
                            if (rangeSelectionInfo != null && startDate != null && endDate != null) {
                                remember(rangeSelectionInfo, date, startDate, endDate) {
                                    mutableStateOf(date >= startDate && date <= endDate)
                                }.value
                            } else {
                                false
                            }
                        val dayContentDescription =
                            dayContentDescription(
                                rangeSelectionEnabled = rangeSelectionInfo != null,
                                isToday = isToday,
                                isStartDate = startDateSelected,
                                isEndDate = endDateSelected,
                                isInRange = inRange
                            )
                        val formattedDateDescription =
                            dateFormatter.formatDate(
                                date = date,
                                locale = LocalPickerLocale.current,
                                decimalStyle = LocalPickerDecimalStyle.current,
                                forContentDescription = true
                            ) ?: ""
                        Day(
                            modifier = Modifier,
                            selected = startDateSelected || endDateSelected,
                            onClick = { onDateSelectionChange(date) },
                            // Only animate on the first selected day. This is important to
                            // disable when drawing a range marker behind the days on an
                            // end-date selection.
                            animateChecked = startDateSelected,
                            enabled =
                                remember(date, selectableDates) {
                                    // Disabled a day in case its year is not selectable, or the
                                    // date itself is specifically not allowed by the state's
                                    // SelectableDates.
                                    with(selectableDates) {
                                        isSelectableYear(date.year) &&
                                                isSelectableDate(date)
                                    }
                                },
                            today = isToday,
                            inRange = inRange,
                            description =
                                if (dayContentDescription != null) {
                                    "$dayContentDescription, $formattedDateDescription"
                                } else {
                                    formattedDateDescription
                                },
                            colors = colors,
                            dayNumber = dayNumber
                        )
                    }
                    cellIndex++
                }
            }
        }
    }
}

/**
 * Composes a horizontally pageable list of months that supports **multi-date selection**.
 *
 * This is a multi-select variant of [HorizontalMonthsPager]. Rather than tracking a single
 * selected date, it receives a [selectedDates] set and a [onDateToggle] callback to toggle
 * membership in that set.
 *
 * Internally, this uses the same pager infrastructure as the single-date picker, including
 * [PagerState] and [updateDisplayedMonth], so the host state can remain in sync with the
 * currently visible month.
 *
 * @param pagerState Pager state that controls which month page is currently visible.
 * @param selectedDates The set of Hijri dates that should be rendered as selected.
 * @param onDateToggle Callback invoked when a date cell is tapped in the calendar grid.
 *   Implementations typically call [HijriMultiDatePickerState.toggleDate] from here.
 * @param onDisplayedMonthChange Callback invoked whenever the visible month changes as the
 *   user scrolls through pages. This should update the host state's [displayedMonth].
 * @param yearRange The inclusive Hijri year range that the pager can navigate.
 * @param selectableDates Constraints that determine which dates are enabled for selection.
 * @param colors Color tokens used to style the calendar grid and day cells.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HorizontalMonthsPagerMulti(
    pagerState: PagerState,
    selectedDates: Set<HijrahDate>,
    onDateToggle: (date: HijrahDate) -> Unit,
    onDisplayedMonthChange: (month: HijrahDate) -> Unit,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    val today = HijrahDate.now()

    ProvideTextStyle(DatePickerModalTokens.DateLabelTextFont) {
        HorizontalPager(
            state = pagerState,
        ) { currentPage ->

            val displayedMonth = calculateDateFromPage(currentPage, yearRange)

            Box(modifier = Modifier.fillMaxWidth()) {
                MonthMulti(
                    displayedMonth = displayedMonth,
                    selectedDates = selectedDates,
                    onDateToggle = onDateToggle,
                    today = today,
                    selectableDates = selectableDates,
                    colors = colors
                )
            }
        }
    }

    // Keep the external "displayed month" in sync with the pager's current page.
    LaunchedEffect(pagerState) {
        updateDisplayedMonth(
            pagerState = pagerState,
            onDisplayedMonthChange = onDisplayedMonthChange,
            yearRange = yearRange
        )
    }
}

/**
 * Renders a single Hijri calendar month that supports **multi-date selection**.
 *
 * This is a simplified variant of [Month] that:
 * - Does not track range start/end.
 * - Does not compute in-range states.
 * - Marks a day as selected if it is present in [selectedDates].
 *
 * The actual tap handling is delegated to [onDateToggle], allowing the caller
 * (usually a picker state) to manage the underlying set of selected dates.
 *
 * @param displayedMonth The Hijri month to render in the grid.
 * @param selectedDates The set of Hijri dates that should appear as selected.
 * @param onDateToggle Invoked when a day cell is tapped. The given [HijrahDate] represents
 *   the full Hijri date (year, month, day) for that cell.
 * @param today The Hijri date that should be considered "today" for styling purposes.
 * @param selectableDates Constraints used to determine if a date should be enabled.
 * @param colors Color tokens used to style the day cells.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun MonthMulti(
    displayedMonth: HijrahDate,
    selectedDates: Set<HijrahDate>,
    onDateToggle: (date: HijrahDate) -> Unit,
    today: HijrahDate,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    val dateFormatter = LocalPickerFormatter.current
    val firstDayOfWeek = LocalFirstDayOfWeek.current

    val daysFromStartOfWeekToFirstOfMonth = remember(displayedMonth, firstDayOfWeek) {
        calculateDaysFromStartOfWeekToFirstOfMonth(displayedMonth, firstDayOfWeek)
    }

    val numberOfDays = remember(displayedMonth) {
        displayedMonth.lengthOfMonth()
    }

    var cellIndex = 0

    Column(
        modifier =
            Modifier.requiredHeight(RecommendedSizeForAccessibility * MAX_CALENDAR_ROWS),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(MAX_CALENDAR_ROWS) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(DAYS_IN_WEEK) {
                    if (
                        cellIndex < daysFromStartOfWeekToFirstOfMonth ||
                        cellIndex >=
                        (daysFromStartOfWeekToFirstOfMonth + numberOfDays)
                    ) {
                        // Empty cell: keep the calendar grid aligned by inserting a spacer.
                        Spacer(
                            modifier =
                                Modifier.requiredSize(
                                    width = RecommendedSizeForAccessibility,
                                    height = RecommendedSizeForAccessibility
                                )
                        )
                    } else {
                        val dayNumber = cellIndex - daysFromStartOfWeekToFirstOfMonth + 1
                        val date = displayedMonth.withDayOfMonth(dayNumber)
                        val isToday = date == today
                        val isSelected = selectedDates.contains(date)

                        val dayContentDescription =
                            dayContentDescription(
                                rangeSelectionEnabled = false,
                                isToday = isToday,
                                isStartDate = isSelected,
                                isEndDate = false,
                                isInRange = false
                            )

                        val formattedDateDescription =
                            dateFormatter.formatDate(
                                date = date,
                                locale = LocalPickerLocale.current,
                                decimalStyle = LocalPickerDecimalStyle.current,
                                forContentDescription = true
                            ) ?: ""

                        Day(
                            modifier = Modifier,
                            selected = isSelected,
                            onClick = { onDateToggle(date) },
                            // Animations are applied when a day transitions into "selected" state.
                            animateChecked = isSelected,
                            enabled =
                                remember(date, selectableDates) {
                                    with(selectableDates) {
                                        isSelectableYear(date.year) &&
                                                isSelectableDate(date)
                                    }
                                },
                            today = isToday,
                            inRange = false,
                            description =
                                if (dayContentDescription != null) {
                                    "$dayContentDescription, $formattedDateDescription"
                                } else {
                                    formattedDateDescription
                                },
                            colors = colors,
                            dayNumber = dayNumber
                        )
                    }
                    cellIndex++
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
internal suspend fun updateDisplayedMonth(
    pagerState: PagerState,
    onDisplayedMonthChange: (month: HijrahDate) -> Unit,
    yearRange: IntRange
) {
    snapshotFlow { pagerState.currentPage }
        .collect {
            onDisplayedMonthChange(
                calculateDateFromPage(it, yearRange)
            )
        }
}


internal const val MAX_CALENDAR_ROWS = 6
