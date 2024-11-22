package method.interactor

import client.ClientEvent
import client.ClientRepository
import client.InputMessage
import interactor.Interactor
import kotlinx.coroutines.flow.Flow
import method.Method

class InvokeStreamMethod(
    private val clientRepository: ClientRepository,
) : Interactor<InvokeStreamMethod.Input, Flow<ClientEvent>> {
    data class Input(
        val address: String,
        val method: Method.Stream,
        val inputMessages: Flow<InputMessage>,
        val metadata: String,
    )

    override suspend fun execute(input: Input) = with(input) {
        clientRepository.invokeStreamMethod(
            address = address,
            method = method,
            inputMessages = inputMessages,
            metadata = metadata
        )
    }
}