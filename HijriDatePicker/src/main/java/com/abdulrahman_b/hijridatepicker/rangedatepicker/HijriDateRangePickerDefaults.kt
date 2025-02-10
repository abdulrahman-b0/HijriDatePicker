package com.abdulrahman_b.hijridatepicker.rangedatepicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.LocalPickerFormatter
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.Strings
import com.abdulrahman_b.hijridatepicker.getString
import java.time.chrono.HijrahDate

/** Contains default values used by the [DateRangePicker]. */
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
                Text(getString(string = Strings.DateRangePickerTitle), modifier = modifier)

            DisplayMode.Input ->
                Text(getString(string = Strings.DateRangeInputTitle), modifier = modifier)
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
        val startDateText = getString(Strings.DateRangePickerStartHeadline)
        val endDateText = getString(Strings.DateRangePickerEndHeadline)
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
        val dateFormatter = LocalPickerFormatter.current
        val formatterStartDate =
            dateFormatter.formatDate(date = selectedStartDate, locale = defaultLocale)

        val formatterEndDate =
            dateFormatter.formatDate(date = selectedEndDate, locale = defaultLocale)

        val verboseStartDateDescription =
            dateFormatter.formatDate(
                date = selectedStartDate,
                locale = defaultLocale,
                forContentDescription = true
            )
                ?: when (displayMode) {
                    DisplayMode.Picker -> getString(Strings.DatePickerNoSelectionDescription)
                    DisplayMode.Input -> getString(Strings.DateInputNoInputDescription)
                    else -> ""
                }

        val verboseEndDateDescription =
            dateFormatter.formatDate(
                date = selectedEndDate,
                locale = defaultLocale,
                forContentDescription = true
            )
                ?: when (displayMode) {
                    DisplayMode.Picker -> getString(Strings.DatePickerNoSelectionDescription)
                    DisplayMode.Input -> getString(Strings.DateInputNoInputDescription)
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
