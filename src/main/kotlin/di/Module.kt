package di

import client.ChannelService
import client.ClientRepository
import client.ClientService
import descriptor.DescriptorService
import file.*
import generation.GenerateRandomRequest
import generation.GenerationRepository
import generation.GenerationService
import hub.feature.HubFeature
import hub.feature.HubReducer
import method.interactor.InvokeCallMethod
import method.interactor.InvokeStreamMethod
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose
import service.*
import service.feature.ServiceCommunicationReducer
import service.feature.ServiceFeature
import service.feature.ServiceInteractionReducer
import service.feature.ServiceReducer

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