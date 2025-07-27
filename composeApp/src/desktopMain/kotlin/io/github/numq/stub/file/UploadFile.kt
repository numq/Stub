package io.github.numq.stub.file

import io.github.numq.stub.interactor.Interactor

class UploadFile(private val fileRepository: FileRepository) : Interactor<UploadFile.Input, Unit> {
    data class Input(val path: String)

    override suspend fun execute(input: Input) = fileRepository.uploadFile(path = input.path)
}