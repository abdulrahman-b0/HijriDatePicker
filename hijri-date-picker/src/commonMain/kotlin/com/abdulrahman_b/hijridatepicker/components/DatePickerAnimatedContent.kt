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


import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.tokens.MotionTokens

/**
 * Displays animated content for the date picker based on the display mode.
 *
 * @param displayMode The current display mode of the date picker.
 * @param content The composable content to be displayed within the animated content scope.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerAnimatedContent(
    displayMode: DisplayMode,
    content: @Composable (AnimatedContentScope.(DisplayMode) -> Unit)
) {
    // Parallax effect offset that will slightly scroll in and out the navigation part of the picker
    // when the display mode changes.
    val parallaxTarget = with(LocalDensity.current) { -48.dp.roundToPx() }
    AnimatedContent(
        targetState = displayMode,
        modifier =
            Modifier.semantics { isTraversalGroup = true },
        transitionSpec = {
            // When animating the input mode, fade out the calendar picker and slide in the text
            // field from the bottom with a delay to show up after the picker is hidden.
            if (targetState == DisplayMode.Input) {
                slideInVertically { height -> height } +
                        fadeIn(
                            animationSpec =
                                tween(
                                    durationMillis = MotionTokens.DURATION_100.toInt(),
                                    delayMillis = MotionTokens.DURATION_100.toInt()
                                )
                        ) togetherWith
                        fadeOut(tween(durationMillis = MotionTokens.DURATION_100.toInt())) +
                        slideOutVertically(targetOffsetY = { _ -> parallaxTarget })
            } else {
                // When animating the picker mode, slide out text field and fade in calendar
                // picker with a delay to show up after the text field is hidden.
                slideInVertically(
                    animationSpec = tween(delayMillis = MotionTokens.DURATION_50.toInt()),
                    initialOffsetY = { _ -> parallaxTarget }
                ) +
                        fadeIn(
                            animationSpec =
                                tween(
                                    durationMillis = MotionTokens.DURATION_100.toInt(),
                                    delayMillis = MotionTokens.DURATION_100.toInt()
                                )
                        ) togetherWith
                        slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }) +
                        fadeOut(animationSpec = tween(MotionTokens.DURATION_100.toInt()))
            }
                .using(
                    SizeTransform(
                        clip = true,
                        sizeAnimationSpec = { _, _ ->
                            tween(
                                MotionTokens.DURATION_500.toInt(),
                                easing = MotionTokens.EasingEmphasizedDecelerateCubicBezier
                            )
                        }
                    )
                )
        },
        label = "DatePickerDisplayModeAnimation",
        content = content
    )
}