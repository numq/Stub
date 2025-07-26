package io.github.numq.stub.method.interactor

import io.github.numq.stub.client.ClientEvent
import io.github.numq.stub.client.ClientRepository
import io.github.numq.stub.client.InputMessage
import io.github.numq.stub.interactor.Interactor
import kotlinx.coroutines.flow.Flow
import io.github.numq.stub.method.Method

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