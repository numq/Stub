package io.github.numq.stub.di

import io.github.numq.stub.client.ChannelService
import io.github.numq.stub.client.ClientRepository
import io.github.numq.stub.client.ClientService
import io.github.numq.stub.descriptor.DescriptorService
import io.github.numq.stub.file.*
import io.github.numq.stub.generation.GenerateRandomRequest
import io.github.numq.stub.generation.GenerationRepository
import io.github.numq.stub.generation.GenerationService
import io.github.numq.stub.hub.HubFeature
import io.github.numq.stub.hub.HubReducer
import io.github.numq.stub.interaction.InteractionCommunicationReducer
import io.github.numq.stub.interaction.InteractionFeature
import io.github.numq.stub.interaction.InteractionReducer
import io.github.numq.stub.method.InvokeCallMethod
import io.github.numq.stub.method.InvokeStreamMethod
import io.github.numq.stub.navigation.NavigationFeature
import io.github.numq.stub.navigation.NavigationReducer
import io.github.numq.stub.service.Service
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

private val channel = module {
    single { ChannelService.Default() } bind ChannelService::class onClose { it?.close() }
}

private val descriptor = module {
    single { DescriptorService.Default() } bind DescriptorService::class
}

private val generation = module {
    single { GenerationService.Default() } bind GenerationService::class
    single { GenerationRepository.Default(get(), get()) } bind GenerationRepository::class
    single { GenerateRandomRequest(get()) }
}

private val client = module {
    single { ClientService.Default() } bind ClientService::class
    single { ClientRepository.Default(get(), get(), get()) } bind ClientRepository::class
}

private val file = module {
    single { FileService.Default() } bind FileService::class
    single { FileRepository.Default(get()) } bind FileRepository::class
    single { GetFiles(get()) }
    single { UploadFile(get()) }
    single { DeleteFile(get()) }
}

private val method = module {
    single { InvokeCallMethod(get()) }
    single { InvokeStreamMethod(get()) }
}

private val hub = module {
    single { HubReducer(get(), get(), get()) }
    factory { HubFeature(get()) } onClose { it?.close() }
}

private val interaction = module {
    single { InteractionCommunicationReducer(get(), get()) }
    single { InteractionReducer(get(), get()) }
    factory { (service: Service) -> InteractionFeature(service = service, reducer = get()) } onClose { it?.close() }
}

private val navigation = module {
    single { NavigationReducer() }
    factory { NavigationFeature(get()) } onClose { it?.close() }
}

internal val appModule = listOf(channel, descriptor, generation, client, file, method, hub, interaction, navigation)