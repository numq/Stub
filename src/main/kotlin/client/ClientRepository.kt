package client

import descriptor.DescriptorService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import method.Method
import kotlin.coroutines.cancellation.CancellationException

interface ClientRepository {
    suspend fun invokeCallMethod(
        address: String,
        method: Method.Call,
        inputMessage: InputMessage,
        metadata: String,
    ): Result<Flow<ClientEvent>>

    suspend fun invokeStreamMethod(
        address: String,
        method: Method.Stream,
        inputMessages: Flow<InputMessage>,
        metadata: String,
    ): Result<Flow<ClientEvent>>

    class Default(
        private val channelService: ChannelService,
        private val descriptorService: DescriptorService,
        private val clientService: ClientService,
    ) : ClientRepository {
        override suspend fun invokeCallMethod(
            address: String,
            method: Method.Call,
            inputMessage: InputMessage,
            metadata: String,
        ) = descriptorService.resolveDescriptor(method = method).mapCatching { descriptor ->
            channelService.useChannel(address = address).mapCatching { channel ->
                channelFlow {
                    send(ClientEvent.Started(method = method))

                    val handledInputMessage = inputMessage.also { inputMessage ->
                        send(
                            ClientEvent.Request(
                                method = method, inputMessage = inputMessage.copy(
                                    body = inputMessage.body.takeIf(String::isNotBlank) ?: "{\n}"
                                )
                            )
                        )
                    }

                    when (method) {
                        is Method.Call.Unary -> clientService.unary(
                            channel = channel,
                            descriptor = descriptor,
                            inputMessage = handledInputMessage,
                            metadata = metadata
                        ).onSuccess { response ->
                            send(ClientEvent.Response(method = method, outputMessage = response))

                            send(ClientEvent.Completed(method = method))
                        }

                        is Method.Call.Server -> clientService.serverStreaming(
                            channel = channel,
                            descriptor = descriptor,
                            inputMessage = handledInputMessage,
                            metadata = metadata
                        ).onSuccess { responses ->
                            responses.onCompletion {
                                send(ClientEvent.Completed(method = method))
                            }.collect { outputMessage ->
                                send(ClientEvent.Response(method = method, outputMessage = outputMessage))
                            }
                        }
                    }.onFailure { throwable ->
                        when (throwable) {
                            is CancellationException -> send(ClientEvent.Cancelled(method = method))

                            else -> send(ClientEvent.Error(method = method, throwable = throwable))
                        }

                        close()
                    }
                }
            }.getOrThrow()
        }

        override suspend fun invokeStreamMethod(
            address: String,
            method: Method.Stream,
            inputMessages: Flow<InputMessage>,
            metadata: String,
        ) = descriptorService.resolveDescriptor(method = method).mapCatching { descriptor ->
            channelService.useChannel(address = address).mapCatching { channel ->
                channelFlow {
                    send(ClientEvent.Started(method = method))

                    val handledInputMessages = inputMessages.onEach { inputMessage ->
                        send(
                            ClientEvent.Request(
                                method = method, inputMessage = inputMessage.copy(
                                    body = inputMessage.body.takeIf(String::isNotBlank) ?: "{}"
                                )
                            )
                        )
                    }

                    when (method) {
                        is Method.Stream.Client -> clientService.clientStreaming(
                            channel = channel,
                            descriptor = descriptor,
                            inputMessages = handledInputMessages,
                            metadata = metadata
                        ).onSuccess { outputMessage ->
                            send(ClientEvent.Response(method = method, outputMessage = outputMessage))

                            send(ClientEvent.Completed(method = method))
                        }

                        is Method.Stream.Bidi -> clientService.bidiStreaming(
                            channel = channel,
                            descriptor = descriptor,
                            inputMessages = handledInputMessages,
                            metadata = metadata
                        ).onSuccess { responses ->
                            responses.onCompletion {
                                send(ClientEvent.Completed(method = method))
                            }.collect { outputMessage ->
                                send(ClientEvent.Response(method = method, outputMessage = outputMessage))
                            }
                        }
                    }.onFailure { throwable ->
                        when (throwable) {
                            is CancellationException -> send(ClientEvent.Cancelled(method = method))

                            else -> send(ClientEvent.Error(method = method, throwable = throwable))
                        }

                        close()
                    }
                }
            }.getOrThrow()
        }
    }
}