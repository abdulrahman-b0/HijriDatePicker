package com.abdulrahman_b.hijridatepicker
/*
* Copyright 2023 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


import android.text.format.DateFormat
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.format.DecimalStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
internal class HijriDatePickerFormatter(
    val yearSelectionSkeleton: String,
    val selectedDateSkeleton: String,
    val selectedDateDescriptionSkeleton: String,
    val inputDateSkeleton: String,
    val inputDateDelimiter: Char
) : DatePickerFormatter {

    private val formattersCache = mutableMapOf<String, DateTimeFormatter>()

    override fun formatDate(dateMillis: Long?, locale: Locale, forContentDescription: Boolean): String? {
        throw UnsupportedOperationException("This method is not supported for HijriDatePickerFormatter")
    }

    override fun formatMonthYear(monthMillis: Long?, locale: Locale): String? {
        throw UnsupportedOperationException("This method is not supported for HijriDatePickerFormatter")
    }

    fun formatDate(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle, forContentDescription: Boolean = false): String? {

        if (date == null) return null
        val skeleton = if (forContentDescription) selectedDateDescriptionSkeleton else selectedDateSkeleton

        return getOrCreateFormatter(skeleton, locale, decimalStyle).format(date)
    }

    fun formatInputDateWithoutDelimiters(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle): String? {
        if (date == null) return null
        return getOrCreateFormatter(
            skeleton = inputDateSkeleton.replace(inputDateDelimiter.toString(), ""),
            locale = locale,
            decimalStyle = decimalStyle,
            applyBestPattern = false
        ).format(date)
    }

    fun formatMonthYear(date: HijrahDate?, locale: Locale, decimalStyle: DecimalStyle): String? {
        if (date == null) return null
        return getOrCreateFormatter(yearSelectionSkeleton, locale, decimalStyle).format(date)
    }

    fun parseDateWithoutDelimiters(text: String, locale: Locale, decimalStyle: DecimalStyle): Result<HijrahDate> {
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