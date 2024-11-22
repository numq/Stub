package file

import interactor.Interactor
import proto.ProtoFile

class DeleteFile(private val fileRepository: FileRepository) : Interactor<DeleteFile.Input, Unit> {
    data class Input(val file: ProtoFile)

    override suspend fun execute(input: Input) = with(input) {
        fileRepository.deleteFile(protoFile = file)
    }
}