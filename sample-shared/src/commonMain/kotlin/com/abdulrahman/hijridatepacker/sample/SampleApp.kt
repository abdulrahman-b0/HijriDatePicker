package com.abdulrahman.hijridatepacker.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abdulrahman.hijridatepacker.sample.theme.HijriDatePickerTheme
import com.abdulrahman_b.hijrahdatetime.format.HijrahDateTimeFormat
import com.abdulrahman_b.hijridatepicker.datepicker.HijriDatePicker
import com.abdulrahman_b.hijridatepicker.datepicker.HijriMultiDatePicker
import com.abdulrahman_b.hijridatepicker.datepicker.rememberHijriDatePickerState
import com.abdulrahman_b.hijridatepicker.datepicker.rememberHijriMultiDatePickerState
import com.abdulrahman_b.hijridatepicker.rangedatepicker.HijriDateRangePicker
import com.abdulrahman_b.hijridatepicker.rangedatepicker.rememberHijriDateRangePickerState
import hijridatepicker.sample_shared.generated.resources.Res
import hijridatepicker.sample_shared.generated.resources.ic_date_range
import hijridatepicker.sample_shared.generated.resources.ic_event
import hijridatepicker.sample_shared.generated.resources.ok
import hijridatepicker.sample_shared.generated.resources.select_date
import hijridatepicker.sample_shared.generated.resources.select_date_range
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SampleApp() {
    HijriDatePickerTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            DatePickerFormSample(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerFormSample(
    modifier: Modifier = Modifier
) {
    val formatter = remember {
        HijrahDateTimeFormat.ofPattern("yyyy/MM/dd")
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {

        //--------------------------- Hijri Date Picker (single) ---------------------------
        val datePickerState = rememberHijriDatePickerState(
            yearRange = 1400..1500,
        )
        var selectDateDialogOpen by remember {
            mutableStateOf(false)
        }
        var selectedDate by remember {
            mutableStateOf("")
        }

        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(Res.string.select_date)) },
            modifier = Modifier.padding(horizontal = 16.dp),
            trailingIcon = {
                IconButton(
                    onClick = { selectDateDialogOpen = true }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_event),
                        contentDescription = stringResource(Res.string.select_date)
                    )
                }
            }
        )

        if (selectDateDialogOpen) {
            DatePickerDialog(
                onDismissRequest = { selectDateDialogOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedDate = datePickerState.selectedDate?.format(formatter) ?: ""
                            selectDateDialogOpen = false
                        },
                        content = { Text(stringResource(Res.string.ok)) }
                    )
                },
            ) {
                HijriDatePicker(state = datePickerState, firstDayOfWeek = DayOfWeek.SUNDAY)
            }
        }

        //--------------------------- Hijri Date Range Picker ---------------------------

        val dateRangePickerState = rememberHijriDateRangePickerState()
        var selectDateRangeDialogOpen by remember {
            mutableStateOf(false)
        }
        var selectedDateRange by remember {
            mutableStateOf("")
        }

        OutlinedTextField(
            value = selectedDateRange,
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(Res.string.select_date_range)) },
            modifier = Modifier.padding(horizontal = 16.dp),
            trailingIcon = {
                IconButton(
                    onClick = { selectDateRangeDialogOpen = true }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_date_range),
                        contentDescription = stringResource(Res.string.select_date_range)
                    )
                }
            }
        )

        if (selectDateRangeDialogOpen) {
            DatePickerDialog(
                onDismissRequest = { selectDateRangeDialogOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val startDate = dateRangePickerState.selectedStartDate?.format(formatter) ?: ""
                            val endDate = dateRangePickerState.selectedEndDate?.format(formatter) ?: ""

                            selectedDateRange =
                                "$startDate - $endDate".takeIf { startDate.isNotEmpty() && endDate.isNotEmpty() } ?: ""
                            selectDateRangeDialogOpen = false
                        },
                        content = { Text(stringResource(Res.string.ok)) }
                    )
                },
            ) {
                HijriDateRangePicker(state = dateRangePickerState)
            }
        }

        //--------------------------- Hijri Multi-Date Picker (NEW) ---------------------------
        HijriMultiDatePickerSection(
            formatter = formatter,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/**
 * Displays a form field and dialog for selecting **multiple Hijri dates** using
 * [HijriMultiDatePicker].
 *
 * This composable mirrors the behavior of the single-date and range examples:
 *
 * - An [OutlinedTextField] shows the currently selected dates formatted as a string.
 * - Clicking the trailing icon opens a [DatePickerDialog] that hosts [HijriMultiDatePicker].
 * - When the user confirms, all selected dates from [rememberHijriMultiDatePickerState]
 *   are formatted with [formatter] and written back to the text field.
 *
 * The selected dates are displayed in ascending order of their epoch day to keep the
 * output stable and easy to read.
 *
 * @param formatter A [DateTimeFormatter] used to format each selected Hijri date.
 * @param modifier Optional [Modifier] for spacing and layout customization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HijriMultiDatePickerSection(
    formatter: HijrahDateTimeFormat,
    modifier: Modifier = Modifier
) {
    val multiDatePickerState = rememberHijriMultiDatePickerState(
        yearRange = 1400..1500,
    )
    var selectMultiDateDialogOpen by remember {
        mutableStateOf(false)
    }
    var selectedDatesText by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = selectedDatesText,
            onValueChange = { },
            readOnly = true,
            // Using a literal label here to avoid requiring new string resources.
            label = { Text("Select multiple dates") },
            modifier = Modifier.padding(horizontal = 16.dp),
            trailingIcon = {
                IconButton(
                    onClick = { selectMultiDateDialogOpen = true }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_date_range),
                        contentDescription = "Select multiple dates"
                    )
                }
            }
        )

        if (selectMultiDateDialogOpen) {
            DatePickerDialog(
                onDismissRequest = { selectMultiDateDialogOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Sort by epoch day so the dates appear in chronological order.
                            val formatted = multiDatePickerState.selectedDates
                                .sortedBy { it.toEpochDays() }
                                .joinToString(separator = ", ") { date ->
                                    date.format(formatter)
                                }

                            selectedDatesText = formatted
                            selectMultiDateDialogOpen = false
                        },
                        content = { Text(stringResource(Res.string.ok)) }
                    )
                },
            ) {
                HijriMultiDatePicker(state = multiDatePickerState)
            }
        }
    }
}