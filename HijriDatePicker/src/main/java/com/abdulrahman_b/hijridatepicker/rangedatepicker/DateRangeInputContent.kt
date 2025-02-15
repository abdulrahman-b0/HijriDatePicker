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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.R.string.*
import com.abdulrahman_b.hijridatepicker.datepicker.DateInputFormat
import com.abdulrahman_b.hijridatepicker.datepicker.DateInputTextField
import com.abdulrahman_b.hijridatepicker.datepicker.InputIdentifier
import com.abdulrahman_b.hijridatepicker.datepicker.InputTextFieldPadding
import com.abdulrahman_b.hijridatepicker.rememberDateInputValidator
import java.time.chrono.HijrahDate
import java.time.format.DecimalStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateRangeInputContent(
    selectedStartDate: HijrahDate?,
    selectedEndDate: HijrahDate?,
    onDatesSelectionChange: (startDate: HijrahDate?, endDate: HijrahDate?) -> Unit,
    yearRange: IntRange,
    pattern: String,
    patternDelimiter: Char,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    // Obtain the DateInputFormat for the default Locale.
    val locale = LocalPickerLocale.current

    val dateInputFormat = remember(locale) {
        DateInputFormat(pattern, patternDelimiter)
    }

    val dateInputValidator = rememberDateInputValidator(
        dateInputFormat = dateInputFormat,
        yearRange = yearRange,
        selectableDates = selectableDates,
        errorInvalidRangeInput = stringResource(date_range_input_invalid_range_input)
    )
    // Apply both start and end dates for proper validation.
    dateInputValidator.apply {
        currentStartDate = selectedStartDate
        currentEndDate = selectedEndDate
    }
    Row(
        modifier = Modifier.padding(paddingValues = InputTextFieldPadding),
        horizontalArrangement = Arrangement.spacedBy(TextFieldSpacing)
    ) {
        val pattern = dateInputFormat.patternWithDelimiters.uppercase()
        val startRangeText = (stringResource(date_range_picker_start_headline))
        DateInputTextField(
            modifier = Modifier.weight(0.5f),
            label = {
                Text(
                    startRangeText,
                    modifier =
                        Modifier.semantics { contentDescription = "$startRangeText, $pattern" }
                )
            },
            placeholder = { Text(pattern, modifier = Modifier.clearAndSetSemantics {}) },
            initialDate = selectedStartDate,
            onDateSelectionChange = { startDateMillis ->
                // Delegate to the onDatesSelectionChange and change just the start date.
                onDatesSelectionChange(startDateMillis, selectedEndDate)
            },
            inputIdentifier = InputIdentifier.StartDateInput,
            dateInputValidator = dateInputValidator,
            dateInputFormat = dateInputFormat,
            locale = locale,
            decimalStyle = DecimalStyle.STANDARD, //Because DateTimeFormatter doesn't support parsing Arabic numbers, it will throw an exception
            colors = colors
        )
        val endRangeText = (stringResource(date_range_picker_end_headline))
        DateInputTextField(
            modifier = Modifier.weight(0.5f),
            label = {
                Text(
                    endRangeText,
                    modifier = Modifier.semantics { contentDescription = "$endRangeText, $pattern" }
                )
            },
            placeholder = { Text(pattern, modifier = Modifier.clearAndSetSemantics {}) },
            initialDate = selectedEndDate,
            onDateSelectionChange = { endDateMillis ->
                // Delegate to the onDatesSelectionChange and change just the end date.
                onDatesSelectionChange(selectedStartDate, endDateMillis)
            },
            inputIdentifier = InputIdentifier.EndDateInput,
            dateInputValidator = dateInputValidator,
            dateInputFormat = dateInputFormat,
            locale = locale,
            decimalStyle = DecimalStyle.STANDARD, //Because DateTimeFormatter doesn't support parsing Arabic numbers, it will throw an exception
            colors = colors
        )
    }
}

private val TextFieldSpacing = 8.dp
