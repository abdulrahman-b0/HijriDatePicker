package com.abdulrahman_b.hijridatepicker.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.verticalScrollAxisRange
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.Strings
import com.abdulrahman_b.hijridatepicker.getString
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
                    getString(Strings.Companion.DatePickerSwitchToDaySelection)
                } else {
                    getString(Strings.Companion.DatePickerSwitchToYearSelection)
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
    selectableDates: SelectableDates,
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
        val coroutineScope = rememberCoroutineScope()
        val scrollToEarlierYearsLabel = getString(Strings.Companion.DatePickerScrollToShowEarlierYears)
        val scrollToLaterYearsLabel = getString(Strings.Companion.DatePickerScrollToShowLaterYears)
        LazyVerticalGrid(
            columns = GridCells.Fixed(YearsInRow),
            modifier =
                modifier
                    .background(containerColor)
                    // Apply this to have the screen reader traverse outside the visible list of
                    // years
                    // and not scroll them by default.
                    .semantics {
                        verticalScrollAxisRange = ScrollAxisRange(value = { 0f }, maxValue = { 0f })
                    },
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
                        )
                            .semantics {
                                // Apply a11y custom actions to the first and last items in the
                                // years
                                // grid. The actions will suggest to scroll to earlier or later
                                // years in
                                // the grid.
                                customActions =
                                    if (
                                        lazyGridState.firstVisibleItemIndex == it ||
                                        lazyGridState.layoutInfo.visibleItemsInfo
                                            .lastOrNull()
                                            ?.index == it
                                    ) {
                                        customScrollActions(
                                            state = lazyGridState,
                                            coroutineScope = coroutineScope,
                                            scrollUpLabel = scrollToEarlierYearsLabel,
                                            scrollDownLabel = scrollToLaterYearsLabel
                                        )
                                    } else {
                                        emptyList()
                                    }
                            },
                    selected = yearEntry == displayedYear,
                    currentYear = yearEntry == currentYear,
                    onClick = { onYearSelected(yearEntry) },
                    enabled = selectableDates.isSelectableYear(yearEntry),
                    description =
                        getString(Strings.Companion.DatePickerNavigateToYearDescription)
                            .format(localizedYear),
                    colors = colors
                ) {
                    Text(
                        text = localizedYear,
                        // The semantics are set at the Year level.
                        modifier = Modifier.clearAndSetSemantics {},
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



private fun customScrollActions(
    state: LazyGridState,
    coroutineScope: CoroutineScope,
    scrollUpLabel: String,
    scrollDownLabel: String
): List<CustomAccessibilityAction> {
    val scrollUpAction = {
        if (!state.canScrollBackward) {
            false
        } else {
            coroutineScope.launch { state.scrollToItem(state.firstVisibleItemIndex - YearsInRow) }
            true
        }
    }
    val scrollDownAction = {
        if (!state.canScrollForward) {
            false
        } else {
            coroutineScope.launch { state.scrollToItem(state.firstVisibleItemIndex + YearsInRow) }
            true
        }
    }
    return listOf(
        CustomAccessibilityAction(label = scrollUpLabel, action = scrollUpAction),
        CustomAccessibilityAction(label = scrollDownLabel, action = scrollDownAction)
    )
}

private const val YearsInRow: Int = 3

private val YearsVerticalPadding = 16.dp
