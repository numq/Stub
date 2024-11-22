package generation

import method.Method
import interactor.Interactor

class GenerateRandomRequest(
    private val generationRepository: GenerationRepository,
) : Interactor<GenerateRandomRequest.Input, String> {
    data class Input(val method: Method)

    override suspend fun execute(input: Input) = with(input) {
        generationRepository.generateRandomRequest(method = method)
    }
}