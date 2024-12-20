package proto

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProtoFileUploadedItem(
    file: ProtoFile.Uploaded,
    delete: (ProtoFile.Uploaded) -> Unit,
) {
    Card {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterVertically)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = file.name, modifier = Modifier.padding(8.dp), color = Color.Red)
                IconButton(onClick = {
                    delete(file)
                }) {
                    Icon(Icons.Default.DeleteForever, null, tint = Color.Red)
                }
            }
            Divider()
            Text("Missing dependencies", modifier = Modifier.padding(8.dp), color = Color.Red)
            file.missingDependencies.forEach { missingDependency ->
                Text(missingDependency, modifier = Modifier.padding(8.dp), color = Color.Red)
            }
        }
    }
}