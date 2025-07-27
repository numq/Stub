package io.github.numq.stub.interaction

import io.github.numq.stub.feature.Feature
import io.github.numq.stub.service.Service
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import org.koin.ext.getFullName

class InteractionFeature(
    service: Service,
    reducer: InteractionReducer,
) : Feature<InteractionCommand, InteractionState, InteractionEvent>(
    initialState = InteractionState(service = service),
    coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
    reducer = reducer
) {
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    private val jobs = mutableMapOf<String, Job>()

    init {
        coroutineScope.launch {
            events.collect { event ->
                val key = event::class.getFullName()

                jobs[key]?.cancel()

                when (event) {
                    is InteractionEvent.CollectEvents -> event.events.onEach { clientEvent ->
                        execute(InteractionCommand.Communication.AddEvent(event = clientEvent))
                    }.onCompletion {
                        execute(InteractionCommand.Communication.CompleteMethod)
                    }.launchIn(this)

                    else -> null
                }?.let { job ->
                    jobs[key] = job
                }
            }
        }

        invokeOnClose {
            coroutineScope.cancel()
        }
    }
}