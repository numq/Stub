package io.github.numq.stub.application

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.window.singleWindowApplication
import io.github.numq.stub.di.appModule
import io.github.numq.stub.navigation.Navigation
import io.github.numq.stub.theme.StubTheme
import org.koin.core.context.startKoin
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter

const val appName = "Stub"

fun main() {
    startKoin {
        modules(appModule)
    }

    singleWindowApplication(title = appName) {
        StubTheme(isDarkTheme = isSystemInDarkTheme()) {
            Navigation(openFileDialog = {
                FileDialog(window, "Upload files", FileDialog.LOAD).apply {
                    file = "*.proto"
                    filenameFilter = FilenameFilter { _, name -> name.endsWith(".proto") }
                    isMultipleMode = true
                    isVisible = true
                }.files.filter { file -> file.extension == "proto" }.map(File::getPath)
            })
        }
    }
}