package com.abdulrahman_b.hijridatepicker.rangedatepicker


import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import com.abdulrahman_b.hijridatepicker.datepicker.HijriDatePickerDefaults
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.util.Locale

/**
 * A state object that can be hoisted to observe the date picker state. See
 * [rememberHijriDateRangePickerState].
 */
@ExperimentalMaterial3Api
@Stable
interface HijriDateRangePickerState {

    /**
     * A timestamp that represents the selected date _start_ of the day in _UTC_ milliseconds from
     * the epoch.
     *
     * @throws IllegalArgumentException in case the value is set with a timestamp that does not fall
     *   within the [yearRange].
     */
    var selectedStartDate: HijrahDate?

    var selectedEndDate: HijrahDate?

    /**
     * A timestamp that represents the currently displayed month _start_ date in _UTC_ milliseconds
     * from the epoch.
     *
     * @throws IllegalArgumentException in case the value is set with a timestamp that does not fall
     *   within the [yearRange].
     */
    var displayedMonth: HijrahDate


    /** A [DisplayMode] that represents the current UI mode (i.e. picker or input). */
    val displayMode: DisplayMode //TODO: This is immutable for now, not fully supported yet.

    /** An [IntRange] that holds the year range that the date picker will be limited to. */
    val yearRange: IntRange

    /**
     * A [SelectableDates] that is consulted to check if a date is allowed.
     *
     * In case a date is not allowed to be selected, it will appear disabled in the UI.
     */
    val selectableDates: SelectableDates

    val locale: Locale
}


@OptIn(ExperimentalMaterial3Api::class)
internal class HijriDateRangePickerStateImpl(
    initialSelectedStartDate: HijrahDate?,
    initialSelectedEndDate: HijrahDate?,
    initialDisplayedMonth: HijrahDate,
    initialDisplayMode: DisplayMode,
    override val yearRange: IntRange,
    override val selectableDates: SelectableDates,
    override val locale: Locale
) : HijriDateRangePickerState {

    override var selectedStartDate by mutableStateOf(initialSelectedStartDate)

    override var selectedEndDate by mutableStateOf(initialSelectedEndDate)

    override var displayedMonth by mutableStateOf(initialDisplayedMonth)


    override var displayMode by mutableStateOf(initialDisplayMode)


    companion object {

        fun Saver(
            selectableDates: SelectableDates,
            locale: Locale,
        ): Saver<HijriDateRangePickerState, *> = listSaver(
            save = {
                listOf(
                    it.selectedStartDate?.toEpochDay(),
                    it.selectedEndDate?.toEpochDay(),
                    it.displayedMonth.toEpochDay(),
                    it.yearRange.first,
                    it.yearRange.last,
                    it.displayMode.toString(),
                )
            },
            restore = { value ->
                HijriDateRangePickerStateImpl(
                    initialSelectedStartDate = (value[0] as? Long)?.let(HijrahChronology.INSTANCE::dateEpochDay),
                    initialSelectedEndDate = (value[1] as? Long)?.let(HijrahChronology.INSTANCE::dateEpochDay),
                    initialDisplayedMonth = HijrahChronology.INSTANCE.dateEpochDay(value[2] as Long),
                    yearRange = IntRange(value[3] as Int, value[4] as Int),
                    initialDisplayMode = when ((value[5] as String)) {
                        "Picker" -> DisplayMode.Picker
                        "Input" -> DisplayMode.Input
                        else -> throw IllegalArgumentException("Invalid DisplayMode")
                    },
                    selectableDates = selectableDates,
                    locale = locale
                )
            }
        )

    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun rememberHijriDateRangePickerState(
    initialSelectedStartDate: HijrahDate? = null,
    initialSelectedEndDate: HijrahDate? = null,
    initialDisplayedMonth: HijrahDate = HijrahDate.now(),
//    initialDisplayMode: DisplayMode = DisplayMode.Picker, //TODO, not fully supported yet.
    yearRange: IntRange = HijriDatePickerDefaults.YearRange,
    selectableDates: SelectableDates = HijriDatePickerDefaults.AllDates,
    locale: Locale = LocalConfiguration.current.locales[0]
): HijriDateRangePickerState {
    return rememberSaveable(
        saver = HijriDateRangePickerStateImpl.Saver(selectableDates, locale)
    ) {
        HijriDateRangePickerStateImpl(
            initialSelectedStartDate,
            initialSelectedEndDate,
            initialDisplayedMonth,
            DisplayMode.Picker,
            yearRange,
            selectableDates,
            locale
        )
    }
}