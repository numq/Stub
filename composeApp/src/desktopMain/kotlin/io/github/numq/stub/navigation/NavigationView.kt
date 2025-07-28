package io.github.numq.stub.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.numq.stub.hub.HubView

@Composable
fun NavigationView(feature: NavigationFeature, openFileDialog: () -> List<String>) {
    val state by feature.state.collectAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        if (state is NavigationState.Hub) {
            HubView(openFileDialog = openFileDialog)
        }
    }
}