package elements

import androidx.compose.runtime.Composable
import main

/**
 * A WindowElement can be displayed and interact with the user.
 * Extend this, if you want to crate your own WindowElement.
 * Add your element to windowElements within the [main] function.
 */
sealed class WindowElement {
    /**
     * The name which will be displayed on the right selection buttons
     */
    abstract val name: String

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
        println("Starting $name")
    }

    /**
     * Will be called when the user selects another window (so this goes to the background) or the program will be closed
     * normally.
     */
    open fun onEnd() {
        println("Ending $name")
    }
}