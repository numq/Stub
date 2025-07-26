package io.github.numq.stub.hub.feature

import kotlinx.coroutines.flow.StateFlow
import io.github.numq.stub.proto.ProtoFile

sealed interface HubEvent {
    data class Error(val exception: Exception) : HubEvent

    data class CollectFiles(val protoFiles: StateFlow<List<ProtoFile>>) : HubEvent
}