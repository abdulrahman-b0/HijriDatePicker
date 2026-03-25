package com.abdulrahman_b.hijridatepicker

import androidx.compose.material3.CalendarLocale
import com.abdulrahman_b.hijrahdatetime.format.DecimalStyle
import com.abdulrahman_b.hijrahdatetime.format.FormatLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

actual fun Int.toLocalString(
    locale: FormatLocale,
    decimalStyle: DecimalStyle
): String {
    val formatter = NSNumberFormatter()
    formatter.locale = locale
    formatter.maximumFractionDigits = 0u
    formatter.minimumFractionDigits = 0u

    return formatter.stringFromNumber(NSNumber(this)) ?: toString()
}

actual fun FormatLocale.toMaterial3CalendarLocale(): CalendarLocale = this