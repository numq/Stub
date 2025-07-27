package io.github.numq.stub.interaction

import io.github.numq.stub.client.ClientEvent
import io.github.numq.stub.method.Method

sealed interface InteractionCommand {
    data object ExpandMethodsMenu : InteractionCommand

    data object ShrinkMethodsMenu : InteractionCommand

    data class SelectMethod(val method: Method) : InteractionCommand

    data object DeselectMethod : InteractionCommand

    data class ChangeAddress(val address: String) : InteractionCommand

    data class ChangeBody(val body: String) : InteractionCommand

    data class ChangeMetadata(val metadata: String) : InteractionCommand

    data class GenerateRandomBody(val method: Method) : InteractionCommand

    sealed interface Communication : InteractionCommand {
        data class AddEvent(val event: ClientEvent) : Communication

        data object InvokeMethod : Communication

        data object SendRequest : Communication

        data object StopStreaming : Communication

        data object CancelMethod : Communication

        data object CompleteMethod : Communication
    }
}