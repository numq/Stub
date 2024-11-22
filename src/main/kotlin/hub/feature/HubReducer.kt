package hub.feature

import feature.Reducer
import file.DeleteFile
import file.GetFiles
import file.UploadFile
import hub.SelectedService
import proto.ProtoFile

class HubReducer(
    private val getFiles: GetFiles,
    private val uploadFile: UploadFile,
    private val deleteFile: DeleteFile,
) : Reducer<HubCommand, HubState, HubEvent> {
    override suspend fun reduce(state: HubState, command: HubCommand) = when (command) {
        is HubCommand.GetFiles -> getFiles.execute(Unit).fold(onSuccess = { protoFiles ->
            transition(state, HubEvent.CollectFiles(protoFiles = protoFiles))
        }, onFailure = { t ->
            transition(state, HubEvent.Error(Exception(t.localizedMessage)))
        })

        is HubCommand.UpdateFiles -> transition(state.copy(protoFiles = command.protoFiles))

        is HubCommand.UploadFile -> uploadFile.execute(UploadFile.Input(path = command.path)).fold(onSuccess = {
            transition(state)
        }, onFailure = { t ->
            transition(state, HubEvent.Error(Exception(t.localizedMessage)))
        })

        is HubCommand.DeleteFile -> deleteFile.execute(DeleteFile.Input(file = command.protoFile)).fold(onSuccess = {
            state.selectedService?.let { selectedService ->
                (command.protoFile as? ProtoFile.Loaded)?.services?.takeIf { services ->
                    services.contains(selectedService.service)
                }?.let {
                    transition(state.copy(selectedService = null))
                }
            } ?: transition(state.copy(selectedService = null))
        }, onFailure = { t ->
            transition(state, HubEvent.Error(Exception(t.localizedMessage)))
        })

        is HubCommand.SelectService -> transition(
            state.copy(
                selectedService = SelectedService(
                    protoFile = command.protoFile, service = command.service
                )
            )
        )

        is HubCommand.DeselectService -> transition(state.copy(selectedService = null))
    }
}