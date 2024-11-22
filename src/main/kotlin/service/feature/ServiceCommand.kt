package service.feature

import client.ClientEvent
import method.Method

sealed interface ServiceCommand {
    sealed interface Interaction : ServiceCommand {
        data object ExpandMethodsMenu : Interaction

        data object ShrinkMethodsMenu : Interaction

        data class SelectMethod(val method: Method) : Interaction

        data object DeselectMethod : Interaction

        data class ChangeAddress(val address: String) : Interaction

        data class ChangeBody(val body: String) : Interaction

        data class ChangeMetadata(val metadata: String) : Interaction

        data class GenerateRandomBody(val method: Method) : Interaction
    }

    sealed interface Communication : ServiceCommand {
        data class AddEvent(val event: ClientEvent) : Communication

        data object InvokeMethod : Communication

        data object SendRequest : Communication

        data object StopStreaming : Communication

        data object CancelMethod : Communication

        data object CompleteMethod : Communication
    }
}