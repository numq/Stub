package descriptor

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import method.Method
import java.io.BufferedReader
import java.io.File
import java.io.IOException

interface DescriptorService {
    suspend fun createDescriptor(file: File): Result<DescriptorResult>
    suspend fun deleteDescriptor(fileName: String): Result<Unit>
    suspend fun resolveDescriptor(method: Method): Result<Descriptors.MethodDescriptor>

    class Default : DescriptorService {
        private val mutex = Mutex()

        private val descriptors = mutableMapOf<String, Descriptors.FileDescriptor>()

        private suspend fun generateDescriptor(file: File): Result<DescriptorResult> = runCatching {
            val descriptor = withContext(Dispatchers.IO) {
                File.createTempFile("out", ".pb")
            }.apply {
                deleteOnExit()
            }

            val processBuilder = ProcessBuilder(
                "protoc",
                "--proto_path=${file.parentFile.absolutePath}",
                "--descriptor_set_out=${descriptor.absolutePath}",
                file.name
            )

            processBuilder.redirectErrorStream(true)

            val process = processBuilder.start()

            val output = process.inputStream.bufferedReader().use(BufferedReader::readText)

            val exitCode = process.waitFor()

            if (exitCode != 0) {
                throw IOException("Protoc failed with exit code $exitCode for file: ${file.path}. Output: $output")
            }

            val fileDescriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(descriptor.inputStream())

            for (fileDescriptorProto in fileDescriptorSet.fileList) {
                val dependencies = fileDescriptorProto.dependencyList

                val missingDependencies = fileDescriptorProto.dependencyList.filterNot { dependencyName ->
                    descriptors.containsKey(dependencyName)
                }

                if (missingDependencies.isNotEmpty()) {
                    return@runCatching DescriptorResult.MissingDependencies(
                        file = file,
                        dependencies = dependencies,
                        missingDependencies = missingDependencies
                    )
                } else {
                    val fileDescriptor = Descriptors.FileDescriptor.buildFrom(
                        fileDescriptorProto,
                        fileDescriptorProto.dependencyList.mapNotNull(descriptors::get).toTypedArray()
                    )

                    descriptors[fileDescriptor.name] = fileDescriptor
                }
            }

            DescriptorResult.Success(checkNotNull(descriptors[file.name]) { "Descriptor not found for ${file.name}" })
        }

        override suspend fun createDescriptor(file: File) = mutex.withLock {
            generateDescriptor(file = file)
        }

        override suspend fun deleteDescriptor(fileName: String) = mutex.withLock {
            descriptors.remove(fileName)

            Result.success(Unit)
        }

        override suspend fun resolveDescriptor(method: Method) = mutex.withLock {
            runCatching {
                val descriptor =
                    descriptors[method.fileName]?.findServiceByName(method.serviceName)?.findMethodByName(method.name)

                checkNotNull(descriptor) { "Descriptor not found for method: ${method.name}" }
            }
        }
    }
}
