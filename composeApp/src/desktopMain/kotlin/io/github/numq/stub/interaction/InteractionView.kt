package io.github.numq.stub.interaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
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
fun InteractionView(
    feature: InteractionFeature,
    previewDrawerState: BottomDrawerState,
) {
    val coroutineScope = rememberCoroutineScope()

    val state by feature.state.collectAsState()

    val methodSelectionState = rememberTextFieldState()

    BottomDrawer(modifier = Modifier.fillMaxSize(), drawerContent = {
        ProtoFilePreview(content = state.service.fileContent)
    }, drawerState = previewDrawerState, content = {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp, alignment = Alignment.CenterHorizontally
                ), verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.address,
                    onValueChange = { value ->
                        coroutineScope.launch {
                            feature.execute(InteractionCommand.ChangeAddress(address = value.trim()))
                        }
                    },
                    placeholder = { Text("127.0.0.1:8000") },
                    modifier = Modifier.weight(1f).height(IntrinsicSize.Max),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                feature.execute(InteractionCommand.ChangeAddress(address = ""))
                            }
                        }, modifier = Modifier.padding(4.dp), enabled = state.address.isNotBlank()) {
                            Icon(Icons.Default.Clear, null)
                        }
                    })
                ExposedDropdownMenuBox(
                    expanded = state.methodsMenuExpanded, onExpandedChange = {
                        coroutineScope.launch {
                            if (state.methodsMenuExpanded) {
                                feature.execute(InteractionCommand.ShrinkMethodsMenu)
                            } else {
                                feature.execute(InteractionCommand.ExpandMethodsMenu)
                            }
                        }
                    }, modifier = Modifier.height(IntrinsicSize.Max)
                ) {
                    OutlinedTextField(
                        methodSelectionState,
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        placeholder = { Text("Select method") },
                        leadingIcon = {
                            when (state.selectedMethod) {
                                null -> Unit

                                is Method.Call.Unary -> Icon(
                                    Icons.AutoMirrored.Filled.Message, contentDescription = null
                                )

                                is Method.Call.Server -> Icon(
                                    Icons.AutoMirrored.Filled.ArrowRightAlt,
                                    contentDescription = null,
                                    modifier = Modifier.scale(scaleX = -1f, scaleY = 1f)
                                )

                                is Method.Stream.Client -> Icon(
                                    Icons.AutoMirrored.Filled.ArrowRightAlt, contentDescription = null
                                )

                                is Method.Stream.Bidi -> Icon(
                                    Icons.Default.SyncAlt, contentDescription = null
                                )
                            }
                        },
                        trailingIcon = {
                            Box(
                                modifier = Modifier.weight(1f).padding(4.dp), contentAlignment = Alignment.Center
                            ) {
                                if (state.methodsMenuExpanded) {
                                    Icon(Icons.Default.ExpandLess, null)
                                } else {
                                    Icon(Icons.Default.ExpandMore, null)
                                }
                            }
                        })
                    ExposedDropdownMenu(
                        expanded = state.methodsMenuExpanded, onDismissRequest = {
                            coroutineScope.launch {
                                feature.execute(InteractionCommand.ShrinkMethodsMenu)
                            }
                        }) {
                        state.service.methods.forEach { method ->
                            DropdownMenuItem(onClick = {
                                coroutineScope.launch {
                                    if (method == state.selectedMethod) {
                                        feature.execute(InteractionCommand.DeselectMethod)

                                        methodSelectionState.clearText()
                                    } else {
                                        feature.execute(InteractionCommand.SelectMethod(method = method))

                                        methodSelectionState.setTextAndPlaceCursorAtEnd(method.name)
                                    }

                                    feature.execute(InteractionCommand.ShrinkMethodsMenu)
                                }
                            }) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(
                                        space = 4.dp, alignment = Alignment.Start
                                    ), verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(
                                            space = 4.dp, alignment = Alignment.CenterHorizontally
                                        ), verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        when (method) {
                                            is Method.Call.Unary -> Icon(
                                                Icons.AutoMirrored.Filled.Message, contentDescription = null
                                            )

                                            is Method.Call.Server -> Icon(
                                                Icons.AutoMirrored.Filled.ArrowRightAlt,
                                                contentDescription = null,
                                                modifier = Modifier.scale(scaleX = -1f, scaleY = 1f)
                                            )

                                            is Method.Stream.Client -> Icon(
                                                Icons.AutoMirrored.Filled.ArrowRightAlt, contentDescription = null
                                            )

                                            is Method.Stream.Bidi -> Icon(
                                                Icons.Default.SyncAlt, contentDescription = null
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
                            feature.execute(InteractionCommand.Communication.CancelMethod)
                        } else {
                            feature.execute(InteractionCommand.Communication.InvokeMethod)
                        }
                    }
                }, enabled = state.address.isNotBlank()) {
                    if (state.isConnected) {
                        Icon(Icons.Default.CloudOff, null)
                    } else {
                        Icon(Icons.Default.Cloud, null)
                    }
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        feature.execute(InteractionCommand.Communication.SendRequest)
                    }
                }, enabled = state.selectedMethod is Method.Stream && state.isConnected) {
                    Icon(Icons.Default.CloudUpload, null)
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        feature.execute(InteractionCommand.Communication.StopStreaming)
                    }
                }, enabled = state.selectedMethod is Method.Stream && state.isConnected) {
                    Icon(Icons.Default.CloudDone, null)
                }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                state.selectedMethod?.let { method ->
                    MethodItem(method = method, events = state.events, body = state.body, onBodyChange = { body ->
                        coroutineScope.launch {
                            feature.execute(InteractionCommand.ChangeBody(body = body))
                        }
                    }, metadata = state.metadata, onMetadataChange = { metadata ->
                        coroutineScope.launch {
                            feature.execute(InteractionCommand.ChangeMetadata(metadata = metadata))
                        }
                    }, generateRandomBody = {
                        coroutineScope.launch {
                            feature.execute(InteractionCommand.GenerateRandomBody(method = method))
                        }
                    })
                } ?: Text("Upload proto and select method to start")
            }
        }
    })
}