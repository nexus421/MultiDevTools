package elements

import SplittedWindow
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
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

        var request by remember { mutableStateOf("") }
        val server = remember {
            HttpServer.create(InetSocketAddress(8080), 0).apply {
                createContext("/") {
                    //ToDo: Empfangene Daten schöner ausgeben.
                    val requestData = String(it.requestBody.buffered().readAllBytes())

                    request = requestData + it.requestHeaders.toString() + it.requestMethod + it.requestURI.toString()
                    val response = "OK" // Antworttext
                    it.sendResponseHeaders(200, response.length.toLong()) // Statuscode und Länge der Antwort setzen

                    val os = it.responseBody // Output-Stream der Antwort erhalten
                    os.write(response.toByteArray()) // Antwort schreiben
                    os.close()
                }
            }
        }

        SplittedWindow(leftSide = {
            //Incoming info from client
            Text(request)
        }, middle = {
            //Settings
            Button(onClick = {
                server.start()
            }) {
                Text("Server starten")
            }
        }, rightSide = {
            //Outgoing JSON (optional)
        })
    }
}