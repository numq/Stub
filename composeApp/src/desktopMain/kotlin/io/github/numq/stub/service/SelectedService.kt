package io.github.numq.stub.service

import io.github.numq.stub.proto.ProtoFile

data class SelectedService(val service: Service, val protoFile: ProtoFile)