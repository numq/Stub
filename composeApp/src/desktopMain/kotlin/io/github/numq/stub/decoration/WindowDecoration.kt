package io.github.numq.stub.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import kotlin.math.roundToInt

@Composable
fun WindowDecoration(
    window: ComposeWindow,
    decorationHeight: Dp,
    isDarkTheme: Boolean,
    changeTheme: (isDarkTheme: Boolean) -> Unit,
    close: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    var lastWindowSize by remember { mutableStateOf(window.size) }

    var lastWindowLocation by remember { mutableStateOf(window.location) }

    Row(
        modifier = Modifier.fillMaxWidth().height(decorationHeight).background(MaterialTheme.colors.surface)
            .pointerInput(window.placement) {
                detectTapGestures(onDoubleTap = {
                    window.placement = when (window.placement) {
                        WindowPlacement.Floating -> {
                            lastWindowSize = window.size

                            lastWindowLocation = window.location

                            WindowPlacement.Maximized
                        }

                        else -> {
                            window.size = lastWindowSize

                            window.location = lastWindowLocation

                            WindowPlacement.Floating
                        }
                    }
                })
            }.composed {
                if (window.placement == WindowPlacement.Floating) {
                    pointerInput(window.location) {
                        var dragOffset = Offset.Zero

                        detectDragGestures(onDragStart = { initialOffset ->
                            lastWindowLocation = window.location
                            dragOffset = initialOffset
                        }, onDragCancel = {
                            window.location = lastWindowLocation
                        }, onDragEnd = {
                            lastWindowLocation = window.location
                        }) { change, _ ->
                            val dx = (change.position.x - dragOffset.x).roundToInt()
                            val dy = (change.position.y - dragOffset.y).roundToInt()
                            window.location = window.location.apply { translate(dx, dy) }
                        }
                    }
                } else this
            }, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }
        Box(modifier = Modifier.fillMaxHeight().aspectRatio(1f).clickable {
            changeTheme(!isDarkTheme)
        }, contentAlignment = Alignment.Center) {
            Icon(
                if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                null,
                tint = MaterialTheme.colors.primary
            )
        }
        Box(modifier = Modifier.fillMaxHeight().aspectRatio(1f).clickable {
            window.isMinimized = !window.isMinimized
        }, contentAlignment = Alignment.Center) {
            Icon(
                if (window.isMinimized) Icons.Default.Maximize else Icons.Default.Minimize,
                null,
                tint = MaterialTheme.colors.primary
            )
        }
        Box(
            modifier = Modifier.fillMaxHeight().aspectRatio(1f).clickable {
                window.placement = when (window.placement) {
                    WindowPlacement.Floating -> {
                        lastWindowSize = window.size

                        lastWindowLocation = window.location

                        WindowPlacement.Maximized
                    }

                    else -> {
                        window.size = lastWindowSize

                        window.location = lastWindowLocation

                        WindowPlacement.Floating
                    }
                }
            }, contentAlignment = Alignment.Center
        ) {
            Icon(
                if (window.placement == WindowPlacement.Floating) Icons.Default.Fullscreen else Icons.Default.FullscreenExit,
                null,
                tint = MaterialTheme.colors.primary
            )
        }
        Box(modifier = Modifier.fillMaxHeight().aspectRatio(1f).clickable {
            close()
        }, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Close, null, tint = MaterialTheme.colors.primary)
        }
    }
}