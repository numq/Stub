package io.github.numq.stub.interaction

import io.github.numq.stub.client.InputMessage
import io.github.numq.stub.feature.Reducer
import io.github.numq.stub.feature.Transition
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import io.github.numq.stub.method.Method
import io.github.numq.stub.method.InvokeCallMethod
import io.github.numq.stub.method.InvokeStreamMethod

class InteractionCommunicationReducer(
    private val invokeCallMethod: InvokeCallMethod,
    private val invokeStreamMethod: InvokeStreamMethod,
) : Reducer<InteractionCommand.Communication, InteractionState, InteractionEvent> {
    override suspend fun reduce(
        state: InteractionState,
        command: InteractionCommand.Communication,
    ): Transition<InteractionState, InteractionEvent> = when (val method = state.selectedMethod) {
        null -> transition(state)

        else -> when (command) {
            is InteractionCommand.Communication.AddEvent -> Result.success(transition(state.copy(events = state.events + command.event)))

            is InteractionCommand.Communication.InvokeMethod -> {
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
                            InteractionEvent.CollectEvents(events = clientEvents)
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
                                InteractionEvent.CollectEvents(events = clientEvents)
                            )
                        }
                    }
                }
            }

            is InteractionCommand.Communication.SendRequest -> {
                if (method is Method.Stream) {
                    state.inputMessages?.send(InputMessage(body = state.body))
                }
                Result.success(transition(state))
            }

            is InteractionCommand.Communication.StopStreaming -> {
                if (method is Method.Stream) {
                    state.inputMessages?.close()
                }
                Result.success(transition(state))
            }

            is InteractionCommand.Communication.CancelMethod -> {
                if (method is Method.Stream) {
                    state.inputMessages?.cancel()
                }
                Result.success(transition(state.copy(inputMessages = null, isConnected = false)))
            }

            is InteractionCommand.Communication.CompleteMethod -> Result.success(
                transition(
                    state.copy(inputMessages = null, isConnected = false),
                    InteractionEvent.DisposeEvents
                )
            )
        }.getOrElse { transition(state) }
    }
}