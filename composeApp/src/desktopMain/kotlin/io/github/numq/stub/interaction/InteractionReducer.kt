package io.github.numq.stub.interaction

import io.github.numq.stub.feature.Reducer
import io.github.numq.stub.generation.GenerateRandomRequest

class InteractionReducer(
    private val generateRandomRequest: GenerateRandomRequest,
    private val interactionCommunicationReducer: InteractionCommunicationReducer,
) : Reducer<InteractionCommand, InteractionState, InteractionEvent> {
    override suspend fun reduce(state: InteractionState, command: InteractionCommand) = when (command) {
        is InteractionCommand.ExpandMethodsMenu -> transition(state.copy(methodsMenuExpanded = true))

        is InteractionCommand.ShrinkMethodsMenu -> transition(state.copy(methodsMenuExpanded = false))

        is InteractionCommand.SelectMethod -> if (command.method != state.selectedMethod) {
            interactionCommunicationReducer.reduce(
                state = state.copy(selectedMethod = command.method),
                command = InteractionCommand.Communication.CancelMethod
            )
        } else transition(state)

        is InteractionCommand.DeselectMethod -> transition(state.copy(selectedMethod = null))

        is InteractionCommand.ChangeAddress -> transition(state.copy(address = command.address))

        is InteractionCommand.ChangeBody -> transition(state.copy(body = command.body))

        is InteractionCommand.ChangeMetadata -> transition(state.copy(metadata = command.metadata))

        is InteractionCommand.GenerateRandomBody -> generateRandomRequest.execute(
            GenerateRandomRequest.Input(method = command.method)
        ).fold(onFailure = {
            transition(state)
        }, onSuccess = { body ->
            transition(state.copy(body = body))
        })

        is InteractionCommand.Communication -> interactionCommunicationReducer.reduce(state = state, command = command)
    }
}