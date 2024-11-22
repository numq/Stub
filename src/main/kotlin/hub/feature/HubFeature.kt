package hub.feature

import feature.Feature
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class HubFeature(reducer: HubReducer) : Feature<HubCommand, HubState, HubEvent>(
    initialState = HubState(protoFiles = emptyList(), selectedService = null),
    reducer = reducer
) {
    init {
        events.onStart {
            execute(HubCommand.GetFiles)
        }.onEach { event ->
            when (event) {
                is HubEvent.Error -> println(event.exception.localizedMessage)

                is HubEvent.CollectFiles -> event.protoFiles.onEach { protoFiles ->
                    execute(HubCommand.UpdateFiles(protoFiles = protoFiles))
                }.launchIn(featureScope)
            }
        }.launchIn(featureScope)
    }
}