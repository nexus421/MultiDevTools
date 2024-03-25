package elements

import ButtonText
import CheckBoxText
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.unit.sp
import elements.base.DefaultSplitWindowElement
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import org.jetbrains.skiko.toBufferedImage
import qrcode.QRCode
import utils.TransferableImage
import utils.copyToClipboard
import java.awt.Toolkit

class QrCodeWindowElement : DefaultSplitWindowElement() {
    override val name = "QR-Code generator"

    private var qrInput by mutableStateOf("")
    private var qrBitmap by mutableStateOf<Bitmap?>(null)
    private var liveGeneration by mutableStateOf(false)
    private var expandDropDown by mutableStateOf(false)
    private var selectedType by mutableStateOf(Types.Rounded)
    private var hexColor by mutableStateOf("ffffff")

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        OutlinedTextField(qrInput, modifier = Modifier.fillMaxWidth(), onValueChange = {
            qrInput = it
            if (liveGeneration) {
                generateQrCode(qrInput, selectedType, hexColor) {
                    qrBitmap = it
                    val image = TransferableImage(it.toBufferedImage())
                    Toolkit.getDefaultToolkit().systemClipboard.setContents(image, image)
                }
            }
        }, label = { Text("QR-Code data") })
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        qrBitmap?.let {
            Image(it.asComposeImageBitmap(), null)

        }
    }

    override fun getMiddle(): @Composable ColumnScope.() -> Unit = {
        ButtonText("Generate", modifier = Modifier.fillMaxWidth(), onClick = {
            generateQrCode(qrInput, selectedType, hexColor) { qrBitmap = it }
        })
        CheckBoxText("Live generation", liveGeneration) { liveGeneration = it }

        Box {
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                expandDropDown = expandDropDown.not()
            }) {
                Text(selectedType.name, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = "DropDown")
            }
            DropdownMenu(expandDropDown, onDismissRequest = { expandDropDown = false }) {
                Types.entries.forEach {
                    DropdownMenuItem(onClick = {
                        selectedType = it
                        expandDropDown = false
                        generateQrCode(qrInput, selectedType, hexColor) {
                            qrBitmap = it
                        }
                    }) {
                        Text(it.name)
                    }
                }
            }
        }

        qrBitmap?.let {
            Button({
                it.copyToClipboard()
                postSuccessMessage("Copied image to clipboard")
            }, Modifier.fillMaxWidth()) { Text("Copy to Clipboard") }
        }

        Spacer(Modifier.weight(1f))
        Text("Based on https://github.com/g0dkar/qrcode-kotlin", fontSize = 10.sp)
    }

    override fun getTopView() = null

    private fun generateQrCode(input: String, types: Types, hexColor: String, onResult: (Bitmap) -> Unit) {
        val qrRenderedPNG = QRCode.let {
            when (types) {
                Types.Rounded -> it.ofCircles()
                Types.Squares -> it.ofSquares()
                Types.RoundedSquares -> it.ofRoundedSquares()
            }
        }.withColor(org.jetbrains.skia.Color.WHITE)
            .withBackgroundColor(org.jetbrains.skia.Color.BLACK)
            .withGradientColor(org.jetbrains.skia.Color.MAGENTA, org.jetbrains.skia.Color.CYAN)
            .build(input)
            .renderToBytes()
        onResult(Bitmap.makeFromImage(Image.makeFromEncoded(qrRenderedPNG)))
    }

    private enum class Types {
        Rounded, Squares, RoundedSquares
    }
}