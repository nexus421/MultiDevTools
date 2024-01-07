package elements

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

class Welcome: WindowElement() {
    override val name = "Welcome"

    @Composable
    override fun windowComposable() {
        Text("Hier könnte Ihre Werbung stehen!")
    }

    override fun onStart() {
        println("Started Welcome, yeah!")
    }

    override fun onEnd() {
        println("Ending Welcome, yeah!")
    }


}