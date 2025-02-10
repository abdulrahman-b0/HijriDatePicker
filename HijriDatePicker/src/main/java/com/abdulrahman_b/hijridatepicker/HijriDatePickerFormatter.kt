package com.abdulrahman_b.hijridatepicker

import android.text.format.DateFormat
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
internal class HijriDatePickerFormatter(
    val yearSelectionSkeleton: String,
    val selectedDateSkeleton: String,
    val selectedDateDescriptionSkeleton: String
): DatePickerFormatter {

    private val formattersCache = mutableMapOf<String, DateTimeFormatter>()

    override fun formatDate(dateMillis: Long?, locale: CalendarLocale, forContentDescription: Boolean): String? {
        throw UnsupportedOperationException("This method is not supported for HijriDatePickerFormatter")
    }

    override fun formatMonthYear(monthMillis: Long?, locale: CalendarLocale): String? {
        throw UnsupportedOperationException("This method is not supported for HijriDatePickerFormatter")
    }

    fun formatDate(date: HijrahDate?, locale: CalendarLocale, forContentDescription: Boolean = false): String? {

        if (date == null) return null
        val skeleton = if (forContentDescription) selectedDateDescriptionSkeleton else selectedDateSkeleton

        return getOrCreateFormatter(skeleton, locale).format(date)
    }

    fun formatMonthYear(date: HijrahDate?, locale: CalendarLocale): String? {
        if (date == null) return null
        return getOrCreateFormatter(yearSelectionSkeleton, locale).format(date)
    }

    private fun getOrCreateFormatter(skeleton: String, locale: Locale): DateTimeFormatter {
        val bestSkeleton = DateFormat.getBestDateTimePattern(locale, skeleton)
        return formattersCache.getOrPut(bestSkeleton) {
            DateTimeFormatter.ofPattern(bestSkeleton, locale)
        }
    }
}