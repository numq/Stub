package application

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.singleWindowApplication
import di.appModule
import navigation.Navigation
import org.koin.core.context.startKoin
import java.awt.FileDialog
import java.io.File

fun main() {
    startKoin {
        modules(appModule)
    }

    singleWindowApplication {
        MaterialTheme {
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