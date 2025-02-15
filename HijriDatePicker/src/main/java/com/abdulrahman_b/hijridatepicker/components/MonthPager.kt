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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
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
    val daysFromStartOfWeekToFirstOfMonth = remember(displayedMonth) {
        calculateDaysFromStartOfWeekToFirstOfMonth(displayedMonth)
    }

    val numberOfDays = remember(displayedMonth) {
        displayedMonth.lengthOfMonth()
    }


    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(RecommendedSizeForAccessibility * MAX_CALENDAR_ROWS)
            .run {  // Draw a range background behind the days if needed.
                if (rangeSelectionInfo != null) {
                    Modifier.drawBehind {
                        drawRangeBackground(rangeSelectionInfo, colors.dayInSelectionRangeContainerColor)
                    }
                } else {
                    Modifier
                }
            },
        verticalArrangement = Arrangement.Top,
        horizontalArrangement = Arrangement.SpaceEvenly,
        maxItemsInEachRow = DAYS_IN_WEEK
    ) {
        repeat(daysFromStartOfWeekToFirstOfMonth) { // Empty cells before the first day of the month
            Spacer(
                Modifier.requiredSize(
                    RecommendedSizeForAccessibility,
                    RecommendedSizeForAccessibility
                )
            )
        }


        repeat(numberOfDays) {
            val dayNumber = it + 1
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
                            isSelectableYear(displayedMonth.year) && isSelectableDate(displayedMonth)
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
                colors = colors
            ) {
                Text(
                    text = dayNumber.toLocalString(),
                    // The semantics are set at the Day level.
                    modifier = Modifier.clearAndSetSemantics {},
                    textAlign = TextAlign.Center
                )
            }
        }

        FillRemainingOfRow(daysFromStartOfWeekToFirstOfMonth + numberOfDays, DAYS_IN_WEEK)

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
