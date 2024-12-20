package hub

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CodeOff
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.runtime.*
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.unit.dp
import hub.feature.HubCommand
import hub.feature.HubFeature
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import proto.ProtoFile
import service.feature.ServiceScreen
import java.net.URI
import java.nio.file.LinkOption
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.toPath

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun HubScreen(feature: HubFeature = koinInject(), openFileDialog: () -> List<String>) {
    val state by feature.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val uploadedFilesDrawerState = rememberDrawerState(DrawerValue.Closed)

    val previewDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    fun uploadFiles(paths: List<String>) {
        if (paths.isEmpty()) return

        coroutineScope.launch {
            paths.forEach { path ->
                feature.execute(command = HubCommand.UploadFile(path = path))
            }
        }
    }

    LaunchedEffect(state.protoFiles) {
        state.protoFiles.firstOrNull { it is ProtoFile.Loaded }?.let { protoFile ->
            (protoFile as? ProtoFile.Loaded)?.services?.firstOrNull()?.let { service ->
                feature.execute(HubCommand.SelectService(protoFile = protoFile, service = service))
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize().onExternalDrag { dragValue ->
        when (val data = dragValue.dragData) {
            is DragData.FilesList -> data.readFiles().map {
                URI(it).toPath()
            }.filter { path ->
                path.exists(LinkOption.NOFOLLOW_LINKS)
            }.map(Path::toAbsolutePath).map(Path::toString)

            else -> emptyList()
        }.let(::uploadFiles)
    }, topBar = {
        TopAppBar(title = {
            state.selectedService?.run {
                Text(service.fullName, modifier = Modifier.padding(8.dp))
            }
        }, navigationIcon = {
            IconButton(onClick = {
                coroutineScope.launch {
                    when {
                        uploadedFilesDrawerState.isOpen -> uploadedFilesDrawerState.close()

                        uploadedFilesDrawerState.isClosed -> uploadedFilesDrawerState.open()
                    }
                }
            }) {
                BadgedBox(badge = {
                    if (state.protoFiles.isNotEmpty()) {
                        Badge {
                            Text("${state.protoFiles.size}")
                        }
                    }
                }) {
                    Icon(Icons.Default.Reorder, null)
                }
            }
        }, actions = {
            if (state.selectedService != null) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        when {
                            previewDrawerState.isOpen || previewDrawerState.isExpanded -> previewDrawerState.close()

                            previewDrawerState.isClosed -> previewDrawerState.expand()
                        }
                    }
                }) {
                    when {
                        previewDrawerState.isOpen || previewDrawerState.isExpanded -> Icon(Icons.Default.CodeOff, null)

                        previewDrawerState.isClosed -> Icon(Icons.Default.Code, null)
                    }
                }
            }
        }, modifier = Modifier.fillMaxWidth())
    }) { paddingValues ->
        ModalDrawer(modifier = Modifier.fillMaxSize().padding(paddingValues),
            drawerState = uploadedFilesDrawerState,
            drawerContent = {
                UploadedFiles(files = state.protoFiles, openFileDialog = {
                    openFileDialog().let(::uploadFiles)
                }, deleteFile = { protoFile ->
                    coroutineScope.launch {
                        feature.execute(command = HubCommand.DeleteFile(protoFile = protoFile))
                    }
                }, selectedService = state.selectedService, selectService = { protoFile, service ->
                    coroutineScope.launch {
                        feature.execute(HubCommand.SelectService(protoFile = protoFile, service = service))
                    }
                }, deselectService = {
                    coroutineScope.launch {
                        feature.execute(HubCommand.DeselectService)
                    }
                })
            },
            content = {
                state.selectedService?.let { (protoFile, service) ->
                    ServiceScreen(
                        fileContent = protoFile.content,
                        previewDrawerState = previewDrawerState,
                        feature = koinInject(parameters = { parametersOf(service) })
                    )
                }
            }
        )
    }
}