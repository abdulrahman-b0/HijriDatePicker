package com.abdulrahman_b.hijridatepicker.tokens

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

internal object DatePickerModalTokens {
//    val ContainerColor = ColorSchemeKeyTokens.SurfaceContainerHigh
//    val ContainerElevation = ElevationTokens.Level3
    val ContainerHeight = 568.0.dp
//    val ContainerShape = ShapeKeyTokens.CornerExtraLarge
    val ContainerWidth = 360.0.dp
    val DateContainerHeight = 40.0.dp
    val DateContainerShape = CircleShape
    val DateContainerWidth = 40.0.dp
    val DateLabelTextFont @Composable get() = MaterialTheme.typography.bodyLarge
//    val DateSelectedContainerColor = ColorSchemeKeyTokens.Primary
//    val DateSelectedLabelTextColor = ColorSchemeKeyTokens.OnPrimary
    val DateStateLayerHeight = 40.0.dp
    val DateStateLayerShape = CircleShape
    val DateStateLayerWidth = 40.0.dp
//    val DateTodayContainerOutlineColor = ColorSchemeKeyTokens.Primary
    val DateTodayContainerOutlineWidth = 1.0.dp
//    val DateTodayLabelTextColor = ColorSchemeKeyTokens.Primary
//    val DateUnselectedLabelTextColor = ColorSchemeKeyTokens.OnSurface
    val HeaderContainerHeight = 120.0.dp
    val HeaderContainerWidth = 360.0.dp
//    val HeaderHeadlineColor = ColorSchemeKeyTokens.OnSurfaceVariant
    val HeaderHeadlineFont @Composable get() = MaterialTheme.typography.headlineLarge
//    val HeaderSupportingTextColor = ColorSchemeKeyTokens.OnSurfaceVariant
    val HeaderSupportingTextFont @Composable get() = MaterialTheme.typography.labelLarge
//    val RangeSelectionActiveIndicatorContainerColor = ColorSchemeKeyTokens.SecondaryContainer
//    val RangeSelectionActiveIndicatorContainerHeight = 40.0.dp
    val RangeSelectionActiveIndicatorContainerShape = CircleShape
//    val RangeSelectionContainerElevation = ElevationTokens.Level0
    val RangeSelectionContainerShape = RectangleShape
//    val SelectionDateInRangeLabelTextColor = ColorSchemeKeyTokens.OnSecondaryContainer
    val RangeSelectionHeaderContainerHeight = 128.0.dp
    val RangeSelectionHeaderHeadlineFont @Composable get() = MaterialTheme.typography.titleLarge
//    val RangeSelectionMonthSubheadColor = ColorSchemeKeyTokens.OnSurfaceVariant
    val RangeSelectionMonthSubheadFont @Composable get() = MaterialTheme.typography.titleSmall
//    val WeekdaysLabelTextColor = ColorSchemeKeyTokens.OnSurface
    val WeekdaysLabelTextFont @Composable get() = MaterialTheme.typography.bodyLarge
    val SelectionYearContainerHeight = 36.0.dp
    val SelectionYearContainerWidth = 72.0.dp
    val SelectionYearLabelTextFont @Composable get() = MaterialTheme.typography.bodyLarge
//    val SelectionYearSelectedContainerColor = ColorSchemeKeyTokens.Primary
//    val SelectionYearSelectedLabelTextColor = ColorSchemeKeyTokens.OnPrimary
    val SelectionYearStateLayerHeight = 36.0.dp
    val SelectionYearStateLayerShape = CircleShape
    val SelectionYearStateLayerWidth = 72.0.dp
//    val SelectionYearUnselectedLabelTextColor = ColorSchemeKeyTokens.OnSurfaceVariant
}