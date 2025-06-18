@file:OptIn(ExperimentalMaterial3Api::class)

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

package com.abdulrahman_b.hijridatepicker

import android.R.attr.firstDayOfWeek
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.dayOfWeek
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.withDayOfMonth
import com.abdulrahman_b.hijridatepicker.datepicker.DAYS_IN_WEEK
import com.abdulrahman_b.hijridatepicker.datepicker.RecommendedSizeForAccessibility
import com.abdulrahman_b.hijridatepicker.tokens.MotionTokens
import java.time.DayOfWeek
import java.time.chrono.HijrahDate
import java.time.format.DecimalStyle
import java.time.temporal.ChronoField
import java.util.Locale

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
        animateColorAsState(target, tween(durationMillis = MotionTokens.DURATION_100.toInt()))
    }
}

/**
 * Represents the container color for a calendar day.
 *
 * @param selected indicates that the color is for a selected day
 * @param enabled indicates that the day is enabled for selection
 * @param animate whether to animate a container color change
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
        animateColorAsState(target, tween(durationMillis = MotionTokens.DURATION_100.toInt()))
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
        tween(durationMillis = MotionTokens.DURATION_100.toInt())
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
        tween(durationMillis = MotionTokens.DURATION_100.toInt())
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
    displayedMonth: HijrahDate,
    firstDayOfWeek: DayOfWeek,
): Int {
    val firstOfMonth = displayedMonth.withDayOfMonth(1)
    val firstOfMonthDayOfWeek = firstOfMonth.dayOfWeek

    val startIndex = firstDayOfWeek.ordinal //6
    val dayIndex = firstOfMonthDayOfWeek.ordinal //3

    return (dayIndex - startIndex + 7) % 7

}

@Composable
@ReadOnlyComposable
internal fun Int.toLocalString(): String {
    val decimalStyle = LocalPickerDecimalStyle.current
    val locale = LocalPickerLocale.current
    return toLocalString(locale, decimalStyle)
}

internal fun Int.toLocalString(locale: Locale, decimalStyle: DecimalStyle): String {
    val formattingLocale = if (decimalStyle == DecimalStyle.STANDARD) {
        Locale.ENGLISH
    } else {
        locale
    }

    return String.format(formattingLocale, "%d", this)
}

internal fun DisplayMode.Companion.valueOf(value: String): DisplayMode {
    return when (value) {
        "Picker" -> Picker
        "Input" -> Input
        else -> throw IllegalArgumentException("Invalid DisplayMode: $value")
    }
}