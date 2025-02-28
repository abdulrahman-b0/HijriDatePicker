package com.abdulrahman_b.hijridatepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import java.time.DayOfWeek
import java.time.format.DecimalStyle
import java.time.format.TextStyle
import java.util.*

/**
 * A CompositionLocal used to provide a `Locale` for customizing locale-aware behavior in the
 * date picker or related components. It defines the locale to be utilized for formatting,
 * parsing, or localization throughout the composables that consume it.
 *
 * This CompositionLocal must be explicitly provided a value by a parent composable. If no value
 * is provided, an error will be thrown when accessed.
 */
internal val LocalPickerLocale = compositionLocalOf<Locale> {
    error("No LocalPickerLocale provided")
}

/**
 * A CompositionLocal that provides a [DecimalStyle] used for formatting numbers.
 *
 * This local is used to determine how decimal numbers should be formatted
 * in the Hijri date picker, based on the current locale and decimal style.
 *
 * It is expected to be provided by a `CompositionLocalProvider` in the composition hierarchy.
 * If no value is provided, an error will be thrown.
 */
internal val LocalPickerDecimalStyle = compositionLocalOf<DecimalStyle> {
    error("No LocalPickerDecimalStyle provided")
}

/**
 * Provides a local composition for a `HijriDatePickerFormatter` instance
 * within the Compose framework tree.
 *
 * This composition local is used to supply a `HijriDatePickerFormatter` implementation
 * to descendant composables. The formatter handles the formatting and parsing
 * of Hijri dates based on various customizable parameters such as skeletons, locale,
 * and decimal styles.
 *
 * If no `HijriDatePickerFormatter` instance is provided in the composition tree,
 * accessing this variable will throw an error.
 */
@OptIn(ExperimentalMaterial3Api::class)
internal val LocalPickerFormatter = staticCompositionLocalOf<HijriDatePickerFormatter> {
    error("No LocalPickerFormatter provided")
}

/**
 * A CompositionLocal that provides the first day of the week to be used within the
 * component's composition hierarchy. This is typically used to determine which day
 * of the week starts the calendar week, such as Monday or Sunday.
 *
 * If no value is provided, invoking this composition local will throw an error.
 * Ensure that a valid `DayOfWeek` is set using `CompositionLocalProvider` at an
 * appropriate level in the composition hierarchy.
 */
@OptIn(ExperimentalMaterial3Api::class)
internal val LocalFirstDayOfWeek = staticCompositionLocalOf<DayOfWeek> {
    error("No LocalFirstDayOfWeek provided")
}

/**
 * CompositionLocal for providing a [TextStyle] to style the day of the week text in a calendar view.
 *
 * This local must be provided with a value using [CompositionLocalProvider]. If accessed without a
 * provided value, it will throw an error.
 */
internal val LocalDayOfWeekTextStyle = staticCompositionLocalOf<TextStyle> {
    error("No LocalDayOfWeekTextStyle provided")
}