import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import elements.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val coroutine = CoroutineScope(Dispatchers.Default)

fun main() {

    if (false) return doTest()

    //Should not be changed after MultiDevTools started
    //The first Element here will be displayed first
    val windowElements = listOf(
        Welcome(),
        HashWindowElement(),
        BruteForceWindowElement(),
        StringGeneratorWindowElement(),
        ServerWindowElement(),
        Base64WindowElement(),
        TimeWindowElement(),
        StringCaseWindowElement(),
        QrCodeWindowElement(),
        UUIDWindowElement(),
        RegexWindowElement(),
        NumConverterWindowElement(),
        PermutationsWindowElement()
    )

    var onClose by mutableStateOf(false)

    application {
        Window(
            onCloseRequest = {
                coroutine.launch {
                    onClose = true
                    selectedElement?.onEnd()
                    exitApplication()
                }
            },
            title = "Multi-Dev-Tools",
            state = rememberWindowState(width = 1280.dp, height = 800.dp)
        ) {
            MaterialTheme(colors = darkColors()) {
                Scaffold {
                    MainWindow(windowElements)
                    onClose.ifTrue { SimpleLoadingDialog("Finishing current window. Please wait...") }
                }
            }
        }
    }
}

fun doTest() {
    val search = "asdf".hash()
    charArrayOf('a', 'd', 'f', 's').createPermutations(9, onEachGeneration = {
        if (it.hash() == search) {
            println("Gefunden!")
            true
        } else false
    }).let {
        println(it.result.joinToString("\n"))
        println("Time: ${it.timeMillis}ms for ${it.result.size} elements.")
    }

//    val result = charArrayOf('a', 'p', 'w', 'q', 'b').createPermutationsMulti()

//    println(result)

}
