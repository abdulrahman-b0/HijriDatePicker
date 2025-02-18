package com.abdulrahman_b.hijridatepicker.datepicker

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

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates
import com.abdulrahman_b.hijridatepicker.*
import com.abdulrahman_b.hijridatepicker.R
import com.abdulrahman_b.hijridatepicker.datepicker.HijriDatePickerDefaults.dateFormatter
import java.time.chrono.HijrahDate

@OptIn(ExperimentalMaterial3Api::class)
object HijriDatePickerDefaults {


    val YearRange = HijrahDates.MIN_YEAR..HijrahDates.MAX_YEAR

    val AllDates = object : HijriSelectableDates {}

    /**
     * A date format skeleton used to format the date picker's year selection menu button (e.g.
     * "Ramadan 1446")
     */
    const val YEAR_MONTH_SKELETON: String = "yMMMM"

    /** A date format skeleton used to format a selected date (e.g. "Saf 27, 1446") */
    const val YEAR_ABBR_MONTH_DAY_SKELETON: String = "yMMMMd"

    /**
     * A date format skeleton used to format a selected date to be used as content description for
     * screen readers (e.g. "Saturday, Shawwal 27, 1446")
     */
    @Suppress("SpellCheckingInspection")
    const val YEAR_MONTH_WEEKDAY_DAY_SKELETON: String = "yMMMMEEEEd"

    private const val INPUT_DATE_SKELETON: String = "yyyy/MM/dd"
    private const val INPUT_DATE_DELIMITER: Char = '/'


    fun dateFormatter(
        headlineDateSkeleton: String = YEAR_MONTH_WEEKDAY_DAY_SKELETON,
    ): HijriDatePickerFormatter {
        return HijriDatePickerFormatter(
            yearMonthSelectionSkeleton = YEAR_MONTH_SKELETON,
            selectedDateSkeleton = headlineDateSkeleton,
            selectedDateDescriptionSkeleton = YEAR_ABBR_MONTH_DAY_SKELETON,
            inputDateSkeleton = INPUT_DATE_SKELETON,
            inputDateDelimiter = INPUT_DATE_DELIMITER
        )
    }


    /**
     * A default date picker title composable.
     *
     * @param displayMode the current [androidx.compose.material3.DisplayMode]
     * @param modifier a [androidx.compose.ui.Modifier] to be applied for the title
     */
    @Composable
    fun DatePickerTitle(displayMode: DisplayMode, modifier: Modifier = Modifier.Companion) {
        when (displayMode) {
            DisplayMode.Companion.Picker ->
                Text(text = (stringResource(R.string.date_picker_title)), modifier = modifier)
            DisplayMode.Companion.Input ->
                Text(text = (stringResource(R.string.date_input_title)), modifier = modifier)
        }
    }


    /**
     * A default date picker headline composable that displays a default headline text when there is
     * no date selection, and an actual date string when there is.
     *
     * @param selectedDate a timestamp that represents the selected date _start_ of the day in
     *   _UTC_ milliseconds from the epoch
     * @param displayMode the current [DisplayMode]
     * @param dateFormatter a [DatePickerFormatter]
     * @param modifier a [Modifier] to be applied for the headline
     */
    @Composable
    fun DatePickerHeadline(
        selectedDate: HijrahDate?,
        displayMode: DisplayMode,
        modifier: Modifier = Modifier
    ) {
        val locale = LocalPickerLocale.current
        val decimalStyle = LocalPickerDecimalStyle.current
        val dateFormatter = LocalPickerFormatter.current

        val formattedDate = dateFormatter.formatDate(
            date = selectedDate,
            locale = locale,
            decimalStyle = decimalStyle,
            forContentDescription = false
        )
        val verboseDateDescription = dateFormatter.formatDate(
            date = selectedDate,
            locale = locale,
            decimalStyle = decimalStyle,
            forContentDescription = true
        ) ?: when (displayMode) {
            DisplayMode.Companion.Picker -> stringResource(R.string.date_picker_no_selection_description)
            DisplayMode.Companion.Input -> stringResource(R.string.date_input_no_input_description)
            else -> ""
        }

        val headlineText = formattedDate ?: when (displayMode) {
            DisplayMode.Companion.Picker -> stringResource(R.string.date_picker_headline)
            DisplayMode.Companion.Input -> stringResource(R.string.date_input_headline)
            else -> ""
        }

        val headlineDescription = when (displayMode) {
            DisplayMode.Companion.Picker -> stringResource(R.string.date_picker_headline_description)
            DisplayMode.Companion.Input -> stringResource(R.string.date_input_headline_description)
            else -> ""
        }.format(verboseDateDescription)

        val style = LocalTextStyle.current
        var fontSize by remember(style) {
            mutableFloatStateOf(style.fontSize.value)
        }
        Text(
            text = headlineText,
            modifier = modifier.semantics {
                liveRegion = LiveRegionMode.Companion.Polite
                contentDescription = headlineDescription
            },
            maxLines = 1,
            fontSize = fontSize.sp,
            onTextLayout = { result ->
                if (result.hasVisualOverflow) {
                    fontSize--
                }
            }
        )
    }

}