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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.R
import com.abdulrahman_b.hijridatepicker.toLocalString
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import com.abdulrahman_b.hijridatepicker.yearContainerColor
import com.abdulrahman_b.hijridatepicker.yearContentColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
internal fun YearPickerMenuButton(
    onClick: () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
        elevation = null,
        border = null,
    ) {
        content()
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Icon(
            Icons.Filled.ArrowDropDown,
            contentDescription =
                if (expanded) {
                    stringResource(R.string.date_picker_switch_to_day_selection)
                } else {
                    stringResource(R.string.date_picker_switch_to_year_selection)
                },
            Modifier.rotate(if (expanded) 180f else 0f)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun YearPicker(
    modifier: Modifier,
    currentYear: Int,
    displayedYear: Int,
    onYearSelected: (year: Int) -> Unit,
    selectableDates: HijriSelectableDates,
    yearRange: IntRange,
    colors: DatePickerColors
) {
    ProvideTextStyle(value = DatePickerModalTokens.SelectionYearLabelTextFont) {
        val lazyGridState =
            rememberLazyGridState(
                // Set the initial index to a few years before the current year to allow quicker
                // selection of previous years.
                initialFirstVisibleItemIndex = max(0, displayedYear - yearRange.first - YearsInRow)
            )
        // Match the years container color to any elevated surface color that is composed under it.
        val containerColor = colors.containerColor
        LazyVerticalGrid(
            columns = GridCells.Fixed(YearsInRow),
            modifier = modifier.background(containerColor),
            state = lazyGridState,
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(YearsVerticalPadding)
        ) {
            items(yearRange.count()) {
                val yearEntry = it + yearRange.first
                val localizedYear = yearEntry.toLocalString()
                Year(
                    modifier =
                        Modifier.requiredSize(
                            width = DatePickerModalTokens.SelectionYearContainerWidth,
                            height = DatePickerModalTokens.SelectionYearContainerHeight
                        ),
                    selected = yearEntry == displayedYear,
                    currentYear = yearEntry == currentYear,
                    onClick = { onYearSelected(yearEntry) },
                    enabled = selectableDates.isSelectableYear(yearEntry),
                    description =
                        stringResource(R.string.date_picker_navigate_to_year_description)
                            .format(localizedYear),
                    colors = colors
                ) {
                    Text(
                        text = localizedYear,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Year(
    modifier: Modifier,
    selected: Boolean,
    currentYear: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    description: String,
    colors: DatePickerColors,
    content: @Composable () -> Unit
) {
    val border =
        remember(currentYear, selected) {
            if (currentYear && !selected) {
                // Use the day's spec to draw a border around the current year.
                BorderStroke(
                    DatePickerModalTokens.DateTodayContainerOutlineWidth,
                    colors.todayDateBorderColor
                )
            } else {
                null
            }
        }
    Surface(
        selected = selected,
        onClick = onClick,
        // Apply and merge semantics here. This will ensure that when scrolling the list the entire
        // Year surface is treated as one unit and holds the date semantics even when it's not
        // completely visible atm.
        modifier =
            modifier.semantics(mergeDescendants = true) {
                text = AnnotatedString(description)
                role = Role.Button
            },
        enabled = enabled,
        shape = DatePickerModalTokens.SelectionYearStateLayerShape,
        color = colors.yearContainerColor(selected = selected, enabled = enabled).value,
        contentColor =
            colors
                .yearContentColor(currentYear = currentYear, selected = selected, enabled = enabled)
                .value,
        border = border,
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { content() }
    }
}



private const val YearsInRow: Int = 3

private val YearsVerticalPadding = 16.dp
