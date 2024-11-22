package service.feature

import client.ClientEvent
import client.InputMessage
import kotlinx.coroutines.channels.Channel
import method.Method
import service.Service

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