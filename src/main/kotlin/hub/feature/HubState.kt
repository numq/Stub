package hub.feature

import hub.SelectedService
import proto.ProtoFile

data class HubState(val protoFiles: List<ProtoFile>, val selectedService: SelectedService?)