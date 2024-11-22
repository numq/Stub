package generation

import descriptor.DescriptorService
import method.Method

interface GenerationRepository {
    suspend fun generateRandomRequest(method: Method): Result<String>

    class Default(
        private val descriptorService: DescriptorService,
        private val generationService: GenerationService,
    ) : GenerationRepository {
        override suspend fun generateRandomRequest(method: Method) =
            descriptorService.resolveDescriptor(method = method).mapCatching { descriptor ->
                generationService.generateRandomJson(descriptor = descriptor).getOrThrow()
            }
    }
}