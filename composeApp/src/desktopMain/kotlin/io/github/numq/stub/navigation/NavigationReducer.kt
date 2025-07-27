package io.github.numq.stub.navigation

import io.github.numq.stub.feature.Reducer

class NavigationReducer : Reducer<NavigationCommand, NavigationState, NavigationEvent> {
    override suspend fun reduce(state: NavigationState, command: NavigationCommand) = when (command) {
        is NavigationCommand.NavigateToHub -> transition(NavigationState.Hub)
    }
}