package elements

import SplittedWindow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotnexlib.getRandomString
import rememberIt

class StringGeneratorWindowElement : WindowElement() {
    override val name = "String-Generator"

    @Composable
    override fun windowComposable() {

        val defaultChars = (('A'..'Z') + ('a'..'z') + ('0'..'9')).joinToString(separator = "")
        var charsInput by rememberIt(defaultChars)
        var length by rememberIt(10)
        var randomString by rememberIt("")

        SplittedWindow(leftSide = {
            Column {
                TextField(charsInput, modifier = Modifier.weight(1f).fillMaxWidth(), label = {
                    Text("Chars to use for generation")
                }, onValueChange = {
                    charsInput = it
                })
                TextField(length.toString(), modifier = Modifier.weight(0.5f).fillMaxWidth(), label = {
                    Text("Generated string length")
                }, onValueChange = {
                    length = it.toIntOrNull() ?: return@TextField
                })
            }
        }, middle = {
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                if (charsInput.isBlank()) charsInput = defaultChars
                randomString = getRandomString(length, charsInput.toList())
            }) {
                Text("Generate")
            }
        }, rightSide = {
            SelectionContainer {
                Text(randomString)
            }
        })
    }
}