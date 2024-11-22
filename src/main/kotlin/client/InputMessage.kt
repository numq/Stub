package client

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class InputMessage(
    val body: String,
    val timestamp: Duration = System.currentTimeMillis().milliseconds,
)