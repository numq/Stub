package service.feature

import feature.Reducer

class ServiceReducer(
    private val serviceInteractionReducer: ServiceInteractionReducer,
    private val serviceCommunicationReducer: ServiceCommunicationReducer,
) : Reducer<ServiceCommand, ServiceState, ServiceEvent> {
    override suspend fun reduce(state: ServiceState, command: ServiceCommand) = when (command) {
        is ServiceCommand.Interaction -> serviceInteractionReducer.reduce(state = state, command = command)

        is ServiceCommand.Communication -> serviceCommunicationReducer.reduce(state = state, command = command)
    }
}