package com.abdulrahman_b.hijridatepicker

import com.abdulrahman_b.hijrahdatetime.DecimalStyle
import com.abdulrahman_b.hijrahdatetime.FormatLocale

actual fun Int.toLocalString(
    locale: FormatLocale,
    decimalStyle: DecimalStyle
): String {
    val formattingLocale = if (decimalStyle == DecimalStyle.Standard) {
        FormatLocale.English
    } else {
        locale
    }

    return String.format(formattingLocale.locale, "%d", this)
}