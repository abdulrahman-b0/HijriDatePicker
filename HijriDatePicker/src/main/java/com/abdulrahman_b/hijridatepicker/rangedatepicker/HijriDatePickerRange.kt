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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.HijriDatePickerFormatter
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.LocalPickerFormatter
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.Strings
import com.abdulrahman_b.hijridatepicker.calculateDateFromPage
import com.abdulrahman_b.hijridatepicker.calculatePageFromDate
import com.abdulrahman_b.hijridatepicker.calculateTotalPages
import com.abdulrahman_b.hijridatepicker.components.DatePickerAnimatedContent
import com.abdulrahman_b.hijridatepicker.components.Month
import com.abdulrahman_b.hijridatepicker.components.WeekDays
import com.abdulrahman_b.hijridatepicker.components.updateDisplayedMonth
import com.abdulrahman_b.hijridatepicker.datepicker.DAYS_IN_WEEK
import com.abdulrahman_b.hijridatepicker.datepicker.DateEntryContainer
import com.abdulrahman_b.hijridatepicker.datepicker.DatePickerHorizontalPadding
import com.abdulrahman_b.hijridatepicker.datepicker.DatePickerModeTogglePadding
import com.abdulrahman_b.hijridatepicker.datepicker.DisplayModeToggleButton
import com.abdulrahman_b.hijridatepicker.datepicker.HijriDatePickerDefaults
import com.abdulrahman_b.hijridatepicker.datepicker.RecommendedSizeForAccessibility
import com.abdulrahman_b.hijridatepicker.getString
import com.abdulrahman_b.hijridatepicker.rangedatepicker.HijriDateRangePickerDefaults
import com.abdulrahman_b.hijridatepicker.rangedatepicker.HijriDateRangePickerState
import com.abdulrahman_b.hijridatepicker.rangedatepicker.SelectedRangeInfo
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.chrono.HijrahDate

/**
 * <a href="https://m3.material.io/components/date-pickers/overview" class="external"
 * target="_blank">Material Design date range picker</a>.
 *
 * Date range pickers let people select a range of dates and can be embedded into Dialogs.
 *
 * ![Date range picker
 * image](https://developer.android.com/images/reference/androidx/compose/material3/range-picker.png)
 *
 * A simple DateRangePicker looks like:
 *
 * @sample androidx.compose.material3.samples.DateRangePickerSample
 *
 * @param state state of the date range picker. See [rememberDateRangePickerState].
 * @param modifier the [Modifier] to be applied to this date range picker
 * @param dateFormatter a [DatePickerFormatter] that provides formatting skeletons for dates display
 * @param title the title to be displayed in the date range picker
 * @param headline the headline to be displayed in the date range picker
 * @param showModeToggle indicates if this DateRangePicker should show a mode toggle action that
 *   transforms it into a date range input
 * @param colors [DatePickerColors] that will be used to resolve the colors used for this date range
 *   picker in different states. See [DatePickerDefaults.colors].
 */
@ExperimentalMaterial3Api
@Composable
fun HijriDateRangePicker(
    state: HijriDateRangePickerState,
    modifier: Modifier = Modifier,
    dateFormatter: DatePickerFormatter = remember { HijriDatePickerDefaults.dateFormatter() },
    title: (@Composable () -> Unit)? = {
        HijriDateRangePickerDefaults.DateRangePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(DateRangePickerTitlePadding)
        )
    },
    headline: (@Composable () -> Unit)? = {
        HijriDateRangePickerDefaults.HijriDateRangePickerHeadline(
            selectedStartDate = state.selectedStartDate,
            selectedEndDate = state.selectedEndDate,
            displayMode = state.displayMode,
            modifier = Modifier.padding(DateRangePickerHeadlinePadding)
        )
    },
