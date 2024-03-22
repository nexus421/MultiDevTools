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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sun.net.httpserver.HttpServer
import coroutine
import kotlinx.coroutines.launch
import kotnexlib.ifNull
import kotnexlib.tryOrNull
import rememberIt
import java.net.InetSocketAddress


/**
 * This generates a simple http server, which accepts all incoming requests.
 * The user can choose, which HTTP-Code can be returned and an optional JSON-Body.
 * Der Port kann ebenfalls eingestellt werden.
 */
class ServerWindowElement : WindowElement() {
    override val name = "Client-Test"

    private var requestHeader by mutableStateOf("")
    private var requestBody by mutableStateOf("")
    private var requestURL by mutableStateOf("")
    private var serverRunning by mutableStateOf(false)
    private var serverPort by mutableStateOf<Int?>(8080)
    private var textToResponse by mutableStateOf("OK")
    private var httpResponseCode by mutableStateOf<Int?>(200)

    private var contentType by mutableStateOf("*/*")
    private var contentTypeIsJson by mutableStateOf(false)
    private var customHeader1 by mutableStateOf<Pair<String, String>?>(null)
    private var customHeader2 by mutableStateOf<Pair<String, String>?>(null)

    private var server = createHttpServer()

    override fun onEnd() {
        super.onEnd()
        coroutine.launch {
            if (serverRunning) {
                server?.stop(1)
                server = createHttpServer()
                serverRunning = serverRunning.not()
            }
        }
    }

    @Composable
    override fun windowComposable() {

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
                    if (serverRunning.not()) server?.start()
                    else {
                        server?.stop(1)
                        server = createHttpServer()
                    }
                    serverRunning = serverRunning.not()
                }
            }) {
                Text(if (serverRunning.not()) "Start server" else "Stop server")
            }

            OutlinedTextField((serverPort ?: "").toString(), modifier = Modifier.fillMaxWidth(), onValueChange = {
                if (serverRunning.not())
                    serverPort = it.toIntOrNull()
            }, label = {
                Text("Server-Port")
            })

            Row(verticalAlignment = Alignment.CenterVertically) {
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

            server.ifNull {
                Text("Error creating server!")
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

    private fun createHttpServer(
        setRequestUrl: (String) -> Unit = {
            requestURL = it
        },
        setRequestHeader: (String) -> Unit = {
            requestHeader = it
        },
        setRequestBody: (String) -> Unit = {
            requestBody = it
        }
    ) = tryOrNull(onError = {
        displayDialog("Error while creating Server. Stacktrace:\n${it.stackTraceToString()}")
    }) {
        HttpServer.create(InetSocketAddress(serverPort ?: 8080), 0).apply {
            createContext("/") { httpExchange ->
                setRequestUrl("Method ${httpExchange.requestMethod} at Path ${httpExchange.requestURI}")
                setRequestHeader(httpExchange.requestHeaders.map { "${it.key} => ${it.value.joinToString()}" }
                    .joinToString(separator = "\n"))
                setRequestBody(String(httpExchange.requestBody.buffered().readAllBytes()))
                httpExchange.responseHeaders.add(
                    "content-type",
                    if (contentTypeIsJson) "application/json" else contentType
                )
                listOfNotNull(customHeader1, customHeader2).forEach {
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
}