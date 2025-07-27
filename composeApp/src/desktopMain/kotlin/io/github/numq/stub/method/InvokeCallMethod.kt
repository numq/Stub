package io.github.numq.stub.method

import io.github.numq.stub.client.ClientEvent
import io.github.numq.stub.client.ClientRepository
import io.github.numq.stub.client.InputMessage
import io.github.numq.stub.interactor.Interactor
import kotlinx.coroutines.flow.Flow

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