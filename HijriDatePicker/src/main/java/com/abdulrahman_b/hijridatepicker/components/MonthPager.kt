package com.abdulrahman_b.hijridatepicker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.horizontalScrollAxisRange
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.LocalPickerFormatter
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.calculateDateFromPage
import com.abdulrahman_b.hijridatepicker.calculateDaysFromStartOfWeekToFirstOfMonth
import com.abdulrahman_b.hijridatepicker.datepicker.DAYS_IN_WEEK
import com.abdulrahman_b.hijridatepicker.datepicker.RecommendedSizeForAccessibility
import com.abdulrahman_b.hijridatepicker.rangedatepicker.SelectedRangeInfo
import com.abdulrahman_b.hijridatepicker.toLocalString
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import drawRangeBackground
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

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
            // Apply this to prevent the screen reader from scrolling to the next or previous month,
            // and instead, traverse outside the Month composable when swiping from a focused first
            // or last day of the month.
            modifier =
                Modifier.semantics {
                    horizontalScrollAxisRange = ScrollAxisRange(value = { 0f }, maxValue = { 0f })
                },
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
@OptIn(ExperimentalMaterial3Api::class)
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
    val rangeSelectionDrawModifier =
        if (rangeSelectionInfo != null) {
            Modifier.drawWithContent {
                drawRangeBackground(rangeSelectionInfo, colors.dayInSelectionRangeContainerColor)
                drawContent()
            }
        } else {
            Modifier
        }

    var cellIndex = 0
    Column(
        modifier =
            Modifier.requiredHeight(RecommendedSizeForAccessibility * MAX_CALENDAR_ROWS)
                .then(rangeSelectionDrawModifier),
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
                        val date = displayedMonth.with(ChronoField.DAY_OF_MONTH, dayNumber.toLong())
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
                                        isSelectableYear(displayedMonth.get(ChronoField.YEAR_OF_ERA)) &&
                                                isSelectableDate(displayedMonth)
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
