package io.github.numq.stub.method

sealed interface Method {
    val name: String
    val serviceName: String
    val fileName: String
    val fullName: String

    sealed interface Call : Method {
        data class Server(
            override val name: String,
            override val serviceName: String,
            override val fileName: String,
            override val fullName: String,
        ) : Call

        data class Unary(
            override val name: String,
            override val serviceName: String,
            override val fileName: String,
            override val fullName: String,
        ) : Call
    }

    sealed interface Stream : Method {
        data class Client(
            override val name: String,
            override val serviceName: String,
            override val fileName: String,
            override val fullName: String,
        ) : Stream

        data class Bidi(
            override val name: String,
            override val serviceName: String,
            override val fileName: String,
            override val fullName: String,
        ) : Stream
    }
}