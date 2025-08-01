package io.github.numq.stub.application

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import io.github.numq.stub.decoration.WindowDecoration
import io.github.numq.stub.decoration.WindowDecorationColors
import io.github.numq.stub.di.appModule
import io.github.numq.stub.navigation.NavigationView
import io.github.numq.stub.theme.StubTheme
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import java.awt.FileDialog
import java.io.FilenameFilter
import kotlin.system.exitProcess

private const val APP_NAME = "Stub"

private val minWindowSize = DpSize(768.dp, 512.dp)

private val decorationHeight = 32.dp

fun main() {
    startKoin { modules(appModule) }

    application {
        val isSystemInDarkTheme = isSystemInDarkTheme()

        val (isDarkTheme, setIsDarkTheme) = remember(isSystemInDarkTheme) {
            mutableStateOf(isSystemInDarkTheme)
        }

        StubTheme(isDarkTheme = isDarkTheme) {
            WindowDecoration(
                minWindowSize = minWindowSize,
                decorationHeight = decorationHeight,
                windowDecorationColors = WindowDecorationColors(
                    surface = MaterialTheme.colors.surface,
                    switchSchemeButton = MaterialTheme.colors.primary,
                    minimizeButton = MaterialTheme.colors.primary,
                    fullscreenButton = MaterialTheme.colors.primary,
                    closeButton = MaterialTheme.colors.primary
                ),
                isDarkTheme = isDarkTheme,
                setIsDarkTheme = setIsDarkTheme,
                close = { exitProcess(0) },
                decoration = {
                    Text(APP_NAME, color = MaterialTheme.colors.primary)
                },
                content = {
                    NavigationView(feature = koinInject(), openFileDialog = {
                        val dialog = FileDialog(this, "Upload files", FileDialog.LOAD).apply {
                            file = "*.proto"
                            filenameFilter = FilenameFilter { _, name -> name.endsWith(".proto") }
                            isMultipleMode = true
                            isVisible = true
                        }

                        if (dialog.files.isNotEmpty()) {
                            dialog.files.map { it.path }
                        } else {
                            dialog.file?.let { listOf(dialog.directory + dialog.file) } ?: emptyList()
                        }.filter { it.endsWith(".proto") }
                    })
                })
        }
    }
}