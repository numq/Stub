package io.github.numq.stub.method

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MethodMetadata(metadata: String, onMetadataChange: (String) -> Unit) {
    Card {
        OutlinedTextField(
            value = metadata,
            onValueChange = { value ->
                onMetadataChange(value.trim())
            },
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            trailingIcon = {
                if (metadata.isNotEmpty()) {
                    IconButton(onClick = {
                        onMetadataChange("")
                    }, modifier = Modifier.padding(4.dp)) {
                        Icon(Icons.Default.Clear, null)
                    }
                }
            }
        )
    }
}