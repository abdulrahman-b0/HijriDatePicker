package com.abdulrahman_b.hijridatepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import java.time.DayOfWeek
import java.time.format.DecimalStyle
import java.util.*

internal val LocalPickerLocale = compositionLocalOf<Locale> {
    error("No LocalPickerLocale provided")
}

internal val LocalPickerDecimalStyle = compositionLocalOf<DecimalStyle> {
    error("No LocalPickerDecimalStyle provided")
}

@OptIn(ExperimentalMaterial3Api::class)
internal val LocalPickerFormatter = staticCompositionLocalOf<HijriDatePickerFormatter> {
    error("No LocalPickerFormatter provided")
}

@OptIn(ExperimentalMaterial3Api::class)
internal val LocalFirstDayOfWeek = staticCompositionLocalOf<DayOfWeek> {
    error("No LocalFirstDayOfWeek provided")
}