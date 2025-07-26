package io.github.numq.stub.proto

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.numq.stub.service.Service

@Composable
fun ProtoFileLoadedItem(
    file: ProtoFile.Loaded,
    selectService: (Service) -> Unit,
    delete: (ProtoFile.Loaded) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterVertically)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = {
                        isExpanded = !isExpanded
                    }
                ), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = file.name, modifier = Modifier.padding(8.dp))
                IconButton(onClick = {
                    delete(file)
                }) {
                    Icon(Icons.Default.DeleteForever, null)
                }
            }

            if (file.dependencies.isNotEmpty()) {
                Text("Dependencies:", modifier = Modifier.padding(8.dp))
                file.dependencies.forEach { dependency ->
                    Text(dependency, modifier = Modifier.padding(8.dp))
                }
            }

            AnimatedVisibility(
                visible = file.services.isNotEmpty() && isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Divider()
                Column(
                    modifier = Modifier.fillMaxWidth().animateContentSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterVertically)
                ) {
                    file.services.forEach { service ->
                        Box(
                            modifier = Modifier.fillMaxWidth().clickable { selectService(service) },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(text = service.name, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}