package io.github.numq.stub.file

import io.github.numq.stub.interactor.Interactor
import kotlinx.coroutines.flow.StateFlow
import io.github.numq.stub.proto.ProtoFile

class GetFiles(private val fileRepository: FileRepository) : Interactor<Unit, StateFlow<List<ProtoFile>>> {
    override suspend fun execute(input: Unit) = Result.success(fileRepository.protoFiles)
}