package io.github.numq.stub.file

import io.github.numq.stub.interactor.Interactor
import io.github.numq.stub.proto.ProtoFile

class DeleteFile(private val fileRepository: FileRepository) : Interactor<DeleteFile.Input, Unit> {
    data class Input(val file: ProtoFile)

    override suspend fun execute(input: Input) = fileRepository.deleteFile(protoFile = input.file)
}