package com.abdulrahman_b.hijridatepicker

import android.text.format.DateFormat
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.format.DecimalStyle
import java.util.*

/**
 * A formatter class designed to handle Hijri dates and their formatting
 * based on various date skeletons and specific styles.
 *
 * This class utilizes a caching mechanism to improve performance
 * while formatting similar patterns multiple times.
 *
 * @constructor Accepts skeletons for formatting and a delimiter for handling input dates
 * without delimiters.
 */
@OptIn(ExperimentalMaterial3Api::class)
class HijriDatePickerFormatter(
    internal val yearMonthSelectionSkeleton: String,
    internal val selectedDateSkeleton: String,
    internal val selectedDateDescriptionSkeleton: String,
    internal val inputDateSkeleton: String,
    internal val inputDateDelimiter: Char
) {

    /**
     * A cache for storing and reusing instances of [DateTimeFormatter] to improve performance and reduce
     * overhead caused by repeatedly creating new formatter instances. The cache is keyed by a combination
     * of formatting skeleton, locale, and other customization parameters.
     */
    private val formattersCache = mutableMapOf<String, DateTimeFormatter>()

    /**
     * Formats the provided HijrahDate into a string representation suitable for a headline display
     * based on the given locale and decimal style.
     *
     * @param date The HijrahDate to be formatted. If null, the method returns null.
     * @param locale The Locale to use for formatting the date.
     * @param decimalStyle The DecimalStyle to use for formatting. Defaults to the decimal style
     *                     associated with the provided locale.
     * @return A string representation of the date formatted for a headline, or null if the date is null.
     */
    fun formatHeadlineDate(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle = DecimalStyle.of(locale)): String? {
        if (date == null) return null
        return getOrCreateFormatter(selectedDateSkeleton, locale, decimalStyle).format(date)
    }

    /**
     * Formats a given HijrahDate into a localized string representation based on specific formatting parameters.
     *
     * @param date The HijrahDate to be formatted. If null, the method will return null.
     * @param locale The locale to be used for formatting the date.
     * @param decimalStyle The decimal style to be applied for formatting (e.g., numeric symbols specific to the locale).
     * @param forContentDescription If true, a format suitable for content descriptions will be used; otherwise, a general display format will be applied.
     * @return A formatted date string, or null if the date parameter is null.
     */
    internal fun formatDate(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle, forContentDescription: Boolean = false): String? {

        if (date == null) return null
        val skeleton = if (forContentDescription) selectedDateDescriptionSkeleton else selectedDateSkeleton

        return getOrCreateFormatter(skeleton, locale, decimalStyle).format(date)
    }

    /**
     * Formats the given HijrahDate instance into a string representation without delimiters, based on the specified locale
     * and decimal style.
     *
     * @param date The HijrahDate instance to format. If null, the function will return null.
     * @param locale The Locale to use for formatting.
     * @param decimalStyle The DecimalStyle to apply during formatting.
     * @return A formatted string representation of the date without delimiters, or null if the input date is null.
     */
    internal fun formatInputDateWithoutDelimiters(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle): String? {
        if (date == null) return null
        return getOrCreateFormatter(
            skeleton = inputDateSkeleton.replace(inputDateDelimiter.toString(), ""),
            locale = locale,
            decimalStyle = decimalStyle,
            applyBestPattern = false
        ).format(date)
    }

    /**
     * Formats the given HijrahDate into a string representation of the month and year
     * based on the provided locale and decimal style.
     *
     * @param date the HijrahDate to be formatted. If null, the method returns null.
     * @param locale the locale to be used for formatting.
     * @param decimalStyle the decimal style to apply during formatting.
     * @return a string representing the month and year of the given date formatted
     *         according to the specified locale and decimal style, or null if the date is null.
     */
    internal fun formatMonthYear(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle): String? {
        if (date == null) return null
        return getOrCreateFormatter(yearMonthSelectionSkeleton, locale, decimalStyle).format(date)
    }

    /**
     * Parses a date string without delimiters into a `HijrahDate` object using the specified locale and decimal style.
     *
     * @param text The text representing the date without delimiters.
     * @param locale The locale to use for parsing the date string.
     * @param decimalStyle The decimal style to apply during parsing.
     * @return A `Result` containing the parsed `HijrahDate`, or an exception if parsing fails.
     */
    internal fun parseDateWithoutDelimiters(text: String, locale: Locale, decimalStyle: DecimalStyle): Result<HijrahDate> {
        val formatter = getOrCreateFormatter(inputDateSkeleton, locale, decimalStyle, false)

        val text = text.toMutableList().apply {
            val firstDelimiterIndex = inputDateSkeleton.indexOf(inputDateDelimiter)
            val secondDelimiterIndex = inputDateSkeleton.lastIndexOf(inputDateDelimiter)
            add(firstDelimiterIndex, inputDateDelimiter)
            add(secondDelimiterIndex, inputDateDelimiter)
        }.joinToString("")
        return runCatching { formatter.parse(text, HijrahDate::from) }
    }

    /**
     * Retrieves a cached DateTimeFormatter instance based on the given parameters, or creates a new one
     * if it does not already exist in the cache.
     *
     * @param skeleton The pattern or skeleton used to define the desired date/time format.
     * @param locale The locale to use for the formatter, which determines language and regional settings.
     * @param decimalStyle The decimal style that influences the formatting of numeric values in the date/time.
     * @param applyBestPattern A flag indicating whether to adjust the skeleton to the best available date/time pattern
     * for the given locale. Defaults to true.
     * @return A DateTimeFormatter instance configured with the specified parameters.
     */
    private fun getOrCreateFormatter(
        skeleton: String,
        locale: Locale,
        decimalStyle: DecimalStyle,
        applyBestPattern: Boolean = true
    ): DateTimeFormatter {
        val bestSkeleton = if (applyBestPattern) DateFormat.getBestDateTimePattern(locale, skeleton) else skeleton
        val key = "$bestSkeleton-$locale-$decimalStyle"
        return formattersCache.getOrPut(key) {
            DateTimeFormatter.ofPattern(bestSkeleton, locale)
                .withChronology(HijrahChronology.INSTANCE)
                .withDecimalStyle(decimalStyle)
        }
    }
}