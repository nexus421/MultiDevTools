import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import elements.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

val coroutine = CoroutineScope(Dispatchers.Default)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Multi-Dev-Tools",
        state = rememberWindowState(width = 1280.dp, height = 800.dp)
    ) {
        MaterialTheme(colors = darkColors()) {
            Scaffold {
                MainWindow(
                    listOf(
                        Welcome(),
                        HashWindowElement(),
                        StringGeneratorWindowElement(),
                        ServerWindowElement(),
                        Base64WindowElement(),
                        TimeWindowElement()
                    )
                )
            }
        }
    }
}
