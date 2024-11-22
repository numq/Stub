package hub.feature

import proto.ProtoFile
import service.Service

sealed interface HubCommand {
    data object GetFiles : HubCommand

    data class UpdateFiles(val protoFiles: List<ProtoFile>) : HubCommand

    data class UploadFile(val path: String) : HubCommand

    data class DeleteFile(val protoFile: ProtoFile) : HubCommand

    data class SelectService(val protoFile: ProtoFile, val service: Service) : HubCommand

    data object DeselectService : HubCommand
}