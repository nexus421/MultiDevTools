package elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import getRandomString
import rememberIt

class StringGeneratorWindowElement : WindowElement() {
    override val name = "String-Generator"

    @Composable
    override fun windowComposable() {

        val defaultChars = (('A'..'Z') + ('a'..'z') + ('0'..'9')).joinToString(separator = "")
        var charsInput by rememberIt(defaultChars)
        var length by rememberIt(10)
        var randomString by rememberIt("")

        Column {

            TextField(charsInput, label = {
                Text("Chars to use for generation")
            }, onValueChange = {
                charsInput = it
            })

            TextField(length.toString(), label = {
                Text("Generated string length")
            }, onValueChange = {
                length = it.toIntOrNull() ?: return@TextField
            })

            Button(onClick = {
                if (charsInput.isBlank()) charsInput = defaultChars
                randomString = getRandomString(length, charsInput.toList())
            }) {
                Text("Generate")
            }

            SelectionContainer {
                Text(randomString)
            }

        }
    }
}