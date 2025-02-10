package com.abdulrahman_b.hijridatepicker.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import com.abdulrahman_b.hijridatepicker.Strings
import com.abdulrahman_b.hijridatepicker.dayContainerColor
import com.abdulrahman_b.hijridatepicker.dayContentColor
import com.abdulrahman_b.hijridatepicker.getString
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
                descriptionBuilder.append(getString(string = Strings.Companion.DateRangePickerStartHeadline))

            isEndDate ->
                descriptionBuilder.append(getString(string = Strings.Companion.DateRangePickerEndHeadline))

            isInRange ->
                descriptionBuilder.append(getString(string = Strings.Companion.DateRangePickerDayInRange))
        }
    }
    if (isToday) {
        if (descriptionBuilder.isNotEmpty()) descriptionBuilder.append(", ")
        descriptionBuilder.append(getString(string = Strings.Companion.DatePickerTodayDescription))
    }
    return if (descriptionBuilder.isEmpty()) null else descriptionBuilder.toString()
}