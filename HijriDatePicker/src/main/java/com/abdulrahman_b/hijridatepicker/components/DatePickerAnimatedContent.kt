package com.abdulrahman_b.hijridatepicker.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.tokens.MotionTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerAnimatedContent(
    displayMode: DisplayMode,
    content: @Composable (AnimatedContentScope.(DisplayMode) -> Unit)
) {
    // Parallax effect offset that will slightly scroll in and out the navigation part of the picker
    // when the display mode changes.\
    val parallaxTarget = with(LocalDensity.current) { -48.dp.roundToPx() }
    AnimatedContent(
        targetState = displayMode,
        modifier =
            Modifier.semantics {
                @Suppress("DEPRECATION")
                isContainer = true
            },
        transitionSpec = {
            // When animating the input mode, fade out the calendar picker and slide in the text
            // field from the bottom with a delay to show up after the picker is hidden.
            if (targetState == DisplayMode.Input) {
                slideInVertically { height -> height } +
                        fadeIn(
                            animationSpec =
                                tween(
                                    durationMillis = MotionTokens.DurationShort2.toInt(),
                                    delayMillis = MotionTokens.DurationShort2.toInt()
                                )
                        ) togetherWith
                        fadeOut(tween(durationMillis = MotionTokens.DurationShort2.toInt())) +
                        slideOutVertically(targetOffsetY = { _ -> parallaxTarget })
            } else {
                // When animating the picker mode, slide out text field and fade in calendar
                // picker with a delay to show up after the text field is hidden.
                slideInVertically(
                    animationSpec = tween(delayMillis = MotionTokens.DurationShort1.toInt()),
                    initialOffsetY = { _ -> parallaxTarget }
                ) +
                        fadeIn(
                            animationSpec =
                                tween(
                                    durationMillis = MotionTokens.DurationShort2.toInt(),
                                    delayMillis = MotionTokens.DurationShort2.toInt()
                                )
                        ) togetherWith
                        slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }) +
                        fadeOut(animationSpec = tween(MotionTokens.DurationShort2.toInt()))
            }
                .using(
                    SizeTransform(
                        clip = true,
                        sizeAnimationSpec = { _, _ ->
                            tween(
                                MotionTokens.DurationLong2.toInt(),
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