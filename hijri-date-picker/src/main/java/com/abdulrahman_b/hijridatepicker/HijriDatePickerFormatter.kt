package com.abdulrahman_b.hijridatepicker

import android.text.format.DateFormat
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.format.DecimalStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
class HijriDatePickerFormatter(
    internal val yearMonthSelectionSkeleton: String,
    internal val selectedDateSkeleton: String,
    internal val selectedDateDescriptionSkeleton: String,
    internal val inputDateSkeleton: String,
    internal val inputDateDelimiter: Char
) {

    private val formattersCache = mutableMapOf<String, DateTimeFormatter>()

    /**
     * Formats the given [date] using the [selectedDateSkeleton] and the provided [locale] and [decimalStyle].
     *
     * @param date The date to format. If null, this method will return null.
     * @param locale The locale to use for formatting.
     * @param decimalStyle The decimal style to use for formatting. You can use [DecimalStyle.of] to create a new instance, or use [DecimalStyle.STANDARD]
     */
    fun formatHeadlineDate(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle = DecimalStyle.of(locale)): String? {
        if (date == null) return null
        return getOrCreateFormatter(selectedDateSkeleton, locale, decimalStyle).format(date)
    }

    /**
     * Formats the given [date] using the [selectedDateSkeleton] and the provided [locale] and [decimalStyle].
     *
     * @param date The date to format. If null, this method will return null.
     * @param locale The locale to use for formatting.
     * @param decimalStyle The decimal style to use for formatting.
     */
    internal fun formatDate(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle, forContentDescription: Boolean = false): String? {

        if (date == null) return null
        val skeleton = if (forContentDescription) selectedDateDescriptionSkeleton else selectedDateSkeleton

        return getOrCreateFormatter(skeleton, locale, decimalStyle).format(date)
    }

    internal fun formatInputDateWithoutDelimiters(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle): String? {
        if (date == null) return null
        return getOrCreateFormatter(
            skeleton = inputDateSkeleton.replace(inputDateDelimiter.toString(), ""),
            locale = locale,
            decimalStyle = decimalStyle,
            applyBestPattern = false
        ).format(date)
    }

    internal fun formatMonthYear(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle): String? {
        if (date == null) return null
        return getOrCreateFormatter(yearMonthSelectionSkeleton, locale, decimalStyle).format(date)
    }

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