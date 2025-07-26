package io.github.numq.stub.service.feature

import io.github.numq.stub.client.ClientEvent
import io.github.numq.stub.client.InputMessage
import kotlinx.coroutines.channels.Channel
import io.github.numq.stub.method.Method
import io.github.numq.stub.service.Service

data class ServiceState(
    val service: Service,
    val methodsMenuExpanded: Boolean,
    val selectedMethod: Method?,
    val isConnected: Boolean,
    val events: List<ClientEvent>,
    val address: String,
    val body: String,
    val metadata: String,
    val inputMessages: Channel<InputMessage>? = null,
)