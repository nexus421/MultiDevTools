package elements.base

import DynamicSplitWindow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coroutine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.*

/**
 * Use this one to create a window element with some extras.
 * It is recommended to use this view, so every [WindowElement] will look the same!
 *
 * Provides a cleaner structure to use [DynamicSplitWindow].
 * Provides a simple mechanism to post messages (like Snackbar in Android) through [postMessage].
 */
abstract class DefaultSplitWindowElement : WindowElement() {

    private var messageToPost by mutableStateOf<String?>(null)
    private var messageColor = blue
    private var currentRunningMessage: Job? = null

    /**
     * Handle the composable for the left side inside [DynamicSplitWindow].
     * IMPORTANT: You have to manually add @Composable after overriding this one!
     * override fun getLeftSide(): @Composable BoxScope.() -> Unit = {...}
     */
    abstract fun getLeftSide(): @Composable BoxScope.() -> Unit

    /**
     * Handle the composable for the right side inside [DynamicSplitWindow].
     * IMPORTANT: You have to manually add @Composable after overriding this one!
     * override fun getRightSide(): @Composable BoxScope.() -> Unit = {...}
     */
    abstract fun getRightSide(): @Composable BoxScope.() -> Unit

    /**
     * Handle the composable for the middle inside [DynamicSplitWindow].
     * IMPORTANT: You have to manually add @Composable after overriding this one!
     * override fun getMiddle(): @Composable ColumnScope.() -> Unit = {...}
     *
     * Override with null, if you don't want this view!
     */
    abstract fun getMiddle(): @Composable (ColumnScope.() -> Unit)?

    /**
     * Handle the composable for the top view inside [DynamicSplitWindow].
     * IMPORTANT: You have to manually add @Composable after overriding this one!
     * override fun getTopView(): @Composable BoxScope.() -> Unit = {...}
     *
     * Override with null, if you don't want this view!
     */
    abstract fun getTopView(): @Composable (BoxScope.() -> Unit)?

    /**
     * Post a temporary message at the bottom of the screen inside a colored Box.
     *
     * @param msg Message to display to the user
     * @param timeMillis time in milliseconds the message will be displayed
     * @param color Color of the box around the text. Defaults to [blue]
     */
    fun postMessage(msg: String, timeMillis: Long = 2000, color: Color = blue) {
        //If you post a new message while another message is shown, it will be killed and replaced by the new message through this.
        if (currentRunningMessage?.isActive == true) {
            messageToPost = null
            currentRunningMessage?.cancel()
        }

        currentRunningMessage = coroutine.launch {
            messageToPost = msg
            messageColor = color
            delay(timeMillis)
            messageToPost = null
            currentRunningMessage = null
        }
    }

    /**
     * See [postMessage].
     * Color is fixed to [green]
     */
    fun postSuccessMessage(msg: String, timeMillis: Long = 3000) = postMessage(msg, timeMillis, green)

    /**
     * See [postMessage].
     * Color is fixed to [orange]
     */
    fun postWarningMessage(msg: String, timeMillis: Long = 3000) = postMessage(msg, timeMillis, orange)

    /**
     * See [postMessage].
     * Color is fixed to [red]
     */
    fun postErrorMessage(msg: String, timeMillis: Long = 3000) = postMessage(msg, timeMillis, red)

    @Composable
    override fun windowComposable() {
        Column {
            DynamicSplitWindow(
                leftSide = getLeftSide(),
                rightSide = getRightSide(),
                middle = getMiddle(),
                topView = getTopView()
            )

            messageToPost?.let {
                Box(
                    Modifier.padding(top = 2.dp).border(4.dp, messageColor, shape)
                        .background(Color.DarkGray, shape).padding(4.dp)
                ) {
                    Text(it, Modifier.padding(4.dp).fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }
        }
    }

}