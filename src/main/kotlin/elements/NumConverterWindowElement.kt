package elements

import OutTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import rememberIt

class NumConverterWindowElement : WindowElement() {
    override val name = "Number converter"

    private val binaryRegex = "[01]*".toRegex()
    private val octalregex = "[0-7]*".toRegex()
    private val decimalRegex = "[0-9]*".toRegex()
    private val hexRegex = "[0-9a-f]*".toRegex()

    @Composable
    override fun windowComposable() {
        var binary by rememberIt("")
        var octal by rememberIt("")
        var decimal by rememberIt("")
        var hex by rememberIt("")

        Column {
            OutTextField(binary, "Binary") {
                if (it.isBlank()) {
                    binary = ""
                    octal = ""
                    decimal = ""
                    hex = ""
                    return@OutTextField
                }
                if (it.matches(binaryRegex)) {
                    binary = it
                    octal = it.toLong(2).toString(8)
                    decimal = it.toLong(2).toString(10)
                    hex = it.toLong(2).toString(16)
                }
            }

            OutTextField(octal, "Octal") {
                if (it.isBlank()) {
                    binary = ""
                    octal = ""
                    decimal = ""
                    hex = ""
                    return@OutTextField
                }
                if (it.matches(octalregex)) {
                    binary = it.toLong(8).toString(2)
                    octal = it
                    decimal = it.toLong(8).toString(10)
                    hex = it.toLong(8).toString(16)
                }
            }

            OutTextField(decimal, "Decimal") {
                if (it.isBlank()) {
                    binary = ""
                    octal = ""
                    decimal = ""
                    hex = ""
                    return@OutTextField
                }
                if (it.matches(decimalRegex)) {
                    binary = it.toLong(10).toString(2)
                    octal = it.toLong(10).toString(8)
                    decimal = it
                    hex = it.toLong(10).toString(16)
                }
            }

            OutTextField(hex, "Hex") {
                if (it.isBlank()) {
                    binary = ""
                    octal = ""
                    decimal = ""
                    hex = ""
                    return@OutTextField
                }
                if (it.matches(hexRegex)) {
                    binary = it.toLong(16).toString(2)
                    octal = it.toLong(16).toString(8)
                    decimal = it.toLong(16).toString(10)
                    hex = it
                }
            }

            Text("Hint: Do not enter numbers larger than 64 bit (long). Otherwise it will crash.", fontSize = 11.sp)

        }
    }
}