//    showModeToggle: Boolean = true,
    colors: DatePickerColors = DatePickerDefaults.colors()
) {

    val showModeToggle = false

    require(dateFormatter is HijriDatePickerFormatter) {
        "The provided dateFormatter must be an instance of HijriDatePickerFormatter. Use `HijriDatePickerDefaults.dateFormatter()` to create one."
    }

    val selectableDates = state.selectableDates
    require(selectableDates is HijriSelectableDates) {
        "The provided selectableDates must be an instance of HijriSelectableDates. Use `hijriSelectableDates()` to create one."
    }

    CompositionLocalProvider(
        LocalPickerFormatter provides dateFormatter,
        LocalPickerLocale provides state.locale
    ) {
        DateEntryContainer(
            modifier = modifier,
            title = title,
            headline = headline,
            modeToggleButton =
                if (showModeToggle) {
                    {
                        DisplayModeToggleButton(
                            modifier = Modifier.padding(DatePickerModeTogglePadding),
                            displayMode = state.displayMode,
                            onDisplayModeChange = { displayMode -> /*state.displayMode = displayMode*/ },
                        )
                    }
                } else {
                    null
                },
            headlineTextStyle = DatePickerModalTokens.RangeSelectionHeaderHeadlineFont,
            headerMinHeight =
                DatePickerModalTokens.RangeSelectionHeaderContainerHeight - HeaderHeightOffset,
            colors = colors,
        ) {

            SwitchableDateEntryContent(
                selectedStartDate = state.selectedStartDate,
                selectedEndDate = state.selectedEndDate,
                displayedMonth = state.displayedMonth,
                displayMode = state.displayMode,
                onDatesSelectionChange = { startDateMillis, endDateMillis ->
                    state.selectedStartDate = startDateMillis
                    state.selectedEndDate = endDateMillis
                },
                onDisplayedMonthChange = { monthInMillis ->
                    state.displayedMonth = monthInMillis
                },
                yearRange = state.yearRange,
                selectableDates = selectableDates,
                colors = colors
            )
        }
    }
}




