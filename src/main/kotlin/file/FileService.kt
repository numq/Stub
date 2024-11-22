package file

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.IOException

interface FileService {
    suspend fun createDescriptor(path: String): Result<Pair<File, Descriptors.FileDescriptor>>

    class Default : FileService {
        override suspend fun createDescriptor(path: String) = runCatching {
            val file = File(path).takeIf(File::exists)

            checkNotNull(file) { "File not found" }

            val descriptor = withContext(Dispatchers.IO) {
                File.createTempFile("out", ".pb")
            }.apply {
                deleteOnExit()
            }

            val processBuilder = ProcessBuilder(
                "protoc", "--proto_path=${file.parent}", "--descriptor_set_out=${descriptor.path}", file.name
            )

            processBuilder.redirectErrorStream(true)

            val process = processBuilder.start()

            val output = process.inputStream.bufferedReader().use(BufferedReader::readText)
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                throw IOException("Protoc failed with exit code $exitCode. Output: $output")
            }

            val fileDescriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(descriptor.inputStream())

            val fileDescriptorMap = mutableMapOf<String, Descriptors.FileDescriptor>()

            for (fileDescriptorProto in fileDescriptorSet.fileList) {
                val dependencies = fileDescriptorProto.dependencyList.mapNotNull { dependencyName ->
                    fileDescriptorMap[dependencyName]
                }.toTypedArray()

                val fileDescriptor = Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, dependencies)

                fileDescriptorMap[fileDescriptorProto.name] = fileDescriptor
            }

            val fileDescriptor = fileDescriptorMap[file.name]

            checkNotNull(fileDescriptor) { "Unable to create file descriptor" }

            file to fileDescriptor
        }
    }
}