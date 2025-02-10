/*
 * Copyright 2022 The Android Open Source Project
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

package com.abdulrahman_b.hijridatepicker.datepicker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.HijriDatePickerFormatter
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.LocalFirstDayOfWeek
import com.abdulrahman_b.hijridatepicker.LocalPickerFormatter
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.ProvideContentColorTextStyle
import com.abdulrahman_b.hijridatepicker.Strings
import com.abdulrahman_b.hijridatepicker.calculatePageFromDate
import com.abdulrahman_b.hijridatepicker.calculateTotalPages
import com.abdulrahman_b.hijridatepicker.components.DatePickerAnimatedContent
import com.abdulrahman_b.hijridatepicker.components.HorizontalMonthsPager
import com.abdulrahman_b.hijridatepicker.components.MAX_CALENDAR_ROWS
import com.abdulrahman_b.hijridatepicker.components.WeekDays
import com.abdulrahman_b.hijridatepicker.components.YearPicker
import com.abdulrahman_b.hijridatepicker.components.YearPickerMenuButton
import com.abdulrahman_b.hijridatepicker.getString
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

/**
 * <a href="https://m3.material.io/components/date-pickers/overview" class="external"
 * target="_blank">Material Design date picker</a>.
 *
 * Date pickers let people select a date and preferably should be embedded into Dialogs. See
 * [DatePickerDialog].
 *
 * By default, a date picker lets you pick a date via a calendar UI. However, it also allows
 * switching into a date input mode for a manual entry of dates using the numbers on a keyboard.
 *
 * ![Date picker
 * image](https://developer.android.com/images/reference/androidx/compose/material3/date-picker.png)
 *
 * A simple DatePicker looks like:
 *
 * @sample androidx.compose.material3.samples.DatePickerSample
 *
 * A DatePicker with an initial UI of a date input mode looks like:
 *
 * @sample androidx.compose.material3.samples.DateInputSample
 *
 * A DatePicker with a provided [SelectableDates] that blocks certain days from being selected looks
 * like:
 *
 * @sample androidx.compose.material3.samples.DatePickerWithDateSelectableDatesSample
 *
 * @param state state of the date picker. See [rememberDatePickerState].
 * @param modifier the [Modifier] to be applied to this date picker
 * @param dateFormatter a [DatePickerFormatter] that provides formatting skeletons for dates display
 * @param title the title to be displayed in the date picker
 * @param headline the headline to be displayed in the date picker
 * @param showModeToggle indicates if this DatePicker should show a mode toggle action that
 *   transforms it into a date input
 * @param colors [DatePickerColors] that will be used to resolve the colors used for this date
 *   picker in different states. See [DatePickerDefaults.colors].
 */
@ExperimentalMaterial3Api
@Composable
fun HijriDatePicker(
    state: HijriDatePickerState,
    modifier: Modifier = Modifier,
    dateFormatter: DatePickerFormatter = remember { HijriDatePickerDefaults.dateFormatter() },
    firstDayOfWeek: DayOfWeek = DayOfWeek.SATURDAY,
    title: (@Composable () -> Unit)? = {
        HijriDatePickerDefaults.DatePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerTitlePadding)
        )
    },
    headline: (@Composable () -> Unit)? = {
        HijriDatePickerDefaults.DatePickerHeadline(
            selectedDate = state.selectedDate,
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerHeadlinePadding)
        )
    },
//    showModeToggle: Boolean = true, //TODO: This is false for now, not fully supported yet.
    colors: DatePickerColors = DatePickerDefaults.colors()
) {

    val showModeToggle = false
    require(dateFormatter is HijriDatePickerFormatter) {
        "The provided dateFormatter must be an instance of HijriDatePickerFormatter. Use `HijriDatePickerDefaults.dateFormatter()` to create one."
    }

    val selectableDates =state.selectableDates
    require(selectableDates is HijriSelectableDates) {
        "The provided selectableDates must be an instance of HijriSelectableDates. Use `hijriSelectableDates()` to create one."
    }
    
    CompositionLocalProvider(
        LocalPickerLocale provides state.locale,
        LocalPickerFormatter provides dateFormatter,
        LocalFirstDayOfWeek provides firstDayOfWeek,
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
                            onDisplayModeChange = { displayMode -> /*state.displayMode = displayMode*/ }, //TODO: This is immutable for now, not fully supported yet.
                        )
                    }
                } else {
                    null
                },
            headlineTextStyle = DatePickerModalTokens.HeaderHeadlineFont,
            headerMinHeight = DatePickerModalTokens.HeaderContainerHeight,
            colors = colors,
        ) {
            SwitchableDateEntryContent(
                selectedDate = state.selectedDate,
                displayedMonth = state.displayedMonth,
                displayMode = state.displayMode,
                onDateSelectionChange = { date -> state.selectedDate = date },
                onDisplayedMonthChange = { month -> state.displayedMonth = month },
                yearRange = state.yearRange,
                selectableDates = selectableDates,
                colors = colors
            )
        }
    }
}


