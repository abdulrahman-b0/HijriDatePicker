package com.abdulrahman_b.hijridatepicker

import androidx.compose.material3.CalendarLocale
import com.abdulrahman_b.hijrahdatetime.format.DecimalStyle
import com.abdulrahman_b.hijrahdatetime.format.FormatLocale
import com.abdulrahman_b.hijrahdatetime.format.FormatLocales

actual fun Int.toLocalString(
    locale: FormatLocale,
    decimalStyle: DecimalStyle
): String {
    val formattingLocale = if (decimalStyle == DecimalStyle.Standard) {
        FormatLocales.English
    } else {
        locale
    }

    return String.format(formattingLocale, "%d", this)
}

actual fun FormatLocale.toMaterial3CalendarLocale(): CalendarLocale = this