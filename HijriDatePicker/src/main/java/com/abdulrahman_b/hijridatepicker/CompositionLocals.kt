package com.abdulrahman_b.hijridatepicker

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import java.time.DayOfWeek

internal val LocalPickerLocale = compositionLocalOf<CalendarLocale> {
    error("No CalendarLocale provided")
}

@OptIn(ExperimentalMaterial3Api::class)
internal val LocalPickerFormatter = staticCompositionLocalOf<HijriDatePickerFormatter> {
    error("No DatePickerFormatter provided")
}

@OptIn(ExperimentalMaterial3Api::class)
internal val LocalFirstDayOfWeek = staticCompositionLocalOf<DayOfWeek> {
    error("No first day of week provided")
}