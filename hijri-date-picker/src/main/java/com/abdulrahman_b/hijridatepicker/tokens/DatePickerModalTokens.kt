package com.abdulrahman_b.hijridatepicker.tokens

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

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

internal object DatePickerModalTokens {
    val ContainerWidth = 360.0.dp
    val DateContainerShape = CircleShape
    val DateLabelTextFont @Composable get() = MaterialTheme.typography.bodyLarge
    val DateStateLayerHeight = 40.0.dp
    val DateStateLayerWidth = 40.0.dp
    val DateTodayContainerOutlineWidth = 1.0.dp
    val HeaderContainerHeight = 120.0.dp
    val HeaderHeadlineFont @Composable get() = MaterialTheme.typography.headlineLarge
    val HeaderSupportingTextFont @Composable get() = MaterialTheme.typography.labelLarge
    val RangeSelectionHeaderContainerHeight = 128.0.dp
    val RangeSelectionHeaderHeadlineFont @Composable get() = MaterialTheme.typography.titleLarge
    val RangeSelectionMonthSubheadFont @Composable get() = MaterialTheme.typography.titleSmall
    val WeekdaysLabelTextFont @Composable get() = MaterialTheme.typography.bodyLarge
    val SelectionYearContainerHeight = 36.0.dp
    val SelectionYearContainerWidth = 72.0.dp
    val SelectionYearLabelTextFont @Composable get() = MaterialTheme.typography.bodyLarge
    val SelectionYearStateLayerShape = CircleShape
}