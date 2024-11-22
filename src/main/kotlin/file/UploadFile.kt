package file

import interactor.Interactor

class UploadFile(private val fileRepository: FileRepository) : Interactor<UploadFile.Input, Unit> {
    data class Input(val path: String)

    override suspend fun execute(input: Input) = with(input) {
        fileRepository.uploadFile(path = path)
    }
}