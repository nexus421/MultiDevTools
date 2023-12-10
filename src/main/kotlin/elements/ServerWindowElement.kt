package elements

import SplittedWindow
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sun.net.httpserver.HttpServer
import coroutine
import kotlinx.coroutines.launch
import rememberIt
import java.net.InetSocketAddress


/**
 * This generates a simple http server, which accepts all incoming requests.
 * The user can choose, which HTTP-Code can be returned and an optional JSON-Body.
 * Der Port kann ebenfalls eingestellt werden.
 */
class ServerWindowElement : WindowElement() {
    override val name = "Client-Test"

    @Composable
    override fun windowComposable() {

        var requestHeader by rememberIt("")
        var requestBody by rememberIt("")
        var requestURL by rememberIt("")
        var serverRunning by rememberIt(false)
        var serverPort by rememberIt<Int?>(8080)
        var textToResponse by rememberIt("OK")
        var httpResponseCode by rememberIt<Int?>(200)

        var contentType by rememberIt("*/*")
        var contentTypeIsJson by rememberIt(false)
        var customHeader1 by rememberIt<Pair<String, String>?>(null)
        var customHeader2 by rememberIt<Pair<String, String>?>(null)

        var server = remember {
            createHttpServer(serverPort = serverPort, setRequestUrl = {
                requestURL = it
            }, setRequestBody = {
                requestBody = it
            }, setRequestHeader = {
                requestHeader = it
            }, contentTypeIsJson = contentTypeIsJson,
                contentType = contentType,
                httpResponseCode = httpResponseCode,
                textToResponse = textToResponse,
                customHeader = listOf(customHeader1, customHeader2).filterNotNull()
            )
        }

        SplittedWindow(leftSide = {
            Column {
                //Incoming info from client
                OutlinedTextField(
                    requestURL,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {},
                    label = { Text("URL") })
                OutlinedTextField(
                    requestHeader,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {},
                    label = { Text("Header") })
                OutlinedTextField(
                    requestBody,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    onValueChange = {},
                    label = { Text("Body") })
            }
        }, middle = {
            //Settings
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                coroutine.launch {
                    if (serverRunning.not()) server.start()
                    else {
                        server.stop(1)
                        server = createHttpServer(serverPort = serverPort, setRequestUrl = {
                            requestURL = it
                        }, setRequestBody = {
                            requestBody = it
                        }, setRequestHeader = {
                            requestHeader = it
                        }, contentTypeIsJson = contentTypeIsJson,
                            contentType = contentType,
                            httpResponseCode = httpResponseCode,
                            textToResponse = textToResponse,
                            customHeader = listOf(customHeader1, customHeader2).filterNotNull()
                        )
                    }
                    serverRunning = serverRunning.not()
                }
            }) {
                Text(if (serverRunning.not()) "Start server" else "Stop server")
            }

            OutlinedTextField((serverPort ?: "").toString(), modifier = Modifier.fillMaxWidth(), onValueChange = {
                serverPort = it.toIntOrNull()
            }, label = {
                Text("Server-Port")
            })

            Row {
                OutlinedTextField(
                    if (contentTypeIsJson) "application/json" else contentType,
                    modifier = Modifier.weight(1f),
                    onValueChange = {
                        if (contentTypeIsJson) return@OutlinedTextField
                        contentType = it
                    },
                    label = {
                        Text("content-type")
                    })
                Text(" is JSON ")
                Checkbox(contentTypeIsJson, onCheckedChange = { contentTypeIsJson = it })
            }

            customHeader1.let { header ->
                Column(
                    modifier = Modifier.padding(2.dp).border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).padding(4.dp)
                ) {
                    var key by rememberIt(header?.first ?: "")
                    var value by rememberIt(header?.second ?: "")
                    OutlinedTextField(key, modifier = Modifier.fillMaxWidth(), onValueChange = {
                        key = it
                        customHeader1 = if (key.isBlank() || value.isEmpty()) null else Pair(key, value)
                    }, label = {
                        Text("Custom header 1: Key")
                    })

                    OutlinedTextField(value, modifier = Modifier.fillMaxWidth(), onValueChange = {
                        value = it
                        customHeader1 = if (key.isBlank() || value.isEmpty()) null else Pair(key, value)
                    }, label = {
                        Text("Custom header 1: Value")
                    })

                }
            }

            customHeader2.let { header ->
                Column(
                    modifier = Modifier.padding(2.dp).border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).padding(4.dp)
                ) {
                    var key by rememberIt(header?.first ?: "")
                    var value by rememberIt(header?.second ?: "")
                    OutlinedTextField(key, modifier = Modifier.fillMaxWidth(), onValueChange = {
                        key = it
                        customHeader2 = if (key.isBlank() || value.isEmpty()) null else Pair(key, value)
                    }, label = {
                        Text("Custom header 2: Key")
                    })

                    OutlinedTextField(value, modifier = Modifier.fillMaxWidth(), onValueChange = {
                        value = it
                        customHeader2 = if (key.isBlank() || value.isEmpty()) null else Pair(key, value)
                    }, label = {
                        Text("Custom header 2: Value")
                    })
                }
            }

        }, rightSide = {
            Column {
                //Outgoing JSON (optional)
                OutlinedTextField(textToResponse, modifier = Modifier.fillMaxWidth(), onValueChange = {
                    textToResponse = it
                }, label = {
                    Text("Text to respond or empty")
                })

                OutlinedTextField(
                    (httpResponseCode ?: "").toString(),
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        httpResponseCode = it.toIntOrNull()
                    },
                    label = {
                        Text("HTTP-Code")
                    })
            }
        })
    }

    fun createHttpServer(
        serverPort: Int?,
        setRequestUrl: (String) -> Unit,
        setRequestHeader: (String) -> Unit,
        setRequestBody: (String) -> Unit,
        contentTypeIsJson: Boolean,
        contentType: String,
        httpResponseCode: Int?,
        textToResponse: String,
        customHeader: List<Pair<String, String>>
    ) = HttpServer.create(InetSocketAddress(serverPort ?: 8080), 0).apply {
        createContext("/") { httpExchange ->
            //ToDo: Empfangene Daten schöner ausgeben.
            setRequestUrl("Method ${httpExchange.requestMethod} at Path ${httpExchange.requestURI}")
            setRequestHeader(httpExchange.requestHeaders.map { "${it.key} => ${it.value.joinToString()}" }
                .joinToString(separator = "\n"))
            setRequestBody(String(httpExchange.requestBody.buffered().readAllBytes()))
            httpExchange.responseHeaders.add(
                "content-type",
                if (contentTypeIsJson) "application/json" else contentType
            )
            customHeader.forEach {
                httpExchange.responseHeaders.add(it.first, it.second)
            }
            httpExchange.sendResponseHeaders(
                httpResponseCode ?: 200,
                textToResponse.length.toLong()
            ) // Statuscode und Länge der Antwort setzen
            httpExchange.responseBody.use {  // Output-Stream der Antwort erhalten
                it.write(textToResponse.toByteArray())
            }
        }
    }
}