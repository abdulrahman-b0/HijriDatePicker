package com.abdulrahman_b.hijridatepicker

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

import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.year
import com.abdulrahman_b.hijridatepicker.R.string.*
import com.abdulrahman_b.hijridatepicker.datepicker.DateInputFormat
import com.abdulrahman_b.hijridatepicker.datepicker.InputIdentifier
import java.time.chrono.HijrahDate
import java.time.format.DateTimeParseException
import java.time.format.DecimalStyle
import java.util.*

/**
 * A date input validator class.
 *
 * @param yearRange an [IntRange] that holds the year range that the date picker is being limited to
 * @param selectableDates a [SelectableDates] that is consulted to check if a date is allowed
 * @param dateInputFormat a [DateInputFormat] that holds date patterns information
 * @param dateFormatter a [DatePickerFormatter]
 * @param errorDatePattern a string for displaying an error message when an input does not match the
 *   expected date pattern. The string expects a date pattern string as an argument to be formatted
 *   into it.
 * @param errorDateOutOfYearRange a string for displaying an error message when an input date
 *   exceeds the year-range defined at the DateInput's state. The string expects a start and end
 *   year as arguments to be formatted into it.
 * @param errorInvalidNotAllowed a string for displaying an error message when an input date does
 *   not pass the DateInput's validator check. The string expects a date argument to be formatted
 *   into it.
 * @param errorInvalidRangeInput a string for displaying an error message when in a range input mode
 *   and one of the input dates is out of order (i.e. the user inputs a start date that is after the
 *   end date, or an end date that is before the start date)
 * @param currentStartDate the currently selected start date in milliseconds. Only checked
 *   against when the [InputIdentifier] is [InputIdentifier.EndDateInput].
 * @param currentEndDate the currently selected end date in milliseconds. Only checked against
 *   when the [InputIdentifier] is [InputIdentifier.StartDateInput].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Stable
internal class DateInputValidator(
    private val yearRange: IntRange,
    private val selectableDates: HijriSelectableDates,
    private val dateInputFormat: DateInputFormat,
    private val dateFormatter: HijriDatePickerFormatter,
    private val errorDatePattern: String,
    private val errorDateOutOfYearRange: String,
    private val errorInvalidNotAllowed: String,
    private val errorInvalidRangeInput: String,
    internal var currentStartDate: HijrahDate? = null,
    internal var currentEndDate: HijrahDate? = null,
) {

    /**
     * Validates a given HijrahDate input and returns an error message if the date is invalid.
     * If the date is valid, an empty string is returned.
     *
     * @param dateToValidate A Result containing the HijrahDate to validate.
     * @param inputIdentifier An InputIdentifier indicating the type of input field (start or end date).
     * @param locale The current Locale used for formatting.
     * @param decimalStyle The DecimalStyle used for formatting numbers.
     * @return A string containing an error message if the date is invalid, or an empty string if the date is valid.
     */
    fun validate(
        dateToValidate: Result<HijrahDate>,
        inputIdentifier: InputIdentifier,
        locale: Locale,
        decimalStyle: DecimalStyle
    ): String {

        return dateToValidate.fold(
            onSuccess = { date ->
                // Check if the date is within the valid range of years.
                if (!yearRange.contains(date.year)) {
                    return errorDateOutOfYearRange.format(
                        yearRange.first.toLocalString(locale, decimalStyle),
                        yearRange.last.toLocalString(locale, decimalStyle)
                    )
                }
                // Check if the date is selectable according to the provided SelectableDates.
                with(selectableDates) {
                    if (
                        !isSelectableYear(date.year) ||
                        !isSelectableDate(date)
                    ) {
                        return errorInvalidNotAllowed.format(
                            dateFormatter.formatDate(
                                date = date,
                                locale = locale,
                                decimalStyle = DecimalStyle.STANDARD
                            )
                        )
                    }
                }

                // Additional validation for range inputs (start and end dates).
                if (
                    (inputIdentifier == InputIdentifier.StartDateInput && date >= (currentEndDate
                        ?: HijrahDates.MAX)) ||
                    (inputIdentifier == InputIdentifier.EndDateInput && date < (currentStartDate ?: HijrahDates.MIN))
                ) {
                    // The start date is after the end date, or the end date is before the start date.
                    return@fold errorInvalidRangeInput
                }

                return@fold ""
            },
            onFailure = { exception ->
                // Handle parsing exceptions and return appropriate error messages.
                return if (exception is DateTimeParseException && exception.message?.contains("YearOfEra (valid values") == true) {
                    val firstYear = yearRange.first.toLocalString(locale, decimalStyle)
                    val lastYear = yearRange.last.toLocalString(locale, decimalStyle)
                    errorDateOutOfYearRange.format(firstYear, lastYear)
                } else {
                    errorDatePattern.format(dateInputFormat.patternWithDelimiters.uppercase())
                }
            }
        )

    }
}


/**
 * Creates and remembers a [DateInputValidator] instance for validating date input fields in a Hijri date picker.
 *
 * @param dateInputFormat an instance of [DateInputFormat] that provides the date patterns and delimiters for validation
 * @param yearRange an [IntRange] specifying the valid range of years for the date input
 * @param selectableDates an implementation of [HijriSelectableDates] to determine the selectability of specific dates and years
 * @param errorInvalidRangeInput a string for displaying an error message when a range input start date is after the end date,
 *   or the end date is before the start date
 * @return a [DateInputValidator] instance configured with the specified parameters
 */
@Composable
internal fun rememberDateInputValidator(
    dateInputFormat: DateInputFormat,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    errorInvalidRangeInput: String
): DateInputValidator {

    val dateFormatter = LocalPickerFormatter.current
    val errorDatePattern = stringResource(date_input_invalid_for_pattern)
    val errorDateOutOfYearRange = stringResource(date_input_invalid_year_range)
    val errorInvalidNotAllowed = stringResource(date_input_invalid_not_allowed)
    val dateInputValidator = remember(dateInputFormat, dateFormatter) {
        DateInputValidator(
            yearRange = yearRange,
            selectableDates = selectableDates,
            dateInputFormat = dateInputFormat,
            dateFormatter = dateFormatter,
            errorDatePattern = errorDatePattern,
            errorDateOutOfYearRange = errorDateOutOfYearRange,
            errorInvalidNotAllowed = errorInvalidNotAllowed,
            errorInvalidRangeInput = errorInvalidRangeInput
        )
    }

    return dateInputValidator
}