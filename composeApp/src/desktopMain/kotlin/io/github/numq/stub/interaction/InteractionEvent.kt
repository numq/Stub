package io.github.numq.stub.interaction

import io.github.numq.stub.client.ClientEvent
import io.github.numq.stub.event.Event
import kotlinx.coroutines.flow.Flow
import java.util.*

sealed class InteractionEvent private constructor() : Event<UUID> {
    override val key: UUID = UUID.randomUUID()

    data class CollectEvents(val events: Flow<ClientEvent>) : InteractionEvent()

    data object DisposeEvents : InteractionEvent()
}