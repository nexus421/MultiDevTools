package elements

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

sealed class WindowElement {
    abstract val name: String

    @Composable
    abstract fun windowComposable()
}