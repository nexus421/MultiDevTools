package elements

import OutTextField
import SplittedWindow
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import rememberIt
import tryOrNull

class RegexWindowElement : WindowElement() {
    override val name = "Regex matcher"

    @Composable
    override fun windowComposable() {
        var regexInput by rememberIt("")
        var text by rememberIt("")
        val matches = remember { mutableStateListOf<String>() }

        SplittedWindow(leftSide = {
            Column {
                OutTextField(regexInput, "Regex") { input ->
                    regexInput = input
                    if (text.isNotEmpty()) {
                        tryOrNull { regexInput.toRegex() }?.let { regex ->
                            matches.clear()
                            matches.addAll(
                                regex.findAll(text).mapNotNull { if (it.value.isBlank()) null else it.value })
                        }
                    }
                }
                OutTextField(text, "Example text") { input ->
                    text = input
                    if (text.isNotEmpty()) {
                        tryOrNull { regexInput.toRegex() }?.let { regex ->
                            matches.clear()
                            matches.addAll(
                                regex.findAll(text).mapNotNull { if (it.value.isBlank()) null else it.value })
                        }
                    }
                }
            }
        }, middle = null, rightSide = {
            OutTextField(matches.joinToString(separator = "\n") { "=> $it" }, "Matches") {}
        })
    }
}