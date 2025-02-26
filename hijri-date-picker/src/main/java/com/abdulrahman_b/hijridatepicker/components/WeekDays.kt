package com.abdulrahman_b.hijridatepicker.components
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.fastForEach
import com.abdulrahman_b.hijridatepicker.LocalDayOfWeekTextStyle
import com.abdulrahman_b.hijridatepicker.LocalFirstDayOfWeek
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.datepicker.RecommendedSizeForAccessibility
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import java.time.DayOfWeek
import java.time.format.TextStyle

/** Composes the weekdays letters. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WeekDays(
    colors: DatePickerColors,
    firstDayOfWeek: DayOfWeek = LocalFirstDayOfWeek.current,
    dayOfWeekTextStyle: TextStyle = LocalDayOfWeekTextStyle.current
) {
    val locale = LocalPickerLocale.current
    val firstDayOfWeek = firstDayOfWeek.value
    val weekdays = DayOfWeek.entries.map {
        it.getDisplayName(TextStyle.FULL, locale) to it.getDisplayName(dayOfWeekTextStyle, locale)
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
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
