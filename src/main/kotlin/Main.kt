
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import elements.HashWindowElement
import elements.Welcome


@Composable
@Preview
fun App() {

}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme(colors = darkColors()) {
            Scaffold {
                MainWindow(listOf(Welcome(), HashWindowElement()))
            }
        }
    }
}
