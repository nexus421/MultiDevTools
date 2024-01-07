package elements

import ButtonText
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import model.LoadingDialogSettings

class Welcome: WindowElement() {
    override val name = "Welcome"

    @Composable
    override fun windowComposable() {
        Column {
            Text("Hier könnte Ihre Werbung stehen!")
            ButtonText("Click me!") {
                displayDialog("This is an example dialog. This can show you informations or errors.\nClick \"OK\" to close this dialog.")
            }
            ButtonText("Wait! What?") {
                showLoading(LoadingDialogSettings(text = "Wait here for... nothing!") {
                    println("The dialog was canceled through the user.")
                    dismissLoading()
                })
            }
        }
    }

    override fun onStart() {
        println("Started Welcome, yeah!")
    }

    override fun onEnd() {
        println("Ending Welcome, yeah!")
    }


}