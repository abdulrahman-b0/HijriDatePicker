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

import com.abdulrahman_b.hijridatepicker.R
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.withYear
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.year
import com.abdulrahman_b.hijridatepicker.*
import com.abdulrahman_b.hijridatepicker.components.*
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.chrono.HijrahDate
import java.time.format.DecimalStyle
import java.util.*

/**
 * [HijriDatePicker] is a composable function that provides a date picker component for selecting dates
 * in the Hijri calendar. This date picker allows users to select a date via a calendar UI or switch
 * to a date input mode for manual entry of dates using the keyboard.
 *
 * @param state The state of the date picker, which includes the selected date, displayed month,
 *   display mode, and other relevant information. See [HijriDatePickerState].
 * @param modifier The [Modifier] to be applied to this date picker.
 * @param dateFormatter A [DatePickerFormatter] that provides formatting skeletons for dates display.
 *   The default value is created using [HijriDatePickerDefaults.dateFormatter].
 * @param firstDayOfWeek The first day of the week to be displayed in the calendar. The default value
 *   is [DayOfWeek.SATURDAY].
 * @param title A composable function that represents the title to be displayed in the date picker.
 *   The default value is a title provided by [HijriDatePickerDefaults.DatePickerTitle].
 * @param headline A composable function that represents the headline to be displayed in the date
 *   picker. The default value is a headline provided by [HijriDatePickerDefaults.DatePickerHeadline].
 * @param showModeToggle A boolean indicating if this DatePicker should show a mode toggle action that
 *   transforms it into a date input. The default value is true.
 * @param colors [DatePickerColors] that will be used to resolve the colors used for this date picker
 *   in different states. The default value is provided by [DatePickerDefaults.colors].
 * @param locale The locale used to format the date and day of weeks. The default value is the first locale in the current configuration.
 * @param decimalStyle The [DecimalStyle] used to format the date. The default value is the decimal style of the provided locale.
 * This is useful if you want to use a different decimal style than the default locale.
 *
 */
@ExperimentalMaterial3Api
@Composable
fun HijriDatePicker(
    state: HijriDatePickerState,
    modifier: Modifier = Modifier,
    dateFormatter: HijriDatePickerFormatter = remember { HijriDatePickerDefaults.dateFormatter() },
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
    showModeToggle: Boolean = true,
    locale: Locale = LocalConfiguration.current.locales[0],
    decimalStyle: DecimalStyle = DecimalStyle.of(locale),
    colors: DatePickerColors = DatePickerDefaults.colors()
) {

    val selectableDates = state.selectableDates

    CompositionLocalProvider(
        LocalPickerLocale provides locale,
        LocalPickerDecimalStyle provides decimalStyle,
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
                            onDisplayModeChange = { displayMode -> state.displayMode = displayMode },
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
                contentDescription = stringResource(R.string.date_picker_switch_to_input_mode)
            )
        }
    } else {
        IconButton(onClick = { onDisplayModeChange(DisplayMode.Picker) }, modifier = modifier) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = stringResource(R.string.date_picker_switch_to_calendar_mode)
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
    val dateFormatter = LocalPickerFormatter.current
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
                DateInputContent(
                    selectedDate = selectedDate,
                    onDateSelectionChange = onDateSelectionChange,
                    yearRange = yearRange,
                    selectableDates = selectableDates,
                    colors = colors,
                    pattern = dateFormatter.inputDateSkeleton,
                    patternDelimiter = dateFormatter.inputDateDelimiter
                )
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
            yearPickerText = dateFormatter.formatMonthYear(
                date = displayedMonth,
                locale = LocalPickerLocale.current,
                decimalStyle = LocalPickerDecimalStyle.current
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
                val yearsPaneTitle = stringResource(R.string.date_picker_year_picker_pane_title)
                Column(modifier = Modifier.semantics { paneTitle = yearsPaneTitle }) {
                    YearPicker(
                        // Keep the height the same as the monthly calendar + weekdays height, and
                        // take into account the thickness of the divider that will be composed
                        // below it.
                        modifier =
                            Modifier
                                .requiredHeight(
                                    RecommendedSizeForAccessibility * (MAX_CALENDAR_ROWS + 1) -
                                            DividerDefaults.Thickness
                                )
                                .padding(horizontal = DatePickerHorizontalPadding),
                        currentYear = currentDate.year,
                        displayedYear = displayedMonth.year,
                        onYearSelected = { year ->
                            // Switch back to the monthly calendar and scroll to the selected year.
                            yearPickerVisible = !yearPickerVisible
                            coroutineScope.launch {
                                // Scroll to the selected year (maintaining the month of year).
                                // A LaunchEffect at the MonthsList will take care of rest and will
                                // update the state's displayedMonth to the month we scrolled to.
                                val withTargetYear = displayedMonth.withYear(year)
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
        modifier
            .fillMaxWidth()
            .then(heightModifier),
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
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(MonthYearHeight),
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
                            contentDescription = stringResource(R.string.date_picker_switch_to_previous_month)
                        )
                    }
                    IconButton(onClick = onNextClicked, enabled = nextAvailable) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = stringResource(R.string.date_picker_switch_to_next_month)
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

