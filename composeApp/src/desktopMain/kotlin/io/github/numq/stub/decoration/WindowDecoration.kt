package io.github.numq.stub.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import java.awt.*
import java.awt.event.WindowStateListener
import javax.swing.SwingUtilities
import kotlin.math.roundToInt

@Composable
fun WindowDecoration(
    minWindowSize: DpSize = DpSize(512.dp, 512.dp),
    decorationHeight: Dp,
    windowDecorationColors: WindowDecorationColors,
    isDarkTheme: Boolean,
    setIsDarkTheme: (Boolean) -> Unit,
    close: () -> Unit,
    decoration: @Composable RowScope.() -> Unit,
    content: @Composable (ComposeWindow.(WindowDecorationState) -> Unit),
) {
    val windowState = rememberWindowState()

    var isMinimized by remember { mutableStateOf(false) }

    var isFullscreen by remember { mutableStateOf(false) }

    val state = remember(isMinimized, isFullscreen) {
        WindowDecorationState(
            size = windowState.size,
            position = DpOffset(x = windowState.position.x, y = windowState.position.y),
            isMinimized = isMinimized,
            isFullscreen = isFullscreen,
        )
    }

    Window(onCloseRequest = close, state = windowState, undecorated = true, transparent = true) {
        var lastWindowBounds by remember { mutableStateOf(window.bounds) }

        var lastWindowLocation by remember { mutableStateOf(window.location) }

        LaunchedEffect(Unit) {
            SwingUtilities.invokeLater {
                window.minimumSize = Dimension(minWindowSize.width.value.toInt(), minWindowSize.height.value.toInt())

                window.size = window.minimumSize
            }
        }

        DisposableEffect(window) {
            val listener = WindowStateListener { event ->
                isMinimized = (event.newState and Frame.ICONIFIED) != 0
            }

            window.addWindowStateListener(listener)

            onDispose {
                window.removeWindowStateListener(listener)
            }
        }

        LaunchedEffect(isFullscreen) {
            SwingUtilities.invokeLater {
                val windowCenter = Point(
                    window.x + window.width / 2, window.y + window.height / 2
                )

                val screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices.firstOrNull {
                    it.defaultConfiguration.bounds.contains(windowCenter)
                }

                when {
                    isFullscreen && screenDevice != null -> {
                        lastWindowBounds = window.bounds

                        val config = screenDevice.defaultConfiguration

                        val screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(config)

                        val screenBounds = config.bounds

                        window.bounds = Rectangle(
                            screenBounds.x + screenInsets.left,
                            screenBounds.y + screenInsets.top,
                            screenBounds.width - screenInsets.left - screenInsets.right,
                            screenBounds.height - screenInsets.top - screenInsets.bottom
                        )
                    }

                    !isFullscreen && window.bounds != lastWindowBounds -> window.bounds = lastWindowBounds
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().height(decorationHeight)
                .background(windowDecorationColors.surface).pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        isFullscreen = !isFullscreen
                    })
                }.pointerInput(Unit) {
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

                        SwingUtilities.invokeLater {
                            window.location = window.location.apply { translate(dx, dy) }
                        }
                    }
                }, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        decoration()
                    }
                }
                if (windowDecorationColors.switchSchemeButton.isSpecified) {
                    Box(
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f)
                            .clickable {
                                setIsDarkTheme(!isDarkTheme)
                            }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            null,
                            tint = windowDecorationColors.switchSchemeButton
                        )
                    }
                }
                if (windowDecorationColors.minimizeButton.isSpecified) {
                    Box(
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f).clickable {
                            SwingUtilities.invokeLater {
                                window.extendedState = Frame.ICONIFIED
                            }
                        }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Minimize, null, tint = windowDecorationColors.minimizeButton
                        )
                    }
                }
                if (windowDecorationColors.fullscreenButton.isSpecified) {
                    Box(
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f).clickable {
                            isFullscreen = !isFullscreen
                        }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            null,
                            tint = windowDecorationColors.fullscreenButton
                        )
                    }
                }
                if (windowDecorationColors.closeButton.isSpecified) {
                    Box(
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f).clickable {
                            close()
                        }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            null,
                            tint = windowDecorationColors.closeButton
                        )
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                content(window, state)
            }
        }
    }
}