package io.github.numq.stub.hub.feature

import io.github.numq.stub.hub.SelectedService
import io.github.numq.stub.proto.ProtoFile

data class HubState(val protoFiles: List<ProtoFile>, val selectedService: SelectedService?)