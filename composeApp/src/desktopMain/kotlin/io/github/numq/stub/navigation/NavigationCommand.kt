package io.github.numq.stub.navigation

sealed interface NavigationCommand {
    data object NavigateToHub : NavigationCommand
}