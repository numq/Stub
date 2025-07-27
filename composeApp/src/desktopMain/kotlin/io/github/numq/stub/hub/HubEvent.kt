package io.github.numq.stub.hub

import io.github.numq.stub.event.Event
import io.github.numq.stub.proto.ProtoFile
import kotlinx.coroutines.flow.StateFlow
import java.util.*

sealed class HubEvent private constructor() : Event<UUID> {
    override val key: UUID = UUID.randomUUID()

    data class Error(val exception: Exception) : HubEvent()

    data class CollectFiles(val protoFiles: StateFlow<List<ProtoFile>>) : HubEvent()
}