package io.github.numq.stub.navigation

import io.github.numq.stub.feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class NavigationFeature(reducer: NavigationReducer) : Feature<NavigationCommand, NavigationState, NavigationEvent>(
    initialState = NavigationState.Hub,
    coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
    reducer = reducer
)