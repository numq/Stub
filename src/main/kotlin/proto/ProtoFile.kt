package proto

import service.Service

sealed interface ProtoFile {
    val id: String
    val path: String
    val name: String
    val content: String
    val dependencies: List<String>

    data class Uploaded(
        override val id: String,
        override val path: String,
        override val name: String,
        override val content: String,
        override val dependencies: List<String>,
        val missingDependencies: List<String>,
    ) : ProtoFile

    data class Loaded(
        override val id: String,
        override val path: String,
        override val name: String,
        override val content: String,
        override val dependencies: List<String>,
        val services: List<Service>,
    ) : ProtoFile
}