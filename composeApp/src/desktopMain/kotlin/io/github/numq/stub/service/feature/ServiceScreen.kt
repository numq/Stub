package io.github.numq.stub.service.feature

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import io.github.numq.stub.method.Method
import io.github.numq.stub.method.MethodItem
import io.github.numq.stub.proto.ProtoFilePreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ServiceScreen(
    fileContent: String,
    previewDrawerState: BottomDrawerState,
    feature: ServiceFeature,
) {
    val coroutineScope = rememberCoroutineScope()

    val state by feature.state.collectAsState()

    BottomDrawer(
        modifier = Modifier.fillMaxSize(),
        drawerContent = { ProtoFilePreview(content = fileContent) },
        drawerState = previewDrawerState,
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(value = state.address, onValueChange = { value ->
                        coroutineScope.launch {
                            feature.execute(ServiceCommand.Interaction.ChangeAddress(address = value.trim()))
                        }
                    }, modifier = Modifier.weight(1f), singleLine = true, trailingIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                feature.execute(ServiceCommand.Interaction.ChangeAddress(address = ""))
                            }
                        }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    })
                    ExposedDropdownMenuBox(
                        expanded = state.methodsMenuExpanded,
                        onExpandedChange = {
                            coroutineScope.launch {
                                if (state.methodsMenuExpanded) {
                                    feature.execute(ServiceCommand.Interaction.ShrinkMethodsMenu)
                                } else {
                                    feature.execute(ServiceCommand.Interaction.ExpandMethodsMenu)
                                }
                            }
                        }
                    ) {
                        OutlinedTextField(
                            state.selectedMethod?.name ?: "Select method",
                            onValueChange = {},
                            modifier = Modifier.weight(1f),
                            readOnly = true,
                            leadingIcon = {
                                when (state.selectedMethod) {
                                    null -> Unit

                                    is Method.Call.Unary -> Icon(
                                        Icons.AutoMirrored.Filled.Message,
                                        contentDescription = null
                                    )

                                    is Method.Call.Server -> Icon(
                                        Icons.AutoMirrored.Filled.ArrowRightAlt,
                                        contentDescription = null,
                                        modifier = Modifier.scale(scaleX = -1f, scaleY = 1f)
                                    )

                                    is Method.Stream.Client -> Icon(
                                        Icons.AutoMirrored.Filled.ArrowRightAlt,
                                        contentDescription = null
                                    )

                                    is Method.Stream.Bidi -> Icon(
                                        Icons.Default.SyncAlt,
                                        contentDescription = null
                                    )
                                }
                            },
                            trailingIcon = {
                                if (state.methodsMenuExpanded) {
                                    Icon(Icons.Default.ExpandLess, null)
                                } else {
                                    Icon(Icons.Default.ExpandMore, null)
                                }
                            })
                        ExposedDropdownMenu(
                            expanded = state.methodsMenuExpanded,
                            onDismissRequest = {
                                coroutineScope.launch {
                                    feature.execute(ServiceCommand.Interaction.ShrinkMethodsMenu)
                                }
                            }
                        ) {
                            state.service.methods.forEach { method ->
                                DropdownMenuItem(onClick = {
                                    coroutineScope.launch {
                                        if (method == state.selectedMethod) {
                                            feature.execute(ServiceCommand.Interaction.DeselectMethod)
                                        } else {
                                            feature.execute(ServiceCommand.Interaction.SelectMethod(method = method))
                                        }
                                    }
                                }) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(
                                            space = 4.dp,
                                            alignment = Alignment.Start
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(
                                                space = 4.dp,
                                                alignment = Alignment.CenterHorizontally
                                            ), verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            when (method) {
                                                is Method.Call.Unary -> Icon(
                                                    Icons.AutoMirrored.Filled.Message,
                                                    contentDescription = null
                                                )

                                                is Method.Call.Server -> Icon(
                                                    Icons.AutoMirrored.Filled.ArrowRightAlt,
                                                    contentDescription = null,
                                                    modifier = Modifier.scale(scaleX = -1f, scaleY = 1f)
                                                )

                                                is Method.Stream.Client -> Icon(
                                                    Icons.AutoMirrored.Filled.ArrowRightAlt,
                                                    contentDescription = null
                                                )

                                                is Method.Stream.Bidi -> Icon(
                                                    Icons.Default.SyncAlt,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                        Text(
                                            method.name,
                                            modifier = Modifier.padding(4.dp)
                                                .alpha(alpha = if (method == state.selectedMethod) .5f else 1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            if (state.isConnected) {
                                feature.execute(ServiceCommand.Communication.CancelMethod)
                            } else {
                                feature.execute(ServiceCommand.Communication.InvokeMethod)
                            }
                        }
                    }) {
                        if (state.isConnected) {
                            Icon(Icons.Default.CloudOff, null)
                        } else {
                            Icon(Icons.Default.Cloud, null)
                        }
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            feature.execute(ServiceCommand.Communication.SendRequest)
                        }
                    }, enabled = state.selectedMethod is Method.Stream && state.isConnected) {
                        Icon(Icons.Default.CloudUpload, null)
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            feature.execute(ServiceCommand.Communication.StopStreaming)
                        }
                    }, enabled = state.selectedMethod is Method.Stream && state.isConnected) {
                        Icon(Icons.Default.CloudDone, null)
                    }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    state.selectedMethod?.let { method ->
                        MethodItem(
                            method = method,
                            events = state.events,
                            body = state.body,
                            onBodyChange = { body ->
                                coroutineScope.launch {
                                    feature.execute(ServiceCommand.Interaction.ChangeBody(body = body))
                                }
                            },
                            metadata = state.metadata,
                            onMetadataChange = { metadata ->
                                coroutineScope.launch {
                                    feature.execute(ServiceCommand.Interaction.ChangeMetadata(metadata = metadata))
                                }
                            },
                            generateRandomBody = {
                                coroutineScope.launch {
                                    feature.execute(ServiceCommand.Interaction.GenerateRandomBody(method = method))
                                }
                            }
                        )
                    } ?: Text("Upload proto and select method to start")
                }
            }
        }
    )
}