package io.github.numq.stub.navigation

sealed interface NavigationState {
    data object Hub : NavigationState
}