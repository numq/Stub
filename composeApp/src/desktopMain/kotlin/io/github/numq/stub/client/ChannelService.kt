package io.github.numq.stub.client

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

interface ChannelService : AutoCloseable {
    suspend fun useChannel(address: String): Result<ManagedChannel>

    class Default : ChannelService {
        private val mutex = Mutex()

        private var channel: Pair<String, ManagedChannel>? = null

        override suspend fun useChannel(address: String) = mutex.withLock {
            runCatching {
                if (channel?.first != address) {
                    ManagedChannelBuilder.forTarget(address).usePlaintext().build().also { channel = address to it }
                }

                checkNotNull(channel?.second) { "Unable to create channel" }
            }
        }

        override fun close() {
            try {
                channel?.second?.shutdown()?.awaitTermination(5, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                channel?.second?.shutdownNow()
            }
        }
    }
}