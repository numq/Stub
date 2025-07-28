package io.github.numq.stub.application

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import io.github.numq.stub.di.appModule
import io.github.numq.stub.navigation.NavigationView
import io.github.numq.stub.theme.StubTheme
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import java.awt.Dimension
import java.awt.FileDialog
import java.io.FilenameFilter

private const val APP_NAME = "Stub"

private val windowSize = DpSize(768.dp, 768.dp)

private val windowState = WindowState(position = WindowPosition(Alignment.Center), size = windowSize)

fun main() {
    startKoin { modules(appModule) }

    singleWindowApplication(state = windowState, title = APP_NAME) {
        window.minimumSize = Dimension(windowSize.width.value.toInt(), windowSize.height.value.toInt())

        val isSystemInDarkTheme = isSystemInDarkTheme()

        StubTheme(isDarkTheme = isSystemInDarkTheme) {
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                NavigationView(feature = koinInject(), openFileDialog = {
                    val dialog = FileDialog(window, "Upload files", FileDialog.LOAD).apply {
                        file = "*.proto"
                        filenameFilter = FilenameFilter { _, name -> name.endsWith(".proto") }
                        isMultipleMode = true
                        isVisible = true
                    }

                    if (dialog.files.isNotEmpty()) {
                        dialog.files.map { it.path }.also { println(it.size) }
                    } else {
                        dialog.file?.let { listOf(dialog.directory + dialog.file) } ?: emptyList()
                    }.filter { it.endsWith(".proto") }.also { println(it.size) }
                })
            }
        }
    }
}