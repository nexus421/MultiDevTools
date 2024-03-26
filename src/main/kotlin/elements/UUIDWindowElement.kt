package elements

import ButtonText
import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import elements.base.WindowElement
import kotnexlib.copyToClipboard
import rememberIt
import java.util.*

class UUIDWindowElement : WindowElement() {
    override val name = "UUID"

    @Composable
    override fun windowComposable() {
        var uuid by rememberIt("")
        Column {
            OutlinedTextField(uuid, onValueChange = { uuid = it }, label = {
                Text("UUID")
            })
            ButtonText("Generate random UUID") {
                uuid = UUID.randomUUID().toString()
                uuid.copyToClipboard()
            }
            Text("Will automatically be copied to clipboard", fontSize = 11.sp)
        }
    }
}