/**
 * Date entry content that displays a [DateRangePickerContent] or a [DateRangeInputContent]
 * according to the state's display mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwitchableDateEntryContent(
    selectedStartDate: HijrahDate?,
    selectedEndDate: HijrahDate?,
    displayedMonth: HijrahDate,
    displayMode: DisplayMode,
    onDatesSelectionChange: (startDate: HijrahDate?, endDate: HijrahDate?) -> Unit,
    onDisplayedMonthChange: (month: HijrahDate) -> Unit,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {

    DatePickerAnimatedContent(displayMode) { mode ->
        when (mode) {
            DisplayMode.Picker ->
                DateRangePickerContent(
                    selectedStartDateMillis = selectedStartDate,
                    selectedEndDateMillis = selectedEndDate,
                    displayedMonth = displayedMonth,
                    onDatesSelectionChange = onDatesSelectionChange,
                    onDisplayedMonthChange = onDisplayedMonthChange,
                    yearRange = yearRange,
                    selectableDates = selectableDates,
                    colors = colors
                )

            DisplayMode.Input -> { //TODO: Not fully supported yet.
//                DateRangeInputContent(
//                    selectedStartDateMillis = selectedStartDate,
//                    selectedEndDateMillis = selectedEndDateMillis,
//                    onDatesSelectionChange = onDatesSelectionChange,
//                    calendarModel = calendarModel,
//                    yearRange = yearRange,
//                    dateFormatter = dateFormatter,
//                    selectableDates = selectableDates,
//                    colors = colors
//                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangePickerContent(
    selectedStartDateMillis: HijrahDate?,
    selectedEndDateMillis: HijrahDate?,
    displayedMonth: HijrahDate,
    onDatesSelectionChange: (startDateMillis: HijrahDate?, endDateMillis: HijrahDate?) -> Unit,
    onDisplayedMonthChange: (monthInMillis: HijrahDate) -> Unit,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {

    val monthPager = rememberPagerState(
        initialPage = calculatePageFromDate(displayedMonth, yearRange),
    ) {
        calculateTotalPages(yearRange)
    }

    Column(modifier = Modifier.padding(horizontal = DatePickerHorizontalPadding)) {
        WeekDays(colors)
        VerticalMonthsList(
            monthPager = monthPager,
            selectedStartDate = selectedStartDateMillis,
            selectedEndDate = selectedEndDateMillis,
            onDatesSelectionChange = onDatesSelectionChange,
            onDisplayedMonthChange = onDisplayedMonthChange,
            yearRange = yearRange,
            selectableDates = selectableDates,
            colors = colors
        )
    }
}

/**
 * Composes a continuous vertical scrollable list of calendar months. Each month will appear with a
 * header text indicating the month and the year.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerticalMonthsList(
    monthPager: PagerState,
    selectedStartDate: HijrahDate?,
    selectedEndDate: HijrahDate?,
    onDatesSelectionChange: (startDate: HijrahDate?, endDate: HijrahDate?) -> Unit,
    onDisplayedMonthChange: (month: HijrahDate) -> Unit,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    val today = HijrahDate.now()
    val dateFormatter = LocalPickerFormatter.current

    ProvideTextStyle(DatePickerModalTokens.DateLabelTextFont) {
        val coroutineScope = rememberCoroutineScope()
        val scrollToPreviousMonthLabel =
            getString(Strings.DateRangePickerScrollToShowPreviousMonth)
        val scrollToNextMonthLabel =
            getString(Strings.DateRangePickerScrollToShowNextMonth)

        // The updateDateSelection will invoke the onDatesSelectionChange with the proper
        // selection according to the current state.
        val onDateSelectionChange = { date: HijrahDate ->
            updateDateSelection(
                date = date,
                currentStartDate = selectedStartDate,
                currentEndDate = selectedEndDate,
                onDatesSelectionChange = onDatesSelectionChange
            )
        }

        val customAccessibilityAction =
            customScrollActions(
                state = monthPager,
                coroutineScope = coroutineScope,
                scrollUpLabel = scrollToPreviousMonthLabel,
                scrollDownLabel = scrollToNextMonthLabel
            )

        VerticalPager(
            state = monthPager
        ) { currentPage ->
            val displayedMonth = calculateDateFromPage(currentPage, yearRange)

            Column(modifier = Modifier.fillMaxWidth()) {
                ProvideTextStyle(DatePickerModalTokens.RangeSelectionMonthSubheadFont) {
                    Text(
                        text =
                            dateFormatter.formatMonthYear(
                                displayedMonth,
                                LocalPickerLocale.current
                            ) ?: "-",
                        modifier =
                            Modifier.padding(paddingValues = CalendarMonthSubheadPadding)
                                .semantics { customActions = customAccessibilityAction },
                        color = colors.subheadContentColor
                    )
                }
                val rangeSelectionInfo: SelectedRangeInfo? =
                    if (selectedStartDate != null && selectedEndDate != null) {
                        remember(selectedStartDate, selectedEndDate) {
                            SelectedRangeInfo.calculateRangeInfo(
                                displayedMonth = displayedMonth,
                                startDate = selectedStartDate,
                                endDate = selectedEndDate
                            )
                        }
                    } else {
                        null
                    }
                Month(
                    displayedMonth = displayedMonth,
                    onDateSelectionChange = onDateSelectionChange,
                    today = today,
                    startDate = selectedStartDate,
                    endDate = selectedEndDate,
                    rangeSelectionInfo = rangeSelectionInfo,
                    selectableDates = selectableDates,
                    colors = colors
                )
            }
        }
    }

    LaunchedEffect(monthPager) {
        updateDisplayedMonth(
            pagerState = monthPager,
            onDisplayedMonthChange = onDisplayedMonthChange,
            yearRange = yearRange
        )
    }
}

private fun updateDateSelection(
    date: HijrahDate,
    currentStartDate: HijrahDate?,
    currentEndDate: HijrahDate?,
    onDatesSelectionChange: (startDate: HijrahDate?, endDate: HijrahDate?) -> Unit
) {
    if (
        (currentStartDate == null && currentEndDate == null) ||
        (currentStartDate != null && currentEndDate != null)
    ) {
        // Set the selection to "start" only.
        onDatesSelectionChange(date, null)
    } else if (currentStartDate != null && date >= currentStartDate) {
        // Set the end date.
        onDatesSelectionChange(currentStartDate, date)
    } else {
        // The user selected an earlier date than the start date, so reset the start.
        onDatesSelectionChange(date, null)
    }
}

internal val CalendarMonthSubheadPadding = PaddingValues(start = 8.dp, top = 8.dp, bottom = 8.dp)



/**
 * Draws the range selection background.
 *
 * This function is called during a [Modifier.drawWithContent] call when a [Month] is composed with
 * an `rangeSelectionEnabled` flag.
 */
