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


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.*
import com.abdulrahman_b.hijridatepicker.R.string.date_input_label
import java.time.chrono.HijrahDate
import java.time.format.DecimalStyle
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateInputContent(
    selectedDate: HijrahDate?,
    onDateSelectionChange: (date: HijrahDate?) -> Unit,
    yearRange: IntRange,
    pattern: String,
    patternDelimiter: Char,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    val locale = LocalPickerLocale.current

    val dateInputFormat = remember(locale) { DateInputFormat(pattern, patternDelimiter) }
    val dateInputValidator = rememberDateInputValidator(
        dateInputFormat, yearRange, selectableDates, errorInvalidRangeInput = "" // Not used here.
    )
    val labelText = stringResource(date_input_label)

    DateInputTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(InputTextFieldPadding),
        label = {
            Text(
                labelText,
                modifier = Modifier.semantics { contentDescription = "$labelText, $pattern" }
            )
        },
        placeholder = { Text(pattern, modifier = Modifier.clearAndSetSemantics {}) },
        initialDate = selectedDate,
        onDateSelectionChange = onDateSelectionChange,
        inputIdentifier = InputIdentifier.SingleDateInput,
        dateInputValidator =
            dateInputValidator.apply {
                // Only need to apply the start date, as this is for a single date input.
                currentStartDate = selectedDate
            },
        dateInputFormat = dateInputFormat,
        locale = locale,
        decimalStyle = DecimalStyle.STANDARD, //Because DateTimeFormatter doesn't support parsing Arabic numbers, it will throw an exception
        colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateInputTextField(
    modifier: Modifier,
    initialDate: HijrahDate?,
    onDateSelectionChange: (HijrahDate?) -> Unit,
    label: @Composable (() -> Unit)?,
    placeholder: @Composable (() -> Unit)?,
    inputIdentifier: InputIdentifier,
    dateInputValidator: DateInputValidator,
    dateInputFormat: DateInputFormat,
    locale: Locale,
    decimalStyle: DecimalStyle,
    colors: DatePickerColors
) {
    val errorText = rememberSaveable { mutableStateOf("") }
    val dateFormatter = LocalPickerFormatter.current
    var text by
    rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text =
                    initialDate?.let {
                        dateFormatter.formatInputDateWithoutDelimiters(it, locale, decimalStyle)
                    } ?: "",
                TextRange(0, 0)
            )
        )
    }

    OutlinedTextField(
        value = text,
        onValueChange = { input: TextFieldValue ->
            if (
                input.text.length <= dateInputFormat.patternWithoutDelimiters.length &&
                input.text.all { it.isDigit() }
            ) {
                text = input
                val trimmedText = input.text.trim()
                if (
                    trimmedText.isEmpty() ||
                    trimmedText.length < dateInputFormat.patternWithoutDelimiters.length
                ) {
                    errorText.value = ""
                    onDateSelectionChange(null)
                } else {
                    val parsedDate = dateFormatter.parseDateWithoutDelimiters(trimmedText, locale, decimalStyle)
                    errorText.value =
                        dateInputValidator.validate(
                            dateToValidate = parsedDate,
                            inputIdentifier = inputIdentifier,
                            locale = locale,
                            decimalStyle = decimalStyle
                        )
                    // Set the parsed date only if the error validation returned an empty string.
                    // Otherwise, set it to null, as the validation failed.
                    onDateSelectionChange(
                        if (errorText.value.isEmpty()) {
                            parsedDate.getOrNull()
                        } else {
                            null
                        }
                    )
                }
            }
        },
        modifier =
            modifier
                // Add bottom padding when there is no error. Otherwise, remove it as the error text
                // will take additional height.
                .padding(
                    bottom =
                        if (errorText.value.isNotBlank()) {
                            0.dp
                        } else {
                            InputTextNonErroneousBottomPadding
                        }
                )
                .semantics { if (errorText.value.isNotBlank()) error(errorText.value) },
        label = label,
        placeholder = placeholder,
        supportingText = { if (errorText.value.isNotBlank()) Text(errorText.value) },
        isError = errorText.value.isNotBlank(),
        visualTransformation = DateVisualTransformation(dateInputFormat),
        keyboardOptions =
            KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
        singleLine = true,
        colors = colors.dateTextFieldColors
    )
}



/**
 * Represents different input identifiers for the [DateInputTextField]. An `InputIdentifier` is used
 * when validating the user input, and especially when validating an input range.
 */
@Immutable
@JvmInline
internal value class InputIdentifier internal constructor(internal val value: Int) {

    companion object {
        /** Single date input */
        val SingleDateInput = InputIdentifier(0)

        /** A start date input */
        val StartDateInput = InputIdentifier(1)

        /** An end date input */
        val EndDateInput = InputIdentifier(2)
    }

    override fun toString() =
        when (this) {
            SingleDateInput -> "SingleDateInput"
            StartDateInput -> "StartDateInput"
            EndDateInput -> "EndDateInput"
            else -> "Unknown"
        }
}

/**
 * A [VisualTransformation] for date input. The transformation will automatically display the date
 * delimiters provided by the [DateInputFormat] as the date is being entered into the text field.
 */
@OptIn(ExperimentalMaterial3Api::class)
private class DateVisualTransformation(private val dateInputFormat: DateInputFormat) :
    VisualTransformation {

    private val firstDelimiterOffset = dateInputFormat.patternWithDelimiters.indexOf(dateInputFormat.delimiter)
    private val secondDelimiterOffset = dateInputFormat.patternWithDelimiters.lastIndexOf(dateInputFormat.delimiter)
    private val dateFormatLength = dateInputFormat.patternWithoutDelimiters.length

    private val dateOffsetTranslator = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            return when {
                offset < firstDelimiterOffset -> offset
                offset < secondDelimiterOffset -> offset + 1
                else -> offset + 2
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return when {
                offset <= firstDelimiterOffset - 1 -> offset
                offset <= secondDelimiterOffset - 1 -> offset - 1
                else -> offset - 2
            }
        }
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmedText = text.text
        if (text.text.length > dateFormatLength) {
            text.text.substring(0 until dateFormatLength)
        } else {
            text.text
        }
        var transformedText = ""
        trimmedText.forEachIndexed { index, char ->
            transformedText += char
            if (index + 1 == firstDelimiterOffset || index + 2 == secondDelimiterOffset) {
                transformedText += dateInputFormat.delimiter
            }
        }
        return TransformedText(AnnotatedString(transformedText), dateOffsetTranslator)
    }
}

/**
 * Holds the date input format pattern information.
 *
 * This data class hold the delimiter that is used by the current [CalendarLocale] when representing
 * dates in a short format, as well as a date pattern with and without a delimiter.
 */
@Immutable
internal data class DateInputFormat(val patternWithDelimiters: String, val delimiter: Char) {
    val patternWithoutDelimiters: String = patternWithDelimiters.replace(delimiter.toString(), "")
}

internal val InputTextFieldPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 10.dp)

// An optional padding that will only be added to the bottom of the date input text field when it's
// not showing an error message.
private val InputTextNonErroneousBottomPadding = 16.dp
