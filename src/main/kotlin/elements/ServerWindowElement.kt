package elements

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.sun.net.httpserver.HttpServer
import coroutine
import elements.base.DefaultSplitWindowElement
import kotlinx.coroutines.launch
import kotnexlib.format
import kotnexlib.tryOrNull
import rememberIt
import java.net.InetSocketAddress
import java.util.*


/**
 * This generates a simple http server, which accepts all incoming requests.
 * The user can choose, which HTTP-Code can be returned and an optional JSON-Body.
 * Der Port kann ebenfalls eingestellt werden.
 */
class ServerWindowElement : DefaultSplitWindowElement() {
    override val name = "Server-Dummy"

    private var receivedHeader by mutableStateOf("")
    private var receivedBody by mutableStateOf("")
    private var requestURL by mutableStateOf("")
    private var serverRunning by mutableStateOf(false)
    private var serverPort by mutableStateOf<Int?>(8080)
    private var textToResponse by mutableStateOf("OK")
    private var httpResponseCode by mutableStateOf<Int?>(200)

    private var contentType by mutableStateOf("*/*")
    private var contentTypeIsJson by mutableStateOf(false)
    private var customHeader1 by mutableStateOf<Pair<String, String>?>(null)
    private var customHeader2 by mutableStateOf<Pair<String, String>?>(null)

    private var server: HttpServer? = null

    private var lastReceivedCall by mutableStateOf<Date?>(null)

    override fun onStart() {
        super.onStart()
    }

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

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        Column {
            Text("Caller-Data")

            //Incoming info from client
            OutlinedTextField(lastReceivedCall?.format("dd.MM.yyyy hh:mm:ss") ?: "", label = {
                Text("Last received call")
            }, onValueChange = {}, modifier = Modifier.fillMaxWidth(), enabled = false)
            OutlinedTextField(
                requestURL,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {},
                label = { Text("Called URL") })
            OutlinedTextField(
                receivedHeader,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {},
                label = { Text("Received Header") })
            OutlinedTextField(
                receivedBody,
                modifier = Modifier.fillMaxWidth().weight(1f),
                onValueChange = {},
                label = { Text("Received Body") })
        }
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        Column {
            Text("Server-Settings")
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
                    Text("HTTP-Code to respond")
                })

            Divider(Modifier.padding(4.dp), color = Color.LightGray)

            Text(
                "Define up to two custom headers below, which will be additionally sent to the client.",
                fontStyle = FontStyle.Italic
            )

            customHeader1.let { header ->
                Column(
                    modifier = Modifier.padding(2.dp).border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(4.dp)
                ) {
                    Text("Header 1 (optional)")
                    var key by rememberIt(header?.first ?: "")
                    var value by rememberIt(header?.second ?: "")
                    OutlinedTextField(key, modifier = Modifier.fillMaxWidth(), onValueChange = {
                        key = it
                        customHeader1 = if (key.isBlank() || value.isEmpty()) null else Pair(key, value)
                    }, label = {
                        Text("Key")
                    })

                    OutlinedTextField(value, modifier = Modifier.fillMaxWidth(), onValueChange = {
                        value = it
                        customHeader1 = if (key.isBlank() || value.isEmpty()) null else Pair(key, value)
                    }, label = {
                        Text("Value")
                    })

                }
            }

            customHeader2.let { header ->
                Column(
                    modifier = Modifier.padding(2.dp).border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(4.dp)
                ) {
                    Text("Header 2 (optional)")
                    var key by rememberIt(header?.first ?: "")
                    var value by rememberIt(header?.second ?: "")
                    OutlinedTextField(key, modifier = Modifier.fillMaxWidth(), onValueChange = {
                        key = it
                        customHeader2 = if (key.isBlank() || value.isEmpty()) null else Pair(key, value)
                    }, label = {
                        Text("Key")
                    })

                    OutlinedTextField(value, modifier = Modifier.fillMaxWidth(), onValueChange = {
                        value = it
                        customHeader2 = if (key.isBlank() || value.isEmpty()) null else Pair(key, value)
                    }, label = {
                        Text("Value")
                    })
                }
            }
        }
    }

    override fun getMiddle(): @Composable ColumnScope.() -> Unit = {
        //Settings
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            coroutine.launch {
                showLoading()
                if (server == null) server = createHttpServer()
                if (serverRunning.not()) {
                    server?.start()
                    postSuccessMessage("Starting server...")
                } else {
                    server?.stop(1)
                    server = createHttpServer()
                }
                serverRunning = serverRunning.not()
                dismissLoading()
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

        OutlinedTextField(
            if (contentTypeIsJson) "application/json" else contentType,
            onValueChange = {
                if (contentTypeIsJson) return@OutlinedTextField
                contentType = it
            },
            label = {
                Text("content-type")
            })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("JSON Content-Type ")
            Checkbox(contentTypeIsJson, onCheckedChange = { contentTypeIsJson = it })
        }
    }

    override fun getTopView(): @Composable BoxScope.() -> Unit = {
        Text(
            """
                This provides a simple server to test your client applications.
                Click "Start server" to run the server with the given configuration below.
                The Server will be accessible on http://localhost:<Server-Port>.
                Everything received will be displayed on the left side.
            """.trimIndent()
        )
    }

    private fun createHttpServer(
        setRequestUrl: (String) -> Unit = {
            requestURL = it
        },
        setReceivedHeader: (String) -> Unit = {
            receivedHeader = it
        },
        setReceivedBody: (String) -> Unit = {
            receivedBody = it
        }
    ) = tryOrNull(onError = {
        displayDialog("Error while creating Server. Stacktrace:\n${it.stackTraceToString()}")
        postErrorMessage("Error creating server.")
        log("Error starting Server", it)
        serverRunning = false
    }) {
        HttpServer.create(InetSocketAddress(serverPort ?: 8080), 0).apply {
            createContext("/") { httpExchange ->
                setRequestUrl("Method ${httpExchange.requestMethod} at Path ${httpExchange.requestURI}")
                setReceivedHeader(httpExchange.requestHeaders.map { "${it.key} => ${it.value.joinToString()}" }
                    .joinToString(separator = "\n"))
                setReceivedBody(String(httpExchange.requestBody.buffered().readAllBytes()))
                lastReceivedCall = Date()
                postMessage("Received new request!")
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