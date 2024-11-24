package application

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.window.singleWindowApplication
import di.appModule
import navigation.Navigation
import org.koin.core.context.startKoin
import theme.StubTheme
import java.awt.FileDialog
import java.io.File

const val appName = "Stub"

fun main() {
    startKoin {
        modules(appModule)
    }

    singleWindowApplication(title = appName) {
        StubTheme(isDarkTheme = isSystemInDarkTheme()) {
            Navigation(openFileDialog = {
                FileDialog(window, "Upload files", FileDialog.LOAD).apply {
                    setFilenameFilter { _, name -> name.endsWith(".proto") }
                    isMultipleMode = true
                    isVisible = true
                }.files.filter { file -> file.extension == "proto" }.map(File::getPath)
            })
        }
    }
}