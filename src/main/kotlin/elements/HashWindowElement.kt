package elements

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import hash
import rememberIt

class HashWindowElement : WindowElement() {
    override val name = "Hash"

    @Composable
    override fun windowComposable() {
        var rawInput by rememberIt("")
        var hashedInput by rememberIt("")

        Column {
            TextField(rawInput, onValueChange = {
                rawInput = it
                //ToDo: Button: Realtime-Hashen
                //hashedInput = rawInput.hash()
            })
            Button(onClick = {
                hashedInput = rawInput.hash()
            }) {
                Text("Hash")
            }
            Text(hashedInput)
        }
    }
}