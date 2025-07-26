package io.github.numq.stub.generation

import com.google.protobuf.Descriptors
import com.google.protobuf.DynamicMessage
import com.google.protobuf.util.JsonFormat
import kotlin.random.Random

interface GenerationService {
    suspend fun generateRandomJson(descriptor: Descriptors.MethodDescriptor): Result<String>

    class Default : GenerationService {
        private fun generateRandomMessageForDescriptor(descriptor: Descriptors.Descriptor): DynamicMessage {
            val messageBuilder = DynamicMessage.newBuilder(descriptor)
            for (field in descriptor.fields) {
                val randomValue = generateRandomValueForField(field)
                if (randomValue != null) {
                    messageBuilder.setField(field, randomValue)
                }
            }
            return messageBuilder.build()
        }

        private fun generateRandomValueForField(field: Descriptors.FieldDescriptor): Any? {
            return when (field.type) {
                Descriptors.FieldDescriptor.Type.BOOL -> Random.nextBoolean()
                Descriptors.FieldDescriptor.Type.INT32, Descriptors.FieldDescriptor.Type.SINT32, Descriptors.FieldDescriptor.Type.SFIXED32 -> Random.nextInt(
                    1,
                    100
                )

                Descriptors.FieldDescriptor.Type.INT64, Descriptors.FieldDescriptor.Type.SINT64, Descriptors.FieldDescriptor.Type.SFIXED64 -> Random.nextLong(
                    1,
                    1000
                )

                Descriptors.FieldDescriptor.Type.FLOAT -> Random.nextFloat()
                Descriptors.FieldDescriptor.Type.DOUBLE -> Random.nextDouble()
                Descriptors.FieldDescriptor.Type.STRING -> "random_string_${Random.nextInt(1000)}"
                Descriptors.FieldDescriptor.Type.MESSAGE -> generateRandomMessageForDescriptor(field.messageType)
                Descriptors.FieldDescriptor.Type.ENUM -> field.enumType.values[Random.nextInt(field.enumType.values.size)]
                else -> null
            }
        }

        override suspend fun generateRandomJson(descriptor: Descriptors.MethodDescriptor) = runCatching {
            JsonFormat.printer().print(generateRandomMessageForDescriptor(descriptor.inputType))
        }
    }
}