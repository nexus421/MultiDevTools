package elements.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import elements.log
import main
import model.LoadingDialogSettings

/**
 * A WindowElement can be displayed and interact with the user.
 * Extend this, if you want to crate your own WindowElement.
 * Add your element to windowElements within the [main] function.
 *
 * Any function is exampled in the "Welcome" screen.
 */
abstract class WindowElement {

    open val availableOS = model.OS.All
    /**
     * The name which will be displayed on the right selection buttons
     */
    abstract val name: String

    /**
     * If set, this will show an AlertDialog with a CircleProgessIndicator.
     * Customizable through [LoadingDialogSettings].
     * Set to null, if it should not be displayed.
     */
    var loading by mutableStateOf<LoadingDialogSettings?>(null)
        private set

    /**
     * Activate a loading dialog. More settings through [LoadingDialogSettings]
     */
    fun showLoading(loadingDialogSettings: LoadingDialogSettings = LoadingDialogSettings()) {
        loading = loadingDialogSettings
    }

    /**
     * Hide/dismiss the current showing loading dialog.
     */
    fun dismissLoading() {
        loading = null
    }

    /**
     * Text, which will automatically be displayed inside an AlertDialog through MainWindow, when this is not blank.
     * Use the methods below to change this var or create your own styled AlertDialog.
     */
    var textToDisplayThroughDialog by mutableStateOf("")
        private set

    /**
     * Set a new text which has to be displayed through the default AlertDialog.
     */
    fun displayDialog(text: String) {
        textToDisplayThroughDialog = text
    }

    /**
     * Call this to hide the default AlertDialog
     */
    fun dismissDialog() {
        textToDisplayThroughDialog = ""
    }

    /**
     * Contains the composable, which will be displayed next to the selections buttons.
     * Override this and add the annotation "@Composable" by yourself!
     */
    @Composable
    abstract fun windowComposable()

    /**
     * Will be called when the users select this window and before [windowComposable] will be rendered.
     */
    open fun onStart() {
        log("Starting $name")
    }

    /**
     * Will be called when the user selects another window (so this goes to the background) or the program will be closed
     * normally.
     */
    open fun onEnd() {
        log("Ending $name")
    }
}