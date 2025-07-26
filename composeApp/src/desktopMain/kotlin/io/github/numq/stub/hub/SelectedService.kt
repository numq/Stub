package hub

import io.github.numq.stub.proto.ProtoFile
import io.github.numq.stub.service.Service

data class SelectedService(val protoFile: ProtoFile, val service: Service)