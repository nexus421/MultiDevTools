package elements

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import elements.base.DefaultSplitWindowElement
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * A class that represents a window element for URL Encoding
 */
class UrlEncodingWindowElement : DefaultSplitWindowElement() {

    override val name = "URL Encoding/Decoding"

    private var encodedUrl by mutableStateOf("")
    private var decodedUrl by mutableStateOf("")

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        Column { // start of the Compose Column
            OutlinedTextField(
                value = decodedUrl,
                onValueChange = { decodedUrl = it },
                label = { Text("Decoded URL") }
            )
            Button(onClick = {
                try {
                    encodedUrl = URLEncoder.encode(decodedUrl, "UTF-8")
                    postMessage("URL successfully encoded!")
                } catch (e: IllegalArgumentException) {
                    postErrorMessage("Invalid URL for encoding!")
                }
            }) {
                Text("Encode URL")
            }
        }
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        // start of the right-side function
        Column { // start of the Compose Column
            OutlinedTextField(
                value = encodedUrl,
                onValueChange = { encodedUrl = it },
                label = { Text("Encoded URL") }
            )
            Button(onClick = {
                try {
                    decodedUrl = URLDecoder.decode(encodedUrl, "UTF-8")
                    postMessage("Decoded URL successfully!")
                } catch (e: IllegalArgumentException) {
                    postErrorMessage("Invalid encoded URL!")
                }
            }) {
                Text("Decode URL")
            }
        }
    }

    override fun getMiddle(): @Composable (ColumnScope.() -> Unit)? = null

    override fun getTopView(): @Composable BoxScope.() -> Unit = {

    }

}