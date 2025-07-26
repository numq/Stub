package io.github.numq.stub.navigation

import androidx.compose.runtime.Composable
import hub.HubScreen

@Composable
fun Navigation(openFileDialog: () -> List<String>) {
    HubScreen(openFileDialog = openFileDialog)
}