/**
 * A base container for the date picker and the date input. This container composes the top common
 * area of the UI, and accepts [content] for the actual calendar picker or text field input.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateEntryContainer(
    modifier: Modifier,
    title: (@Composable () -> Unit)?,
    headline: (@Composable () -> Unit)?,
    modeToggleButton: (@Composable () -> Unit)?,
    colors: DatePickerColors,
    headlineTextStyle: TextStyle,
    headerMinHeight: Dp,
    content: @Composable () -> Unit
) {
    Column(
        modifier =
            modifier
                .sizeIn(minWidth = DatePickerModalTokens.ContainerWidth)
                .semantics { isTraversalGroup = true }
                .background(colors.containerColor)
    ) {
        DatePickerHeader(
            modifier = Modifier,
            title = title,
            titleContentColor = colors.titleContentColor,
            headlineContentColor = colors.headlineContentColor,
            minHeight = headerMinHeight
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val horizontalArrangement =
                    when {
                        headline != null && modeToggleButton != null -> Arrangement.SpaceBetween
                        headline != null -> Arrangement.Start
                        else -> Arrangement.End
                    }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = horizontalArrangement,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (headline != null) {
                        ProvideTextStyle(value = headlineTextStyle) {
                            Box(modifier = Modifier.weight(1f)) { headline() }
                        }
                    }
                    modeToggleButton?.invoke()
                }
                // Display a divider only when there is a title, headline, or a mode toggle.
                if (title != null || headline != null || modeToggleButton != null) {
                    HorizontalDivider(color = colors.dividerColor)
                }
            }
        }
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DisplayModeToggleButton(
    modifier: Modifier,
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit
) {
    if (displayMode == DisplayMode.Picker) {
        IconButton(onClick = { onDisplayModeChange(DisplayMode.Input) }, modifier = modifier) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = getString(Strings.Companion.DatePickerSwitchToInputMode)
            )
        }
    } else {
        IconButton(onClick = { onDisplayModeChange(DisplayMode.Picker) }, modifier = modifier) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = getString(Strings.Companion.DatePickerSwitchToCalendarMode)
            )
        }
    }
}

/**
 * Date entry content that displays a [DatePickerContent] or a [DateInputContent] according to the
 * state's display mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwitchableDateEntryContent(
    selectedDate: HijrahDate?,
    displayedMonth: HijrahDate,
    displayMode: DisplayMode,
    onDateSelectionChange: (date: HijrahDate?) -> Unit,
    onDisplayedMonthChange: (month: HijrahDate) -> Unit,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    // Parallax effect offset that will slightly scroll in and out the navigation part of the picker
    // when the display mode changes.
    DatePickerAnimatedContent(displayMode) { mode ->
        when (mode) {
            DisplayMode.Picker ->
                DatePickerContent(
                    selectedDate = selectedDate,
                    displayedMonth = displayedMonth,
                    onDateSelectionChange = onDateSelectionChange,
                    onDisplayedMonthChange = onDisplayedMonthChange,
                    yearRange = yearRange,
                    selectableDates = selectableDates,
                    colors = colors
                )

            DisplayMode.Input -> {

                TODO("Not implemented yet")
//                DateInputContent(
//                    selectedDate = selectedDate,
//                    onDateSelectionChange = onDateSelectionChange,
//                    calendarModel = calendarModel,
//                    yearRange = yearRange,
//                    selectableDates = selectableDates,
//                    colors = colors
//                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerContent(
    selectedDate: HijrahDate?,
    displayedMonth: HijrahDate,
    onDateSelectionChange: (date: HijrahDate) -> Unit,
    onDisplayedMonthChange: (month: HijrahDate) -> Unit,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {

    val currentDate = HijrahDate.now()
    val dateFormatter = LocalPickerFormatter.current
    val monthPager = rememberPagerState(
        initialPage = calculatePageFromDate(displayedMonth, yearRange),
    ) {
        calculateTotalPages(yearRange)
    }

    val coroutineScope = rememberCoroutineScope()
    var yearPickerVisible by rememberSaveable { mutableStateOf(false) }

    Column {
        MonthsNavigation(
            modifier = Modifier.padding(horizontal = DatePickerHorizontalPadding),
            nextAvailable = monthPager.canScrollForward,
            previousAvailable = monthPager.canScrollBackward,
            yearPickerVisible = yearPickerVisible,
            yearPickerText =
                dateFormatter.formatMonthYear(
                    date = displayedMonth,
                    locale = LocalPickerLocale.current
                ) ?: "-",
            onNextClicked = {
                coroutineScope.launch {
                    monthPager.animateScrollToPage(
                        monthPager.currentPage + 1
                    )
                }
            },
            onPreviousClicked = {
                coroutineScope.launch {
                    monthPager.animateScrollToPage(
                        monthPager.currentPage - 1
                    )
                }
            },
            onYearPickerButtonClicked = { yearPickerVisible = !yearPickerVisible },
            colors = colors
        )

        Box {
            Column(modifier = Modifier.padding(horizontal = DatePickerHorizontalPadding)) {
                WeekDays(colors)
                HorizontalMonthsPager(
                    pagerState = monthPager,
                    selectedDate = selectedDate,
                    onDateSelectionChange = onDateSelectionChange,
                    onDisplayedMonthChange = onDisplayedMonthChange,
                    yearRange = yearRange,
                    selectableDates = selectableDates,
                    colors = colors
                )
            }
            this@Column.AnimatedVisibility(
                visible = yearPickerVisible,
                modifier = Modifier.clipToBounds(),
                enter = expandVertically() + fadeIn(initialAlpha = 0.6f),
                exit = shrinkVertically() + fadeOut()
            ) {
                // Apply a paneTitle to make the screen reader focus on a relevant node after this
                // column is hidden and disposed.
                val yearsPaneTitle = getString(Strings.Companion.DatePickerYearPickerPaneTitle)
                Column(modifier = Modifier.semantics { paneTitle = yearsPaneTitle }) {
                    YearPicker(
                        // Keep the height the same as the monthly calendar + weekdays height, and
                        // take into account the thickness of the divider that will be composed
                        // below it.
                        modifier =
                            Modifier.requiredHeight(
                                RecommendedSizeForAccessibility * (MAX_CALENDAR_ROWS + 1) -
                                        DividerDefaults.Thickness
                            )
                                .padding(horizontal = DatePickerHorizontalPadding),
                        currentYear = currentDate.get(ChronoField.YEAR_OF_ERA),
                        displayedYear = displayedMonth.get(ChronoField.YEAR_OF_ERA),
                        onYearSelected = { year ->
                            // Switch back to the monthly calendar and scroll to the selected year.
                            yearPickerVisible = !yearPickerVisible
                            coroutineScope.launch {
                                // Scroll to the selected year (maintaining the month of year).
                                // A LaunchEffect at the MonthsList will take care of rest and will
                                // update the state's displayedMonth to the month we scrolled to.
                                val withTargetYear = displayedMonth.with(ChronoField.YEAR_OF_ERA, year.toLong())
                                val page = calculatePageFromDate(withTargetYear, yearRange)
                                monthPager.scrollToPage(page)
                            }
                        },
                        selectableDates = selectableDates,
                        yearRange = yearRange,
                        colors = colors
                    )
                    HorizontalDivider(color = colors.dividerColor)
                }
            }
        }
    }
}

@Composable
internal fun DatePickerHeader(
    modifier: Modifier,
    title: (@Composable () -> Unit)?,
    titleContentColor: Color,
    headlineContentColor: Color,
    minHeight: Dp,
    content: @Composable () -> Unit
) {
    // Apply a defaultMinSize only when the title is not null.
    val heightModifier =
        if (title != null) {
            Modifier.defaultMinSize(minHeight = minHeight)
        } else {
            Modifier
        }
    Column(
        modifier.fillMaxWidth().then(heightModifier),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (title != null) {
            val textStyle = DatePickerModalTokens.HeaderSupportingTextFont
            ProvideContentColorTextStyle(contentColor = titleContentColor, textStyle = textStyle) {
                Box(contentAlignment = Alignment.BottomStart) { title() }
            }
        }
        CompositionLocalProvider(LocalContentColor provides headlineContentColor, content = content)
    }
}




/**
 * A composable that shows a year menu button and a couple of buttons that enable navigation between
 * displayed months.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthsNavigation(
    modifier: Modifier,
    nextAvailable: Boolean,
    previousAvailable: Boolean,
    yearPickerVisible: Boolean,
    yearPickerText: String,
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    onYearPickerButtonClicked: () -> Unit,
    colors: DatePickerColors
) {
    Row(
        modifier = modifier.fillMaxWidth().requiredHeight(MonthYearHeight),
        horizontalArrangement =
            if (yearPickerVisible) {
                Arrangement.Start
            } else {
                Arrangement.SpaceBetween
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.navigationContentColor) {
            // A menu button for selecting a year.
            YearPickerMenuButton(
                onClick = onYearPickerButtonClicked,
                expanded = yearPickerVisible
            ) {
                Text(
                    text = yearPickerText,
                    modifier =
                        Modifier.semantics {
                            // Make the screen reader read out updates to the menu button text as
                            // the
                            // user navigates the arrows or scrolls to change the displayed month.
                            liveRegion = LiveRegionMode.Polite
                            contentDescription = yearPickerText
                        }
                )
            }
            // Show arrows for traversing months (only visible when the year selection is off)
            if (!yearPickerVisible) {
                Row {
                    IconButton(onClick = onPreviousClicked, enabled = previousAvailable) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = getString(Strings.Companion.DatePickerSwitchToPreviousMonth)
                        )
                    }
                    IconButton(onClick = onNextClicked, enabled = nextAvailable) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = getString(Strings.Companion.DatePickerSwitchToNextMonth)
                        )
                    }
                }
            }
        }
    }
}



internal val RecommendedSizeForAccessibility = 48.dp
internal val MonthYearHeight = 56.dp
internal val DatePickerHorizontalPadding = 12.dp
internal val DatePickerModeTogglePadding = PaddingValues(end = 12.dp, bottom = 12.dp)

private val DatePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
private val DatePickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)

internal const val DAYS_IN_WEEK = 7

