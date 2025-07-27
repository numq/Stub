package io.github.numq.stub.navigation

sealed interface NavigationState {
    // todo

    data object Splash : NavigationState

    data object Hub : NavigationState
}