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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import com.abdulrahman_b.hijridatepicker.R
import com.abdulrahman_b.hijridatepicker.dayContainerColor
import com.abdulrahman_b.hijridatepicker.dayContentColor
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Day(
    modifier: Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    animateChecked: Boolean,
    enabled: Boolean,
    today: Boolean,
    inRange: Boolean,
    description: String,
    colors: DatePickerColors,
    content: @Composable () -> Unit
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier =
            modifier
                // Apply and merge semantics here. This will ensure that when scrolling the list the
                // entire Day surface is treated as one unit and holds the date semantics even when
                // it's
                // not completely visible atm.
                .semantics(mergeDescendants = true) {
                    text = AnnotatedString(description)
                    role = Role.Button
                },
        enabled = enabled,
        shape = DatePickerModalTokens.DateContainerShape,
        color =
            colors
                .dayContainerColor(selected = selected, enabled = enabled, animate = animateChecked)
                .value,
        contentColor =
            colors
                .dayContentColor(
                    isToday = today,
                    selected = selected,
                    inRange = inRange,
                    enabled = enabled,
                )
                .value,
        border =
            if (today && !selected) {
                BorderStroke(
                    DatePickerModalTokens.DateTodayContainerOutlineWidth,
                    colors.todayDateBorderColor
                )
            } else {
                null
            }
    ) {
        Box(
            modifier =
                Modifier.requiredSize(
                    DatePickerModalTokens.DateStateLayerWidth,
                    DatePickerModalTokens.DateStateLayerHeight
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}



@Composable
internal fun dayContentDescription(
    rangeSelectionEnabled: Boolean,
    isToday: Boolean,
    isStartDate: Boolean,
    isEndDate: Boolean,
    isInRange: Boolean
): String? {
    val descriptionBuilder = StringBuilder()
    if (rangeSelectionEnabled) {
        when {
            isStartDate ->
                descriptionBuilder.append(stringResource(R.string.date_range_picker_start_headline))

            isEndDate ->
                descriptionBuilder.append(stringResource(R.string.date_range_picker_end_headline))

            isInRange ->
                descriptionBuilder.append(stringResource(R.string.date_range_picker_day_in_range))
        }
    }
    if (isToday) {
        if (descriptionBuilder.isNotEmpty()) descriptionBuilder.append(", ")
        descriptionBuilder.append(stringResource(R.string.date_picker_today_description))
    }
    return if (descriptionBuilder.isEmpty()) null else descriptionBuilder.toString()
}