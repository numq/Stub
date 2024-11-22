package service.feature

import feature.Reducer
import generation.GenerateRandomRequest

class ServiceInteractionReducer(
    private val generateRandomRequest: GenerateRandomRequest,
    private val serviceCommunicationReducer: ServiceCommunicationReducer,
) : Reducer<ServiceCommand.Interaction, ServiceState, ServiceEvent> {
    override suspend fun reduce(state: ServiceState, command: ServiceCommand.Interaction) = when (command) {
        is ServiceCommand.Interaction.ExpandMethodsMenu -> transition(state.copy(methodsMenuExpanded = true))

        is ServiceCommand.Interaction.ShrinkMethodsMenu -> transition(state.copy(methodsMenuExpanded = false))

        is ServiceCommand.Interaction.SelectMethod -> if (command.method != state.selectedMethod) {
            serviceCommunicationReducer.reduce(
                state = state.copy(selectedMethod = command.method),
                command = ServiceCommand.Communication.CancelMethod
            )
        } else transition(state)

        is ServiceCommand.Interaction.DeselectMethod -> transition(state.copy(selectedMethod = null))

        is ServiceCommand.Interaction.ChangeAddress -> transition(state.copy(address = command.address))

        is ServiceCommand.Interaction.ChangeBody -> transition(state.copy(body = command.body))

        is ServiceCommand.Interaction.ChangeMetadata -> transition(state.copy(metadata = command.metadata))

        is ServiceCommand.Interaction.GenerateRandomBody -> generateRandomRequest.execute(
            GenerateRandomRequest.Input(method = command.method)
        ).fold(onFailure = {
            transition(state)
        }, onSuccess = { body ->
            transition(state.copy(body = body))
        })
    }
}