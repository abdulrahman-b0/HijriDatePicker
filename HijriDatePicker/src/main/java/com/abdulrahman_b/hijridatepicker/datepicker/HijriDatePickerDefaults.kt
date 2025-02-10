package com.abdulrahman_b.hijridatepicker.datepicker

import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.abdulrahman_b.hijridatepicker.HijriDatePickerFormatter
import com.abdulrahman_b.hijridatepicker.LocalPickerFormatter
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.Strings
import com.abdulrahman_b.hijridatepicker.getString
import com.abdulrahman_b.hijridatepicker.hijriSelectableDates
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

@OptIn(ExperimentalMaterial3Api::class)
object HijriDatePickerDefaults {


    val YearRange = HijrahChronology.INSTANCE.range(ChronoField.YEAR_OF_ERA).let { it.minimum.toInt()..it.maximum.toInt() }

    val AllDates = hijriSelectableDates()

    /**
     * A date format skeleton used to format the date picker's year selection menu button (e.g.
     * "Ramdan 1446")
     */
    const val YearMonthSkeleton: String = "yMMMM"

    /** A date format skeleton used to format a selected date (e.g. "Saf 27, 1446") */
    const val YearAbbrMonthDaySkeleton: String = "yMMMd"

    /**
     * A date format skeleton used to format a selected date to be used as content description for
     * screen readers (e.g. "Saturday, Shawwal 27, 1446")
     */
    const val YearMonthWeekdayDaySkeleton: String = "yMMMMEEEEd"


    fun dateFormatter(): DatePickerFormatter {
        return HijriDatePickerFormatter(
            yearSelectionSkeleton = YearMonthSkeleton,
            selectedDateSkeleton = YearAbbrMonthDaySkeleton,
            selectedDateDescriptionSkeleton = YearMonthWeekdayDaySkeleton
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
                Text(text = getString(string = Strings.Companion.DatePickerTitle), modifier = modifier)
            DisplayMode.Companion.Input ->
                Text(text = getString(string = Strings.Companion.DateInputTitle), modifier = modifier)
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
        modifier: Modifier = Modifier.Companion
    ) {
        val locale = LocalPickerLocale.current
        val dateFormatter = LocalPickerFormatter.current

        val formattedDate = dateFormatter.formatDate(
            date = selectedDate,
            locale = locale,
            forContentDescription = false
        )
        val verboseDateDescription = dateFormatter.formatDate(
            date = selectedDate,
            locale = locale,
            forContentDescription = true
        ) ?: when (displayMode) {
            DisplayMode.Companion.Picker -> getString(Strings.Companion.DatePickerNoSelectionDescription)
            DisplayMode.Companion.Input -> getString(Strings.Companion.DateInputNoInputDescription)
            else -> ""
        }

        val headlineText = formattedDate ?: when (displayMode) {
            DisplayMode.Companion.Picker -> getString(Strings.Companion.DatePickerHeadline)
            DisplayMode.Companion.Input -> getString(Strings.Companion.DateInputHeadline)
            else -> ""
        }

        val headlineDescription = when (displayMode) {
            DisplayMode.Companion.Picker -> getString(Strings.Companion.DatePickerHeadlineDescription)
            DisplayMode.Companion.Input -> getString(Strings.Companion.DateInputHeadlineDescription)
            else -> ""
        }.format(verboseDateDescription)

        Text(
            text = headlineText,
            modifier = modifier.semantics {
                liveRegion = LiveRegionMode.Companion.Polite
                contentDescription = headlineDescription
            },
            maxLines = 1
        )
    }

}