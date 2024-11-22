package hub.feature

import kotlinx.coroutines.flow.StateFlow
import proto.ProtoFile

sealed interface HubEvent {
    data class Error(val exception: Exception) : HubEvent

    data class CollectFiles(val protoFiles: StateFlow<List<ProtoFile>>) : HubEvent
}