package com.abdulrahman_b.hijridatepicker

import com.abdulrahman_b.hijrahdatetime.DecimalStyle
import com.abdulrahman_b.hijrahdatetime.FormatLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

actual fun Int.toLocalString(
    locale: FormatLocale,
    decimalStyle: DecimalStyle
): String {
    val formatter = NSNumberFormatter()
    formatter.locale = locale.nsLocale
    formatter.maximumFractionDigits = 0u
    formatter.minimumFractionDigits = 0u

    return formatter.stringFromNumber(NSNumber(this)) ?: toString()
}