package io.github.numq.stub.service.feature

import io.github.numq.stub.client.ClientEvent
import kotlinx.coroutines.flow.Flow

sealed interface ServiceEvent {
    data class CollectEvents(val events: Flow<ClientEvent>) : ServiceEvent

    data object DisposeEvents : ServiceEvent
}