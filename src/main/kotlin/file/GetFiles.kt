package file

import interactor.Interactor
import kotlinx.coroutines.flow.StateFlow
import proto.ProtoFile

class GetFiles(private val fileRepository: FileRepository) : Interactor<Unit, StateFlow<List<ProtoFile>>> {
    override suspend fun execute(input: Unit) = Result.success(fileRepository.protoFiles)
}