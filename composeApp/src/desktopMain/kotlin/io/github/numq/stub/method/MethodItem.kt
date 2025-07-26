package io.github.numq.stub.method

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.numq.stub.client.ClientEvent

@Composable
fun MethodItem(
    method: Method,
    events: List<ClientEvent>,
    body: String,
    onBodyChange: (String) -> Unit,
    metadata: String,
    onMetadataChange: (String) -> Unit,
    generateRandomBody: () -> Unit,
) {
    val (selectedTab, setSelectedTab) = remember(method) { mutableStateOf(MethodInputTab.CONTENT) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            MethodEvents(events = events)
        }
        Box(modifier = Modifier.animateContentSize(), contentAlignment = Alignment.BottomCenter) {
            when (selectedTab) {
                MethodInputTab.CONTENT -> {
                    MethodInput(
                        body = body,
                        onBodyChange = onBodyChange,
                        generateRandomBody = generateRandomBody
                    )
                }

                MethodInputTab.METADATA -> {
                    MethodMetadata(
                        metadata = metadata,
                        onMetadataChange = onMetadataChange
                    )
                }
            }
        }
        TabRow(modifier = Modifier.fillMaxWidth(), selectedTabIndex = selectedTab.ordinal) {
            Tab(selected = selectedTab == MethodInputTab.CONTENT, onClick = {
                setSelectedTab(MethodInputTab.CONTENT)
            }, enabled = selectedTab != MethodInputTab.CONTENT, text = {
                Text("Content")
            })
            Tab(selected = selectedTab == MethodInputTab.METADATA, onClick = {
                setSelectedTab(MethodInputTab.METADATA)
            }, enabled = selectedTab != MethodInputTab.METADATA, text = {
                Text("Metadata")
            })
        }
    }
}