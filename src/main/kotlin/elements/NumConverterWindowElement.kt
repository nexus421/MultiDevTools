package elements

import OutTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import elements.base.WindowElement
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
                    val convertedNum = it.toLongOrErr(2) ?: return@OutTextField
                    binary = it
                    octal = convertedNum.toString(8)
                    decimal = convertedNum.toString(10)
                    hex = convertedNum.toString(16)
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
                    val convertedNum = it.toLongOrErr(8) ?: return@OutTextField
                    binary = convertedNum.toString(2)
                    octal = it
                    decimal = convertedNum.toString(10)
                    hex = convertedNum.toString(16)
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
                    val convertedNum = it.toLongOrErr(10) ?: return@OutTextField
                    binary = convertedNum.toString(2)
                    octal = convertedNum.toString(8)
                    decimal = it
                    hex = convertedNum.toString(16)
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
                    val convertedNum = it.toLongOrErr(16) ?: return@OutTextField
                    binary = convertedNum.toString(2)
                    octal = convertedNum.toString(8)
                    decimal = convertedNum.toString(10)
                    hex = it
                }
            }

            Text("Hint: You can't enter numbers larger than 2^64 (long).", fontSize = 11.sp)

        }
    }

    private fun String.toLongOrErr(radix: Int): Long? {
        return toLongOrNull(radix).apply {
            if (this == null)
                displayDialog("Number is to large to convert. Only numbers in range of 2^64 (long) are possible.")
        }
    }
}