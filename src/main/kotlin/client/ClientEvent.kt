package client

import method.Method
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

sealed interface ClientEvent {
    val method: Method
    val receivedAt: Duration

    data class Error(override val method: Method, val throwable: Throwable) : ClientEvent {
        override val receivedAt = System.currentTimeMillis().milliseconds
    }

    data class Started(override val method: Method) : ClientEvent {
        override val receivedAt = System.currentTimeMillis().milliseconds
    }

    data class Request(override val method: Method, val inputMessage: InputMessage) : ClientEvent {
        override val receivedAt = System.currentTimeMillis().milliseconds
    }

    data class Response(override val method: Method, val outputMessage: OutputMessage) : ClientEvent {
        override val receivedAt = System.currentTimeMillis().milliseconds
    }

    data class Completed(override val method: Method) : ClientEvent {
        override val receivedAt = System.currentTimeMillis().milliseconds
    }

    data class Cancelled(override val method: Method) : ClientEvent {
        override val receivedAt = System.currentTimeMillis().milliseconds
    }
}