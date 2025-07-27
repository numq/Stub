package io.github.numq.stub.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import io.github.numq.stub.hub.HubView
import io.github.numq.stub.splash.SplashView
import kotlinx.coroutines.launch

@Composable
fun NavigationView(feature: NavigationFeature, openFileDialog: () -> List<String>) {
    val coroutineScope = rememberCoroutineScope()

    val state by feature.state.collectAsState()

    val slideAnimationSpec = remember<FiniteAnimationSpec<IntOffset>> {
        tween(durationMillis = 500, easing = LinearEasing)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        if (state is NavigationState.Hub) {
            HubView(openFileDialog = openFileDialog)
        }

        // todo

        AnimatedVisibility(
            visible = state is NavigationState.Splash,
            enter = slideInHorizontally(animationSpec = slideAnimationSpec) { -it },
            exit = slideOutHorizontally(animationSpec = slideAnimationSpec) { it },
            modifier = Modifier.fillMaxSize()
        ) {
            SplashView {
                coroutineScope.launch {
                    feature.execute(NavigationCommand.NavigateToHub)
                }
            }
        }
    }
}