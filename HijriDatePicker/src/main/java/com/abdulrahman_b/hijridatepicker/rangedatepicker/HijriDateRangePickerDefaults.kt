package com.abdulrahman_b.hijridatepicker.rangedatepicker

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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.LocalPickerDecimalStyle
import com.abdulrahman_b.hijridatepicker.LocalPickerFormatter
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.R
import java.time.chrono.HijrahDate

/** Contains default values used by the [HijriDateRangePicker]. */
@ExperimentalMaterial3Api
@Stable
object HijriDateRangePickerDefaults {

    /**
     * A default date range picker title composable.
     *
     * @param displayMode the current [DisplayMode]
     * @param modifier a [Modifier] to be applied for the title
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DateRangePickerTitle(displayMode: DisplayMode, modifier: Modifier = Modifier) {
        when (displayMode) {
            DisplayMode.Picker ->
                Text(stringResource(R.string.date_range_picker_title), modifier = modifier)

            DisplayMode.Input ->
                Text((stringResource(R.string.date_range_input_title)), modifier = modifier)
        }
    }

    /**
     * A default date picker headline composable lambda that displays a default headline text when
     * there is no date selection, and an actual date string when there is.
     *
     * @param selectedStartDate a timestamp that represents the selected start date _start_ of
     *   the day in _UTC_ milliseconds from the epoch
     * @param selectedEndDate a timestamp that represents the selected end date _start_ of the
     *   day in _UTC_ milliseconds from the epoch
     * @param displayMode the current [DisplayMode]
     * @param modifier a [Modifier] to be applied for the headline
     */
    @Composable
    fun HijriDateRangePickerHeadline(
        selectedStartDate: HijrahDate?,
        selectedEndDate: HijrahDate?,
        displayMode: DisplayMode,
        modifier: Modifier = Modifier
    ) {
        val startDateText = stringResource(R.string.date_range_picker_start_headline)
        val endDateText = stringResource(R.string.date_range_picker_end_headline)
        DateRangePickerHeadline(
            selectedStartDate = selectedStartDate,
            selectedEndDate = selectedEndDate,
            displayMode = displayMode,
            modifier = modifier,
            startDateText = startDateText,
            endDateText = endDateText,
            startDatePlaceholder = { Text(text = startDateText) },
            endDatePlaceholder = { Text(text = endDateText) },
            datesDelimiter = { Text(text = "-") },
        )
    }

    /**
     * A date picker headline composable lambda that displays a default headline text when there is
     * no date selection, and an actual date string when there is.
     *
     * @param selectedStartDate a timestamp that represents the selected start date _start_ of
     *   the day in _UTC_ milliseconds from the epoch
     * @param selectedEndDate a timestamp that represents the selected end date _start_ of the
     *   day in _UTC_ milliseconds from the epoch
     * @param displayMode the current [DisplayMode]
     * @param modifier a [Modifier] to be applied for the headline
     * @param startDateText a string that, by default, be used as the text content for the
     *   [startDatePlaceholder], as well as a prefix for the content description for the selected
     *   start date
     * @param endDateText a string that, by default, be used as the text content for the
     *   [endDatePlaceholder], as well as a prefix for the content description for the selected end
     *   date
     * @param startDatePlaceholder a composable to be displayed as a headline placeholder for the
     *   start date (i.e. a [Text] with a "Start date" string)
     * @param endDatePlaceholder a composable to be displayed as a headline placeholder for the end
     *   date (i.e a [Text] with an "End date" string)
     * @param datesDelimiter a composable to be displayed as a headline delimiter between the start
     *   and the end dates
     */
    @Composable
    private fun DateRangePickerHeadline(
        selectedStartDate: HijrahDate?,
        selectedEndDate: HijrahDate?,
        displayMode: DisplayMode,
        modifier: Modifier,
        startDateText: String,
        endDateText: String,
        startDatePlaceholder: @Composable () -> Unit,
        endDatePlaceholder: @Composable () -> Unit,
        datesDelimiter: @Composable () -> Unit,
    ) {
        val defaultLocale = LocalPickerLocale.current
        val decimalStyle = LocalPickerDecimalStyle.current
        val dateFormatter = LocalPickerFormatter.current
        val formatterStartDate =
            dateFormatter.formatDate(selectedStartDate, defaultLocale, decimalStyle)

        val formatterEndDate =
            dateFormatter.formatDate(selectedEndDate, defaultLocale, decimalStyle)

        val verboseStartDateDescription =
            dateFormatter.formatDate(
                date = selectedStartDate,
                locale = defaultLocale,
                decimalStyle = decimalStyle,
                forContentDescription = true
            )
                ?: when (displayMode) {
                    DisplayMode.Picker -> stringResource(R.string.date_picker_no_selection_description)
                    DisplayMode.Input -> stringResource(R.string.date_input_no_input_description)
                    else -> ""
                }

        val verboseEndDateDescription =
            dateFormatter.formatDate(
                date = selectedEndDate,
                locale = defaultLocale,
                decimalStyle = decimalStyle,
                forContentDescription = true
            )
                ?: when (displayMode) {
                    DisplayMode.Picker -> stringResource(R.string.date_picker_no_selection_description)
                    DisplayMode.Input -> stringResource(R.string.date_input_no_input_description)
                    else -> ""
                }

        val startHeadlineDescription = "$startDateText: $verboseStartDateDescription"
        val endHeadlineDescription = "$endDateText: $verboseEndDateDescription"

        Row(
            modifier =
                modifier.clearAndSetSemantics {
                    liveRegion = LiveRegionMode.Polite
                    contentDescription = "$startHeadlineDescription, $endHeadlineDescription"
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (formatterStartDate != null) {
                Text(text = formatterStartDate)
            } else {
                startDatePlaceholder()
            }
            datesDelimiter()
            if (formatterEndDate != null) {
                Text(text = formatterEndDate)
            } else {
                endDatePlaceholder()
            }
        }
    }
}
