@file:SuppressLint("PrivateResource")
package com.abdulrahman_b.hijridatepicker

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource

@Composable
@ReadOnlyComposable
internal fun getString(string: Strings) = stringResource(id = string.value)

@Composable
@ReadOnlyComposable
internal fun getString(string: Strings, vararg formatArgs: Any) = stringResource(id = string.value, *formatArgs)

@JvmInline
@Immutable
internal value class Strings (val value: Int) {
    companion object {

        inline val DatePickerTitle
            get() = Strings(R.string.date_picker_title)

        inline val DatePickerHeadline
            get() = Strings(R.string.date_picker_headline)

        inline val DatePickerYearPickerPaneTitle
            get() = Strings(R.string.date_picker_year_picker_pane_title)

        inline val DatePickerSwitchToYearSelection
            get() = Strings(R.string.date_picker_switch_to_year_selection)

        inline val DatePickerSwitchToDaySelection
            get() = Strings(R.string.date_picker_switch_to_day_selection)

        inline val DatePickerSwitchToNextMonth
            get() = Strings(R.string.date_picker_switch_to_next_month)

        inline val DatePickerSwitchToPreviousMonth
            get() = Strings(R.string.date_picker_switch_to_previous_month)

        inline val DatePickerNavigateToYearDescription
            get() = Strings(R.string.date_picker_navigate_to_year_description)

        inline val DatePickerHeadlineDescription
            get() = Strings(R.string.date_picker_headline_description)

        inline val DatePickerNoSelectionDescription
            get() = Strings(R.string.date_picker_no_selection_description)

        inline val DatePickerTodayDescription
            get() = Strings(R.string.date_picker_today_description)

        inline val DatePickerScrollToShowLaterYears
            get() = Strings(R.string.date_picker_scroll_to_later_years)

        inline val DatePickerScrollToShowEarlierYears
            get() = Strings(R.string.date_picker_scroll_to_earlier_years)

        inline val DateInputTitle
            get() = Strings(R.string.date_input_title)

        inline val DateInputHeadline
            get() = Strings(R.string.date_input_headline)

        inline val DateInputLabel
            get() = Strings(R.string.date_input_label)

        inline val DateInputHeadlineDescription
            get() = Strings(R.string.date_input_headline_description)

        inline val DateInputNoInputDescription
            get() = Strings(R.string.date_input_no_input_description)

        inline val DateInputInvalidNotAllowed
            get() = Strings(R.string.date_input_invalid_not_allowed)

        inline val DateInputInvalidForPattern
            get() = Strings(R.string.date_input_invalid_for_pattern)

        inline val DateInputInvalidYearRange
            get() = Strings(R.string.date_input_invalid_year_range)

        inline val DatePickerSwitchToCalendarMode
            get() = Strings(R.string.date_picker_switch_to_calendar_mode)

        inline val DatePickerSwitchToInputMode
            get() = Strings(R.string.date_picker_switch_to_input_mode)

        inline val DateRangePickerTitle
            get() = Strings(R.string.date_range_picker_title)

        inline val DateRangePickerStartHeadline
            get() = Strings(R.string.date_range_picker_start_headline)

        inline val DateRangePickerEndHeadline
            get() = Strings(R.string.date_range_picker_end_headline)

        inline val DateRangePickerScrollToShowNextMonth
            get() = Strings(R.string.date_range_picker_scroll_to_next_month)

        inline val DateRangePickerScrollToShowPreviousMonth
            get() = Strings(R.string.date_range_picker_scroll_to_previous_month)

        inline val DateRangePickerDayInRange
            get() = Strings(R.string.date_range_picker_day_in_range)

        inline val DateRangeInputTitle
            get() = Strings(R.string.date_range_input_title)

        inline val DateRangeInputInvalidRangeInput
            get() = Strings(R.string.date_range_input_invalid_range_input)
    }
}
