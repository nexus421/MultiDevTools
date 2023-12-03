
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import elements.HashWindowElement
import elements.StringGeneratorWindowElement
import elements.Welcome

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Multi-Dev-Tools") {
        MaterialTheme(colors = darkColors()) {
            Scaffold {
                MainWindow(listOf(Welcome(), HashWindowElement(), StringGeneratorWindowElement()))
            }
        }
    }
}
