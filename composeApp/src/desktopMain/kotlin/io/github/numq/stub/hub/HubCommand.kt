package io.github.numq.stub.hub

import io.github.numq.stub.proto.ProtoFile
import io.github.numq.stub.service.Service

sealed interface HubCommand {
    data object GetFiles : HubCommand

    data class UpdateFiles(val protoFiles: List<ProtoFile>) : HubCommand

    data class UploadFile(val path: String) : HubCommand

    data class DeleteFile(val protoFile: ProtoFile) : HubCommand

    data class SelectService(val protoFile: ProtoFile, val service: Service) : HubCommand

    data object DeselectService : HubCommand

    data object OpenDrawer : HubCommand

    data object CloseDrawer : HubCommand
}