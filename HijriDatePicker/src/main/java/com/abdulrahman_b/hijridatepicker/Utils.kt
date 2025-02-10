@file:OptIn(ExperimentalMaterial3Api::class)

package com.abdulrahman_b.hijridatepicker

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.abdulrahman_b.hijridatepicker.tokens.MotionTokens
import java.time.DayOfWeek
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

/**
 * [ProvideContentColorTextStyle]
 *
 * A convenience method to provide values to both [LocalContentColor] and [LocalTextStyle] in one call.
 * This is less expensive than nesting calls to CompositionLocalProvider.
 *
 * Text styles will be merged with the current value of [LocalTextStyle].
 */
@Composable
internal fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content
    )
}


/**
 * Represents the content color for a calendar day.
 *
 * @param isToday indicates that the color is for a date that represents today
 * @param selected indicates that the color is for a selected day
 * @param inRange indicates that the day is part of a selection range of days
 * @param enabled indicates that the day is enabled for selection
 */
@Composable
internal fun DatePickerColors.dayContentColor(
    isToday: Boolean,
    selected: Boolean,
    inRange: Boolean,
    enabled: Boolean
): State<Color> {
    val target =
        when {
            selected && enabled -> selectedDayContentColor
            selected && !enabled -> disabledSelectedDayContentColor
            inRange && enabled -> dayInSelectionRangeContentColor
            inRange && !enabled -> disabledDayContentColor
            isToday -> todayContentColor
            enabled -> dayContentColor
            else -> disabledDayContentColor
        }

    return if (inRange) {
        rememberUpdatedState(target)
    } else {
        // Animate the content color only when the day is not in a range.
        animateColorAsState(target, tween(durationMillis = MotionTokens.DurationShort2.toInt()))
    }
}

/**
 * Represents the container color for a calendar day.
 *
 * @param selected indicates that the color is for a selected day
 * @param enabled indicates that the day is enabled for selection
 * @param animate whether or not to animate a container color change
 */
@Composable
internal fun DatePickerColors.dayContainerColor(
    selected: Boolean,
    enabled: Boolean,
    animate: Boolean
): State<Color> {
    val target =
        if (selected) {
            if (enabled) selectedDayContainerColor else disabledSelectedDayContainerColor
        } else {
            Color.Transparent
        }
    return if (animate) {
        animateColorAsState(target, tween(durationMillis = MotionTokens.DurationShort2.toInt()))
    } else {
        rememberUpdatedState(target)
    }
}

/**
 * Represents the content color for a calendar year.
 *
 * @param currentYear indicates that the color is for a year that represents the current year
 * @param selected indicates that the color is for a selected year
 * @param enabled indicates that the year is enabled for selection
 */
@Composable
internal fun DatePickerColors.yearContentColor(
    currentYear: Boolean,
    selected: Boolean,
    enabled: Boolean
): State<Color> {
    val target =
        when {
            selected && enabled -> selectedYearContentColor
            selected && !enabled -> disabledSelectedYearContentColor
            currentYear -> currentYearContentColor
            enabled -> yearContentColor
            else -> disabledYearContentColor
        }

    return animateColorAsState(
        target,
        tween(durationMillis = MotionTokens.DurationShort2.toInt())
    )
}

/**
 * Represents the container color for a calendar year.
 *
 * @param selected indicates that the color is for a selected day
 * @param enabled indicates that the year is enabled for selection
 */
@Composable
internal fun DatePickerColors.yearContainerColor(selected: Boolean, enabled: Boolean): State<Color> {
    val target =
        if (selected) {
            if (enabled) selectedYearContainerColor else disabledSelectedYearContainerColor
        } else {
            Color.Transparent
        }
    return animateColorAsState(
        target,
        tween(durationMillis = MotionTokens.DurationShort2.toInt())
    )
}


internal fun calculateDateFromPage(page: Int, yearsRange: IntRange): HijrahDate {

    val years = yearsRange.first + (page / 12)
    val months = page % 12 + 1

    return HijrahDate.of(years, months, 1)
}

internal fun calculatePageFromDate(date: HijrahDate, yearsRange: IntRange): Int {
    val years = date.get(ChronoField.YEAR_OF_ERA)
    val months = date.get(ChronoField.MONTH_OF_YEAR)

    return (years - yearsRange.first) * 12 + months - 1
}

internal fun calculateTotalPages(yearsRange: IntRange): Int {
    return yearsRange.count() * 12
}

internal fun calculateDaysFromStartOfWeekToFirstOfMonth(
    displayedMonth: HijrahDate
): Int {
    val weekDayOfFirstDayInMonth = displayedMonth.get(ChronoField.DAY_OF_WEEK)
    val firstDayOfWeek = DayOfWeek.SATURDAY.value

    val difference = weekDayOfFirstDayInMonth - firstDayOfWeek

    return if (difference < 0) {
        difference + 7
    } else {
        difference
    }
}


internal fun Int.toLocalString() = toString()