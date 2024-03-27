package elements

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import elements.base.DefaultSplitWindowElement
import kotnexlib.compress
import kotnexlib.decompress

class CompressWindowElement : DefaultSplitWindowElement() {

    override val name = "String-Compressor"

    private var decompressedInput by mutableStateOf("")
    private var compressedInput by mutableStateOf("")

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            OutlinedTextField(decompressedInput, {
                decompressedInput = it
            }, label = {
                Text("Decompressed text")
            })

            if (decompressedInput.isBlank()) return@Column
            Button({
                if (decompressedInput.isBlank()) return@Button postErrorMessage("Empty text can't be compressed")
                try {
                    compressedInput =
                        decompressedInput.compress() ?: return@Button postErrorMessage("Text could not be compressed")
                } catch (e: Exception) {
                    log("Error compressing", e)
                }
            }) {
                Text("Compress")
            }
        }
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            OutlinedTextField(compressedInput, {
                compressedInput = it
            }, label = {
                Text("Compressed text")
            })

            if (compressedInput.isBlank()) return@Column
            Button({
                if (compressedInput.isBlank()) return@Button postErrorMessage("Empty text can't be decompressed")
                try {
                    decompressedInput =
                        compressedInput.decompress() ?: return@Button postErrorMessage("Text could not be decompressed")
                } catch (e: Exception) {
                    log("Error decompressing", e)
                }
            }) {
                Text("Decompress")
            }
        }
    }

    override fun getMiddle() = null

    override fun getTopView(): @Composable BoxScope.() -> Unit = {
        Text(
            """
            Compress or decompress any String that was compressed/decompressed through this method (or the extension-function from KotNexLib).
            Hint: This is only useful for large Strings. The overhead for small strings is to large.
        """.trimIndent()
        )
    }

}