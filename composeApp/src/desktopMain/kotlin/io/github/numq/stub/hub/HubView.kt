package io.github.numq.stub.hub

import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CodeOff
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.unit.dp
import io.github.numq.stub.interaction.InteractionView
import io.github.numq.stub.proto.ProtoFile
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import java.net.URI
import java.nio.file.LinkOption
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.toPath

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HubView(feature: HubFeature = koinInject(), openFileDialog: () -> List<String>) {
    val state by feature.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val uploadedFilesDrawerState = rememberDrawerState(DrawerValue.Closed)

    val previewDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    fun uploadFiles(paths: List<String>) {
        if (paths.isNotEmpty()) {
            coroutineScope.launch {
                paths.forEach { path ->
                    feature.execute(command = HubCommand.UploadFile(path = path))
                }
            }
        }
    }

    val dropTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                when (val data = event.dragData()) {
                    is DragData.FilesList -> data.readFiles().map {
                        URI(it).toPath()
                    }.filter { path ->
                        path.exists(LinkOption.NOFOLLOW_LINKS)
                    }.map(Path::toAbsolutePath).map(Path::toString)

                    else -> emptyList()
                }.let(::uploadFiles)

                return true
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

    Scaffold(modifier = Modifier.fillMaxSize().dragAndDropTarget(shouldStartDragAndDrop = { event ->
        event.dragData() !is DragData.Image
    }, dropTarget), topBar = {
        TopAppBar(title = {
            state.selectedService?.let { service ->
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
        ModalDrawer(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
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
                state.selectedService?.let { service ->
                    InteractionView(
                        feature = koinInject(parameters = { parametersOf(service) }),
                        previewDrawerState = previewDrawerState
                    )
                }
            })
    }
}