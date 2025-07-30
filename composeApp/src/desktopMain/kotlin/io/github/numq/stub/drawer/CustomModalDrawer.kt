package io.github.numq.stub.drawer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CustomModalDrawer(
    modifier: Modifier,
    isOpen: Boolean,
    openDrawer: (Boolean) -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier) {
        val drawerWidthDp = maxWidth * 0.8f

        val drawerWidthPx = with(density) { drawerWidthDp.toPx() }

        val offsetX = remember(drawerWidthPx) {
            Animatable(if (isOpen) 0f else -drawerWidthPx)
        }

        var currentDrawerWidth by remember { mutableStateOf(drawerWidthPx) }

        LaunchedEffect(drawerWidthPx, isOpen) {
            if (isOpen) {
                val progress = ((offsetX.value + currentDrawerWidth) / currentDrawerWidth).coerceIn(0f, 1f)
                currentDrawerWidth = drawerWidthPx
                val newOffset = -currentDrawerWidth + currentDrawerWidth * progress
                offsetX.snapTo(newOffset)
            } else {
                currentDrawerWidth = drawerWidthPx
                offsetX.snapTo(-currentDrawerWidth)
            }
        }

        LaunchedEffect(isOpen, currentDrawerWidth) {
            val target = if (isOpen) 0f else -currentDrawerWidth

            offsetX.animateTo(target, tween(300))
        }

        content()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = ((offsetX.value + currentDrawerWidth) / currentDrawerWidth * 0.5f)
                            .coerceIn(0f, 0.5f)
                    )
                )
                .clickable(
                    enabled = offsetX.value > -currentDrawerWidth,
                    interactionSource = null,
                    indication = null
                ) {
                    coroutineScope.launch {
                        offsetX.animateTo(-currentDrawerWidth)

                        openDrawer(false)
                    }
                }
                .pointerInput(currentDrawerWidth) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, delta ->
                            coroutineScope.launch {
                                val newOffset = (offsetX.value + delta).coerceIn(-currentDrawerWidth, 0f)
                                offsetX.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                val shouldOpen = offsetX.value > -currentDrawerWidth / 2f

                                offsetX.animateTo(if (shouldOpen) 0f else -currentDrawerWidth, tween(300))

                                openDrawer(shouldOpen)
                            }
                        }
                    )
                }
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(drawerWidthDp)
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .background(MaterialTheme.colors.surface)
                .pointerInput(currentDrawerWidth) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, delta ->
                            coroutineScope.launch {
                                val newOffset = (offsetX.value + delta).coerceIn(-currentDrawerWidth, 0f)
                                offsetX.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                val shouldOpen = offsetX.value > -currentDrawerWidth / 2f

                                offsetX.animateTo(if (shouldOpen) 0f else -currentDrawerWidth, tween(300))

                                openDrawer(shouldOpen)
                            }
                        }
                    )
                }
        ) {
            drawerContent()
        }
    }
}