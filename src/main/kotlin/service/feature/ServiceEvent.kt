package service.feature

import client.ClientEvent
import kotlinx.coroutines.flow.Flow

sealed interface ServiceEvent {
    data class CollectEvents(val events: Flow<ClientEvent>) : ServiceEvent

    data object DisposeEvents : ServiceEvent
}