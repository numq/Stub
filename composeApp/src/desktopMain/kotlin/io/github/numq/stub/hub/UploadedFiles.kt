package io.github.numq.stub.hub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.numq.stub.proto.ProtoFile
import io.github.numq.stub.proto.ProtoFileLoadedItem
import io.github.numq.stub.proto.ProtoFileUploadedItem
import io.github.numq.stub.service.Service

@Composable
fun UploadedFiles(
    files: List<ProtoFile>,
    openFileDialog: () -> Unit,
    deleteFile: (ProtoFile) -> Unit,
    selectedService: Service?,
    selectService: (ProtoFile, Service) -> Unit,
    deselectService: () -> Unit,
) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(onClick = openFileDialog, modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp, alignment = Alignment.CenterHorizontally
                ), verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Upload files")
                Icon(Icons.Default.UploadFile, null)
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            state = listState
        ) {
            itemsIndexed(files, key = { _, item -> item.id }) { index, file ->
                Card {
                    when (file) {
                        is ProtoFile.Uploaded -> ProtoFileUploadedItem(file = file, delete = deleteFile)

                        is ProtoFile.Loaded -> ProtoFileLoadedItem(
                            file = file, selectService = { service ->
                                if (selectedService == service) {
                                    deselectService()
                                } else {
                                    selectService(file, service)
                                }
                            }, delete = deleteFile
                        )
                    }
                }
                if (index < files.lastIndex) {
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}