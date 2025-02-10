package com.abdulrahman_b.hijridatepicker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.fastForEach
import com.abdulrahman_b.hijridatepicker.LocalFirstDayOfWeek
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.datepicker.RecommendedSizeForAccessibility
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import java.time.DayOfWeek

/** Composes the weekdays letters. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WeekDays(
    colors: DatePickerColors,
    firstDayOfWeek: DayOfWeek = LocalFirstDayOfWeek.current
) {
    val locale = LocalPickerLocale.current
    val firstDayOfWeek = firstDayOfWeek.value
    val weekdays = DayOfWeek.entries.map {
        it.getDisplayName(java.time.format.TextStyle.FULL, locale) to it.getDisplayName(java.time.format.TextStyle.SHORT, locale)
    }
    val dayNames = arrayListOf<Pair<String, String>>()
    // Start with firstDayOfWeek - 1 as the days are 1-based.
    for (i in firstDayOfWeek - 1 until weekdays.size) {
        dayNames.add(weekdays[i])
    }
    for (i in 0 until firstDayOfWeek - 1) {
        dayNames.add(weekdays[i])
    }
    val textStyle = DatePickerModalTokens.WeekdaysLabelTextFont

    Row(
        modifier =
            Modifier.defaultMinSize(minHeight = RecommendedSizeForAccessibility).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        dayNames.fastForEach {
            Box(
                modifier =
                    Modifier.clearAndSetSemantics { contentDescription = it.first }
                        .size(
                            width = RecommendedSizeForAccessibility,
                            height = RecommendedSizeForAccessibility
                        ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = it.second,
                    modifier = Modifier.wrapContentSize(),
                    color = colors.weekdayContentColor,
                    style = textStyle,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
