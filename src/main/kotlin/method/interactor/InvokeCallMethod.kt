package method.interactor

import client.ClientEvent
import client.ClientRepository
import client.InputMessage
import interactor.Interactor
import kotlinx.coroutines.flow.Flow
import method.Method

class InvokeCallMethod(
    private val clientRepository: ClientRepository,
) : Interactor<InvokeCallMethod.Input, Flow<ClientEvent>> {
    data class Input(val address: String, val method: Method.Call, val inputMessage: InputMessage, val metadata: String)

    override suspend fun execute(input: Input) = with(input) {
        clientRepository.invokeCallMethod(
            address = address,
            method = method,
            inputMessage = inputMessage,
            metadata = metadata
        )
    }
}