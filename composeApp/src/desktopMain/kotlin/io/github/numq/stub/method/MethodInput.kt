package io.github.numq.stub.method

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MethodInput(body: String, onBodyChange: (String) -> Unit, generateRandomBody: () -> Unit) {
    OutlinedTextField(
        value = body,
        onValueChange = { value ->
            onBodyChange(value.trim())
        },
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        leadingIcon = {
            IconButton(onClick = generateRandomBody) {
                Icon(Icons.Default.Casino, null)
            }
        },
        trailingIcon = {
            if (body.isNotEmpty()) {
                IconButton(onClick = {
                    onBodyChange("")
                }, modifier = Modifier.padding(4.dp)) {
                    Icon(Icons.Default.Clear, null)
                }
            }
        }
    )
}