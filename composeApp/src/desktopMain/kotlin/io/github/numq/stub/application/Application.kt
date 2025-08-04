package io.github.numq.stub.application

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
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

private const val APP_NAME = "Stub"

private val minimumWindowSize = DpSize(768.dp, 512.dp)

fun main() {
    startKoin { modules(appModule) }

    application {
        val isSystemInDarkTheme = isSystemInDarkTheme()

        val (isDarkTheme, setIsDarkTheme) = remember(isSystemInDarkTheme) {
            mutableStateOf(isSystemInDarkTheme)
        }

        StubTheme(isDarkTheme = isDarkTheme) {
            WindowDecoration(
                isDarkTheme = isDarkTheme,
                setIsDarkTheme = setIsDarkTheme,
                initialWindowSize = minimumWindowSize,
                minimumWindowSize = minimumWindowSize,
                windowDecorationColors = WindowDecorationColors().copy(switchSchemeButton = { Color.Unspecified }),
                title = {
                    Text(APP_NAME, color = MaterialTheme.colorScheme.primary)
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