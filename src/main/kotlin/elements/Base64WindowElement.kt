@file:OptIn(ExperimentalEncodingApi::class)

package elements

import SplittedWindow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
        var realtime by rememberIt(true)

        SplittedWindow(leftSide = {
            TextField(input, placeholder = {
                Text("Raw text")
            }, modifier = Modifier.fillMaxSize(), onValueChange = {
                input = it
                if (realtime) {
                    tryOrNull {
                        output = Base64.encode(input.toByteArray())
                    }
                }
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Realtime")
                Divider(Modifier.weight(1f))
                Checkbox(realtime, onCheckedChange = {
                    realtime = it
                })
            }
        }, rightSide = {
            TextField(output, placeholder = {
                Text("Base64")
            }, modifier = Modifier.fillMaxSize(), onValueChange = {
                output = it
                if (realtime) {
                    tryOrNull {
                        input = Base64.decode(output).toString(Charsets.UTF_8)
                    }
                }
            }, label = {
                Text("Base64")
            })
        })
    }
}