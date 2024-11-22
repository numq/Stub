package client

import com.google.gson.JsonParser
import com.google.protobuf.Descriptors
import com.google.protobuf.DynamicMessage
import com.google.protobuf.util.JsonFormat
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.MethodDescriptor.MethodType
import io.grpc.kotlin.ClientCalls
import io.grpc.protobuf.ProtoUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.milliseconds

interface ClientService {
    suspend fun unary(
        channel: ManagedChannel,
        descriptor: Descriptors.MethodDescriptor,
        inputMessage: InputMessage,
        metadata: String,
    ): Result<OutputMessage>

    suspend fun clientStreaming(
        channel: ManagedChannel,
        descriptor: Descriptors.MethodDescriptor,
        inputMessages: Flow<InputMessage>,
        metadata: String,
    ): Result<OutputMessage>

    suspend fun serverStreaming(
        channel: ManagedChannel,
        descriptor: Descriptors.MethodDescriptor,
        inputMessage: InputMessage,
        metadata: String,
    ): Result<Flow<OutputMessage>>

    suspend fun bidiStreaming(
        channel: ManagedChannel,
        descriptor: Descriptors.MethodDescriptor,
        inputMessages: Flow<InputMessage>,
        metadata: String,
    ): Result<Flow<OutputMessage>>

    class Default : ClientService {
        private fun buildMethodDescriptor(
            descriptor: Descriptors.MethodDescriptor,
            methodType: MethodType,
        ) = MethodDescriptor.newBuilder<DynamicMessage, DynamicMessage>().setType(methodType)
            .setFullMethodName(MethodDescriptor.generateFullMethodName(descriptor.service.fullName, descriptor.name))
            .setRequestMarshaller(ProtoUtils.marshaller(DynamicMessage.getDefaultInstance(descriptor.inputType)))
            .setResponseMarshaller(ProtoUtils.marshaller(DynamicMessage.getDefaultInstance(descriptor.outputType)))
            .build()

        private fun buildRequestMessage(
            method: Descriptors.MethodDescriptor,
            json: String,
        ) = runCatching {
            DynamicMessage.newBuilder(method.inputType).also { builder ->
                JsonFormat.parser().ignoringUnknownFields().merge(json, builder)
            }.build()
        }.recoverCatching { throw Exception("Invalid method body") }

        private fun buildMetadata(json: String): Metadata {
            val metadata = Metadata()
            val jsonObject = JsonParser.parseString(json).asJsonObject

            for (entry in jsonObject.entrySet()) {
                val key = entry.key
                val value = entry.value.asString

                val metadataKey = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)

                metadata.put(metadataKey, value)
            }

            return metadata
        }

        private fun buildResponse(message: DynamicMessage) = OutputMessage(
            body = JsonFormat.printer().print(message),
            timestamp = System.currentTimeMillis().milliseconds
        )

        override suspend fun unary(
            channel: ManagedChannel,
            descriptor: Descriptors.MethodDescriptor,
            inputMessage: InputMessage,
            metadata: String,
        ) = runCatching {
            ClientCalls.unaryRpc(
                channel = channel,
                method = buildMethodDescriptor(descriptor, MethodType.UNARY),
                request = buildRequestMessage(descriptor, inputMessage.body).getOrThrow(),
                headers = buildMetadata(metadata)
            ).let(::buildResponse)
        }

        override suspend fun clientStreaming(
            channel: ManagedChannel,
            descriptor: Descriptors.MethodDescriptor,
            inputMessages: Flow<InputMessage>,
            metadata: String,
        ) = runCatching {
            ClientCalls.clientStreamingRpc(
                channel = channel,
                method = buildMethodDescriptor(descriptor, MethodType.CLIENT_STREAMING),
                requests = inputMessages.map { inputMessage ->
                    buildRequestMessage(
                        descriptor,
                        inputMessage.body
                    ).getOrThrow()
                },
                headers = buildMetadata(metadata)
            ).let(::buildResponse)
        }

        override suspend fun serverStreaming(
            channel: ManagedChannel,
            descriptor: Descriptors.MethodDescriptor,
            inputMessage: InputMessage,
            metadata: String,
        ) = runCatching {
            ClientCalls.serverStreamingRpc(
                channel = channel,
                method = buildMethodDescriptor(descriptor, MethodType.SERVER_STREAMING),
                request = buildRequestMessage(descriptor, inputMessage.body).getOrThrow(),
                headers = buildMetadata(metadata)
            ).map(::buildResponse)
        }

        override suspend fun bidiStreaming(
            channel: ManagedChannel,
            descriptor: Descriptors.MethodDescriptor,
            inputMessages: Flow<InputMessage>,
            metadata: String,
        ) = runCatching {
            ClientCalls.bidiStreamingRpc(
                channel = channel,
                method = buildMethodDescriptor(descriptor, MethodType.BIDI_STREAMING),
                requests = inputMessages.map { inputMessage ->
                    buildRequestMessage(
                        descriptor,
                        inputMessage.body
                    ).getOrThrow()
                },
                headers = buildMetadata(metadata)
            ).map(::buildResponse)
        }
    }
}