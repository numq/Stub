package io.github.numq.stub.di

import io.github.numq.stub.client.ChannelService
import io.github.numq.stub.client.ClientRepository
import io.github.numq.stub.client.ClientService
import io.github.numq.stub.descriptor.DescriptorService
import io.github.numq.stub.generation.GenerateRandomRequest
import io.github.numq.stub.generation.GenerationRepository
import io.github.numq.stub.generation.GenerationService
import io.github.numq.stub.hub.feature.HubFeature
import io.github.numq.stub.hub.feature.HubReducer
import io.github.numq.stub.file.*
import io.github.numq.stub.method.interactor.InvokeCallMethod
import io.github.numq.stub.method.interactor.InvokeStreamMethod
import io.github.numq.stub.service.Service
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose
import io.github.numq.stub.service.feature.ServiceCommunicationReducer
import io.github.numq.stub.service.feature.ServiceFeature
import io.github.numq.stub.service.feature.ServiceInteractionReducer
import io.github.numq.stub.service.feature.ServiceReducer

private val channel = module {
    single { ChannelService.Default() } bind ChannelService::class onClose { it?.close() }
}

private val descriptor = module {
    single { DescriptorService.Default() } bind DescriptorService::class
}

private val generation = module {
    single { GenerationService.Default() } bind GenerationService::class
    single { GenerationRepository.Default(get(), get()) } bind GenerationRepository::class
    factory { GenerateRandomRequest(get()) }
}

private val client = module {
    single { ClientService.Default() } bind ClientService::class
    single { ClientRepository.Default(get(), get(), get()) } bind ClientRepository::class
}

private val file = module {
    single { FileService.Default() } bind FileService::class
    single { FileRepository.Default(get()) } bind FileRepository::class
    factory { GetFiles(get()) }
    factory { UploadFile(get()) }
    factory { DeleteFile(get()) }
}

private val hub = module {
    factory { HubReducer(get(), get(), get()) }
    single { HubFeature(get()) }
}

private val service = module {
    factory { ServiceCommunicationReducer(get(), get()) }
    factory { ServiceInteractionReducer(get(), get()) }
    factory { ServiceReducer(get(), get()) }
    single { (service: Service) -> ServiceFeature(service = service, reducer = get()) } onClose { it?.close() }
}

private val method = module {
    factory { InvokeCallMethod(get()) }
    factory { InvokeStreamMethod(get()) }
}

internal val appModule = channel + descriptor + generation + client + file + hub + service + method