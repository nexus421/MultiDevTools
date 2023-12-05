@file:OptIn(ExperimentalEncodingApi::class)

package elements

import SplittedWindow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import rememberIt
import tryOrNull
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class Base64WindowElement : WindowElement() {
    override val name = "Base64"

    @Composable
    override fun windowComposable() {

        var input by rememberIt("")
        var output by rememberIt("")

        SplittedWindow(leftSide = {
            TextField(input, placeholder = {
                Text("Raw text")
            }, modifier = Modifier.fillMaxSize(), onValueChange = {
                input = it
            }, label = {
                Text("Raw text")
            })
        }, middle = {
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                tryOrNull {
                    output = Base64.encode(input.toByteArray())
                }
            }) {
                Text(">> to Base64 >>")
            }

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                tryOrNull {
                    input = Base64.decode(output).toString(Charsets.UTF_8)
                }
            }) {
                Text("<< to raw text <<")
            }
        }, rightSide = {
            TextField(output, placeholder = {
                Text("Base64")
            }, modifier = Modifier.fillMaxSize(), onValueChange = {
                output = it
            }, label = {
                Text("Base64")
            })
        })
    }
}