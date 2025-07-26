package io.github.numq.stub.file

import com.google.protobuf.Descriptors
import io.github.numq.stub.descriptor.DescriptorResult
import io.github.numq.stub.descriptor.DescriptorService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import io.github.numq.stub.method.Method
import io.github.numq.stub.proto.ProtoFile
import io.github.numq.stub.service.Service
import java.io.File
import java.util.*

interface FileRepository {
    val protoFiles: StateFlow<List<ProtoFile>>
    suspend fun uploadFile(path: String): Result<Unit>
    suspend fun deleteFile(protoFile: ProtoFile): Result<Unit>

    class Default(private val descriptorService: DescriptorService) : FileRepository {
        override val protoFiles = MutableStateFlow<List<ProtoFile>>(emptyList())

        private suspend fun generateDescriptor(file: File) =
            descriptorService.createDescriptor(file = file).mapCatching { descriptorResult ->
                val id = UUID.randomUUID().toString()

                when (descriptorResult) {
                    is DescriptorResult.MissingDependencies -> ProtoFile.Uploaded(
                        id = id,
                        path = file.path,
                        name = file.name,
                        dependencies = descriptorResult.dependencies,
                        missingDependencies = descriptorResult.missingDependencies,
                        content = file.readText()
                    )

                    is DescriptorResult.Success -> ProtoFile.Loaded(
                        id = id,
                        path = file.path,
                        name = file.name,
                        dependencies = descriptorResult.descriptor.dependencies.map(Descriptors.FileDescriptor::getName),
                        services = descriptorResult.descriptor.services.map { serviceDescriptor ->
                            Service(
                                name = serviceDescriptor.name,
                                fullName = serviceDescriptor.fullName,
                                methods = serviceDescriptor.methods.map { methodDescriptor ->
                                    when {
                                        methodDescriptor.isClientStreaming && methodDescriptor.isServerStreaming -> Method.Stream.Bidi(
                                            name = methodDescriptor.name,
                                            serviceName = serviceDescriptor.name,
                                            fileName = file.name,
                                            fullName = methodDescriptor.fullName,
                                        )

                                        methodDescriptor.isClientStreaming -> Method.Stream.Client(
                                            name = methodDescriptor.name,
                                            serviceName = serviceDescriptor.name,
                                            fileName = file.name,
                                            fullName = methodDescriptor.fullName,
                                        )

                                        methodDescriptor.isServerStreaming -> Method.Call.Server(
                                            name = methodDescriptor.name,
                                            serviceName = serviceDescriptor.name,
                                            fileName = file.name,
                                            fullName = methodDescriptor.fullName,
                                        )

                                        else -> Method.Call.Unary(
                                            name = methodDescriptor.name,
                                            serviceName = serviceDescriptor.name,
                                            fileName = file.name,
                                            fullName = methodDescriptor.fullName,
                                        )
                                    }
                                },
                                fileContent = file.readText()
                            )
                        },
                        content = file.readText()
                    )
                }
            }

        override suspend fun uploadFile(path: String) = runCatching {
            if (protoFiles.value.any { protoFile -> protoFile.path == path }) return@runCatching

            val file = File(path).takeIf(File::exists)

            checkNotNull(file) { "File $path not found" }

            val protoFile = generateDescriptor(file = file).getOrThrow()

            protoFiles.value += protoFile

            protoFiles.value = protoFiles.value.map { existingFile ->
                (existingFile as? ProtoFile.Uploaded)?.missingDependencies?.contains(protoFile.name)?.let {
                    File(existingFile.path).takeIf(File::exists)?.let { unresolvedFile ->
                        generateDescriptor(file = unresolvedFile).getOrThrow()
                    }
                } ?: existingFile
            }
        }

        override suspend fun deleteFile(protoFile: ProtoFile) = runCatching {
            if (protoFile !in protoFiles.value) return@runCatching

            if (protoFile is ProtoFile.Loaded) {
                descriptorService.deleteDescriptor(protoFile.name).getOrThrow()
            }

            protoFiles.value -= protoFile

            protoFiles.value = protoFiles.value.map { existingFile ->
                File(existingFile.path).takeIf(File::exists)?.let { file ->
                    generateDescriptor(file = file).getOrThrow()
                } ?: existingFile
            }
        }
    }
}