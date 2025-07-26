package io.github.numq.stub.service.feature

import io.github.numq.stub.feature.Feature
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import io.github.numq.stub.service.Service

class ServiceFeature(
    service: Service,
    reducer: ServiceReducer,
) : Feature<ServiceCommand, ServiceState, ServiceEvent>(
    initialState = ServiceState(
        service = service,
        methodsMenuExpanded = false,
        selectedMethod = null,
        isConnected = false,
        events = emptyList(),
        address = "127.0.0.1:8000",
        body = "",
        metadata = ""
    ), reducer = reducer
) {
    init {
        var eventsJob: Job? = null

        events.onEach { event ->
            when (event) {
                is ServiceEvent.CollectEvents -> {
                    eventsJob = event.events.onEach { clientEvent ->
                        execute(ServiceCommand.Communication.AddEvent(event = clientEvent))
                    }.onCompletion {
                        execute(ServiceCommand.Communication.CompleteMethod)
                    }.launchIn(featureScope)
                }

                is ServiceEvent.DisposeEvents -> {
                    eventsJob?.cancel()
                    eventsJob = null
                }
            }
        }.launchIn(featureScope)
    }
}