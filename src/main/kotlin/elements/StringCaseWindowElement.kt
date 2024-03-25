package elements

import SplittedWindow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import elements.base.WindowElement
import rememberIt

class StringCaseWindowElement : WindowElement() {
    override val name = " StringCaseBuilder"

    @Composable
    override fun windowComposable() {
        var input by rememberIt("")
        var camelCase by rememberIt("")
        var snakeCase by rememberIt("")
        var kebabCase by rememberIt("")
        var pascalCase by rememberIt("")
        SplittedWindow(leftSide = {
            OutlinedTextField(input, modifier = Modifier.fillMaxWidth(), onValueChange = { userInput ->
                input = userInput

                camelCase = input.let {
                    it.mapIndexed { index, c ->
                        if (index == 0) c.lowercase()
                        else if (c.isWhitespace()) ""
                        else if (it.getOrNull(index - 1)?.isWhitespace() == true) c.uppercase()
                        else c
                    }.joinToString(separator = "")
                }
                pascalCase = camelCase.replaceFirstChar { it.uppercase() }

                snakeCase = input.let {
                    it.mapIndexed { index, c ->
                        if (c.isWhitespace()) "_"
                        else c.lowercase()
                    }.joinToString(separator = "")
                }

                kebabCase = snakeCase.replace("_", "-")

            }, label = { Text("Text input") })
        }, middle = null, rightSide = {
            SelectionContainer {
                Column {
                    OutlinedTextField(camelCase, modifier = Modifier.fillMaxWidth(), onValueChange = {
                    }, label = { Text("camelCase") })

                    OutlinedTextField(pascalCase, modifier = Modifier.fillMaxWidth(), onValueChange = {
                    }, label = { Text("PascalCase") })

                    OutlinedTextField(snakeCase, modifier = Modifier.fillMaxWidth(), onValueChange = {
                    }, label = { Text("snake_case") })

                    OutlinedTextField(kebabCase, modifier = Modifier.fillMaxWidth(), onValueChange = {
                    }, label = { Text("kebab-case") })

                }
            }
        })
    }
}