internal fun ContentDrawScope.drawRangeBackground(
    selectedRangeInfo: SelectedRangeInfo,
    color: Color
) {
    // The LazyVerticalGrid is defined to space the items horizontally by
    // DaysHorizontalPadding (e.g. 4.dp). However, as the grid is not limited in
    // width, the spacing can go beyond that value, so this drawing takes this into
    // account.
    val itemContainerWidth = RecommendedSizeForAccessibility.toPx()
    val itemContainerHeight = RecommendedSizeForAccessibility.toPx()
    val itemStateLayerHeight = DatePickerModalTokens.DateStateLayerHeight.toPx()
    val stateLayerVerticalPadding = (itemContainerHeight - itemStateLayerHeight) / 2
    val horizontalSpaceBetweenItems =
        (this.size.width - DAYS_IN_WEEK * itemContainerWidth) / DAYS_IN_WEEK

    val (x1, y1) = selectedRangeInfo.gridStartCoordinates
    val (x2, y2) = selectedRangeInfo.gridEndCoordinates
    // The endX and startX are offset to include only half the item's width when dealing with first
    // and last items in the selection in order to keep the selection edges rounded.
    var startX =
        x1 * (itemContainerWidth + horizontalSpaceBetweenItems) +
                (if (selectedRangeInfo.firstIsSelectionStart) itemContainerWidth / 2 else 0f) +
                horizontalSpaceBetweenItems / 2
    val startY = y1 * itemContainerHeight + stateLayerVerticalPadding
    var endX =
        x2 * (itemContainerWidth + horizontalSpaceBetweenItems) +
                (if (selectedRangeInfo.lastIsSelectionEnd) itemContainerWidth / 2
                else itemContainerWidth) +
                horizontalSpaceBetweenItems / 2
    val endY = y2 * itemContainerHeight + stateLayerVerticalPadding

    val isRtl = layoutDirection == LayoutDirection.Rtl
    // Adjust the start and end in case the layout is RTL.
    if (isRtl) {
        startX = this.size.width - startX
        endX = this.size.width - endX
    }

    // Draw the first row background
    drawRect(
        color = color,
        topLeft = Offset(startX, startY),
        size =
            Size(
                width =
                    when {
                        y1 == y2 -> endX - startX
                        isRtl -> -startX
                        else -> this.size.width - startX
                    },
                height = itemStateLayerHeight
            )
    )

    if (y1 != y2) {
        for (y in y2 - y1 - 1 downTo 1) {
            // Draw background behind the rows in between.
            drawRect(
                color = color,
                topLeft = Offset(0f, startY + (y * itemContainerHeight)),
                size = Size(width = this.size.width, height = itemStateLayerHeight)
            )
        }
        // Draw the last row selection background
        val topLeftX = if (layoutDirection == LayoutDirection.Ltr) 0f else this.size.width
        drawRect(
            color = color,
            topLeft = Offset(topLeftX, endY),
            size =
                Size(
                    width = if (isRtl) endX - this.size.width else endX,
                    height = itemStateLayerHeight
                )
        )
    }
}

private fun customScrollActions(
    state: PagerState,
    coroutineScope: CoroutineScope,
    scrollUpLabel: String,
    scrollDownLabel: String
): List<CustomAccessibilityAction> {
    val scrollUpAction = {
        if (!state.canScrollBackward) {
            false
        } else {
            coroutineScope.launch { state.scrollToPage(state.currentPage - 1) }
            true
        }
    }
    val scrollDownAction = {
        if (!state.canScrollForward) {
            false
        } else {
            coroutineScope.launch { state.scrollToPage(state.currentPage + 1) }
            true
        }
    }
    return listOf(
        CustomAccessibilityAction(label = scrollUpLabel, action = scrollUpAction),
        CustomAccessibilityAction(label = scrollDownLabel, action = scrollDownAction)
    )
}

private val DateRangePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
private val DateRangePickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)

// An offset that is applied to the token value for the RangeSelectionHeaderContainerHeight. The
// implementation does not render a "Save" and "X" buttons by default, so we don't take those into
// account when setting the header's max height.
//private val HeaderHeightOffset = 60.dp
private val HeaderHeightOffset = 16.dp
