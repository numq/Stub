package client

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class OutputMessage(
    val body: String,
    val timestamp: Duration = System.currentTimeMillis().milliseconds,
)