package io.github.numq.stub.descriptor

import com.google.protobuf.Descriptors
import java.io.File

sealed interface DescriptorResult {
    data class MissingDependencies(
        val file: File,
        val dependencies: List<String>,
        val missingDependencies: List<String>,
    ) : DescriptorResult

    data class Success(val descriptor: Descriptors.FileDescriptor) : DescriptorResult
}