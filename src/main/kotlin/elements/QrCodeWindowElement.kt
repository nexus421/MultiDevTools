package elements

import ButtonText
import CheckBoxText
import SplittedWindow
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import qrcode.QRCode
import rememberIt

class QrCodeWindowElement : WindowElement() {
    override val name = "QR-Code generator"

    @Composable
    override fun windowComposable() {

        var qrInput by rememberIt("")
        var qrBitmap by rememberIt<Bitmap?>(null)
        var liveGeneration by rememberIt(false)
        var expandDropDown by rememberIt(false)
        var selectedType by rememberIt(Types.Rounded)
        var hexColor by rememberIt("ffffff")

        SplittedWindow(leftSide = {
            OutlinedTextField(qrInput, modifier = Modifier.fillMaxWidth(), onValueChange = {
                qrInput = it
                if (liveGeneration) {
                    generateQrCode(qrInput, selectedType, hexColor) {
                        qrBitmap = it
                    }
                }
            }, label = { Text("QR-Code data") })
        }, middle = {
            ButtonText("Generate", modifier = Modifier.fillMaxWidth(), onClick = {
                generateQrCode(qrInput, selectedType, hexColor) { qrBitmap = it }
            })
            CheckBoxText("Live generation", liveGeneration) { liveGeneration = it }

//            OutlinedTextField(hexColor, modifier = Modifier.fillMaxWidth(), onValueChange = {
//                hexColor = it
//                if (liveGeneration) {
//                    generateQrCode(qrInput, selectedType, hexColor) {
//                        qrBitmap = it
//                    }
//                }
//            }, label = { Text("QR-Code hex-color") })

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

            Spacer(Modifier.weight(1f))
            Text("Based on https://github.com/g0dkar/qrcode-kotlin", fontSize = 10.sp)
        }, rightSide = {
            qrBitmap?.let {
                Image(it.asComposeImageBitmap(), null)
            }
        })
    }

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