package io.github.numq.stub.application

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.window.singleWindowApplication
import io.github.numq.stub.di.appModule
import io.github.numq.stub.navigation.Navigation
import io.github.numq.stub.theme.StubTheme
import org.koin.core.context.startKoin
import java.awt.FileDialog
import java.io.FilenameFilter

const val appName = "Stub"

fun main() {
    startKoin {
        modules(appModule)
    }

    singleWindowApplication(title = appName) {
        StubTheme(isDarkTheme = isSystemInDarkTheme()) {
            Navigation(openFileDialog = {
                val dialog = FileDialog(window, "Upload files", FileDialog.LOAD).apply {
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
        }
    }
}