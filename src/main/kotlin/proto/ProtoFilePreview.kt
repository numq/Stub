package proto

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProtoFilePreview(content: String) {
    Card(modifier = Modifier.verticalScroll(state = rememberScrollState(0))) {
        Text(content, modifier = Modifier.fillMaxSize().padding(8.dp))
    }
}