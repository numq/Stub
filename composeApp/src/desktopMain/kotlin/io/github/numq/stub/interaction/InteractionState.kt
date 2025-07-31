package io.github.numq.stub.interaction

import io.github.numq.stub.client.ClientEvent
import io.github.numq.stub.client.InputMessage
import io.github.numq.stub.method.Method
import io.github.numq.stub.service.Service
import kotlinx.coroutines.channels.Channel

data class InteractionState(
    val service: Service,
    val methodsMenuExpanded: Boolean = false,
    val selectedMethod: Method? = null,
    val isConnected: Boolean = false,
    val events: List<ClientEvent> = emptyList(),
    val address: String = "",
    val body: String = "",
    val metadata: String = "",
    val inputMessages: Channel<InputMessage>? = null,
)