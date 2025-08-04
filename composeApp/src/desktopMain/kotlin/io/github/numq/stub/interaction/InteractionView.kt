package io.github.numq.stub.interaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.numq.stub.method.Method
import io.github.numq.stub.method.MethodItem
import io.github.numq.stub.proto.ProtoFilePreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractionView(
    feature: InteractionFeature,
    previewDrawerState: BottomDrawerState,
) {
    val coroutineScope = rememberCoroutineScope()

    val state by feature.state.collectAsState()

    var methodSelectionValue by remember { mutableStateOf(TextFieldValue()) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        BottomDrawer(
            modifier = Modifier.fillMaxSize(),
            drawerBackgroundColor = Color.Transparent,
            drawerContentColor = contentColorFor(MaterialTheme.colors.surface),
            scrimColor = MaterialTheme.colors.onSurface.copy(alpha = .32f),
            drawerContent = {
                ProtoFilePreview(content = state.service.fileContent)
            },
            drawerState = previewDrawerState,
            content = {
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
                                value = methodSelectionValue,
                                onValueChange = { methodSelectionValue = it },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).weight(1f),
                                readOnly = true,
                                singleLine = true,
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
                                        modifier = Modifier.weight(1f).padding(4.dp),
                                        contentAlignment = Alignment.Center
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
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            coroutineScope.launch {
                                                if (method == state.selectedMethod) {
                                                    feature.execute(InteractionCommand.DeselectMethod)

                                                    methodSelectionValue = TextFieldValue("")
                                                } else {
                                                    feature.execute(InteractionCommand.SelectMethod(method = method))

                                                    methodSelectionValue = TextFieldValue(
                                                        text = method.name, selection = TextRange(method.name.length)
                                                    )
                                                }

                                                feature.execute(InteractionCommand.ShrinkMethodsMenu)
                                            }
                                        }, horizontalArrangement = Arrangement.spacedBy(
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
                            MethodItem(
                                method = method,
                                events = state.events,
                                body = state.body,
                                onBodyChange = { body ->
                                    coroutineScope.launch {
                                        feature.execute(InteractionCommand.ChangeBody(body = body))
                                    }
                                },
                                metadata = state.metadata,
                                onMetadataChange = { metadata ->
                                    coroutineScope.launch {
                                        feature.execute(InteractionCommand.ChangeMetadata(metadata = metadata))
                                    }
                                },
                                generateRandomBody = {
                                    coroutineScope.launch {
                                        feature.execute(InteractionCommand.GenerateRandomBody(method = method))
                                    }
                                })
                        } ?: Text("Upload proto and select method to start")
                    }
                }
            })
    }
}