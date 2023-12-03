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
import hash
import rememberIt

class HashWindowElement : WindowElement() {
    override val name = "Hash"

    @Composable
    override fun windowComposable() {
        var rawInput by rememberIt("")
        var hashedInput by rememberIt("")

        SplittedWindow(leftSide = {
            TextField(rawInput, modifier = Modifier.fillMaxSize(), onValueChange = {
                rawInput = it
                //ToDo: Button: Realtime-Hashen
                //hashedInput = rawInput.hash()
            })
        }, middle = {
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                hashedInput = rawInput.hash()
            }) {
                Text("Hash")
            }
        }, rightSide = {
            Text(hashedInput, modifier = Modifier.fillMaxSize())
        })
    }
}