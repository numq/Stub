package service.feature

import client.InputMessage
import feature.Reducer
import feature.Transition
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import method.Method
import method.interactor.InvokeCallMethod
import method.interactor.InvokeStreamMethod

class ServiceCommunicationReducer(
    private val invokeCallMethod: InvokeCallMethod,
    private val invokeStreamMethod: InvokeStreamMethod,
) : Reducer<ServiceCommand.Communication, ServiceState, ServiceEvent> {
    override suspend fun reduce(
        state: ServiceState,
        command: ServiceCommand.Communication,
    ): Transition<ServiceState, ServiceEvent> = when (val method = state.selectedMethod) {
        null -> transition(state)

        else -> when (command) {
            is ServiceCommand.Communication.AddEvent -> Result.success(transition(state.copy(events = state.events + command.event)))

            is ServiceCommand.Communication.InvokeMethod -> {
                state.inputMessages?.close()

                when (method) {
                    is Method.Call -> invokeCallMethod.execute(
                        InvokeCallMethod.Input(
                            address = state.address,
                            method = method,
                            inputMessage = InputMessage(body = state.body),
                            metadata = state.metadata
                        )
                    ).mapCatching { clientEvents ->
                        transition(
                            state.copy(events = emptyList(), isConnected = true, inputMessages = null),
                            ServiceEvent.CollectEvents(events = clientEvents)
                        )
                    }

                    is Method.Stream -> {
                        val inputMessages = Channel<InputMessage>(Channel.UNLIMITED)

                        invokeStreamMethod.execute(
                            InvokeStreamMethod.Input(
                                address = state.address,
                                method = method,
                                inputMessages = inputMessages.consumeAsFlow(),
                                metadata = state.metadata
                            )
                        ).mapCatching { clientEvents ->
                            transition(
                                state.copy(events = emptyList(), isConnected = true, inputMessages = inputMessages),
                                ServiceEvent.CollectEvents(events = clientEvents)
                            )
                        }
                    }
                }
            }

            is ServiceCommand.Communication.SendRequest -> {
                if (method is Method.Stream) {
                    state.inputMessages?.send(InputMessage(body = state.body))
                }
                Result.success(transition(state))
            }

            is ServiceCommand.Communication.StopStreaming -> {
                if (method is Method.Stream) {
                    state.inputMessages?.close()
                }
                Result.success(transition(state))
            }

            is ServiceCommand.Communication.CancelMethod -> {
                if (method is Method.Stream) {
                    state.inputMessages?.cancel()
                }
                Result.success(transition(state.copy(inputMessages = null, isConnected = false)))
            }

            is ServiceCommand.Communication.CompleteMethod -> Result.success(
                transition(
                    state.copy(inputMessages = null, isConnected = false),
                    ServiceEvent.DisposeEvents
                )
            )
        }.getOrElse { transition(state) }
    }
}