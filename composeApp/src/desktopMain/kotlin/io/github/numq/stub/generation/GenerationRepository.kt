package io.github.numq.stub.generation

import io.github.numq.stub.descriptor.DescriptorService
import io.github.numq.stub.method.Method

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