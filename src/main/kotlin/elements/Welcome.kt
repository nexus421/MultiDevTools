package elements

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

class Welcome: WindowElement() {
    override val name = "Willkommen"

    @Composable
    override fun windowComposable() {
        Text("Hier könnte Ihre Werbung stehen")
    }

}