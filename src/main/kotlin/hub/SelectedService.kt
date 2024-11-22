package hub

import proto.ProtoFile
import service.Service

data class SelectedService(val protoFile: ProtoFile, val service: Service)