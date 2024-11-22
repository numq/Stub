package method

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import client.ClientEvent
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MethodCallEvent(event: ClientEvent) {
    Card {
        Column(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 4.dp, alignment = Alignment.CenterVertically)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (event) {
                        is ClientEvent.Error -> {
                            Icon(Icons.Default.Error, null)
                            Text(
                                event.throwable.localizedMessage ?: "Something get wrong",
                                modifier = Modifier.padding(4.dp)
                            )
                        }

                        is ClientEvent.Started -> {
                            Icon(Icons.Default.Link, null)
                            Text("Connected", modifier = Modifier.padding(4.dp))
                        }

                        is ClientEvent.Request -> {
                            Icon(Icons.Default.CallMade, null)
                            Text("Request", modifier = Modifier.padding(4.dp))
                        }

                        is ClientEvent.Response -> {
                            Icon(Icons.Default.CallReceived, null)
                            Text("Response", modifier = Modifier.padding(4.dp))
                        }

                        is ClientEvent.Cancelled -> {
                            Icon(Icons.Default.Cancel, null)
                            Text("Cancelled", modifier = Modifier.padding(4.dp))
                        }

                        is ClientEvent.Completed -> {
                            Icon(Icons.Default.LinkOff, null)
                            Text("Disconnected", modifier = Modifier.padding(4.dp))
                        }
                    }
                }
                Text(
                    SimpleDateFormat.getDateTimeInstance().format(Date(event.receivedAt.inWholeMilliseconds)),
                    modifier = Modifier.padding(4.dp)
                )
            }
            when (event) {
                is ClientEvent.Request -> {
                    Divider()
                    Text(
                        event.inputMessage.body.takeIf(String::isNotBlank) ?: "{\n}",
                        modifier = Modifier.fillMaxWidth().padding(4.dp)
                    )
                }

                is ClientEvent.Response -> {
                    Divider()
                    Text(
                        event.outputMessage.body.takeIf(String::isNotBlank) ?: "{\n}",
                        modifier = Modifier.fillMaxWidth().padding(4.dp)
                    )
                }

                else -> Unit
            }
        }
    }
}