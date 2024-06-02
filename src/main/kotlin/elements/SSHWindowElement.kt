package elements

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.*
import elements.base.DefaultSplitWindowElement
import model.OS
import model.SSHElement

class SSHWindowElement : DefaultSplitWindowElement() {

    override val availableOS = OS.Linux
    override val name = "SSH Client"

    private val sshElements = mutableStateListOf<SSHElement>()

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        Column {
            var sshName by mutableStateOf("")
            var sshPassword by mutableStateOf("")
            var sshIp by mutableStateOf("")
            var sshPort by mutableStateOf("")

            //ToDo: die vier Parameter abfragen und als SSHElement speichern
        }
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        Column {
            sshElements.forEach {
                //SSH-Verbdinungen anzeigen
                //Bei klick auf BUtton, Terminal mit Verbindung öffnen und PW in Zwischenablage kopieren.
                // Dann muss man nur noch einfügen und enter klicken.
            }
        }
    }

    override fun getMiddle(): @Composable (ColumnScope.() -> Unit)? = null

    override fun getTopView(): @Composable (BoxScope.() -> Unit) = {

    }

}