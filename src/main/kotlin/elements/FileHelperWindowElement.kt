package elements

import ButtonText
import SimpleDropDown
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import elements.base.DefaultSplitWindowElement
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotnexlib.tryOrNull
import model.Action
import model.AnyFile
import model.Directory
import rememberIt
import java.io.File

class FileHelperWindowElement : DefaultSplitWindowElement() {

    override val name = "File Helper"

    private var filepath by mutableStateOf("")
    private var selectedAction by mutableStateOf(Action.List)

    //ToDo: Das hier muss als eine Liste gespeichert werden, damit wir auf eine LazyColumn setzen können. Wichtig bei vielen Dateien. flatMap nutzen?
    private var rootFile: AnyFile? = null

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        Column {
            OutlinedTextField(
                value = filepath,
                onValueChange = { filepath = it },
                placeholder = { Text("Enter file path") },
                label = { Text("Filepath") }
            )
        }
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        Column {
            Text("Result", fontWeight = FontWeight.Bold)

            when (selectedAction) {
                Action.List -> ListFileView()
                Action.Delete -> Text("Not yet implemented")
                Action.Merge -> Text("Not yet implemented")
                Action.ChangeContainer -> ContainerChangeView()
            }
        }
    }

    @Composable
    fun ContainerChangeView() {

    }

    @Composable
    private fun ColumnScope.ListFileView() {
        val file = File(filepath)
        if (filepath.length < 5 || file.exists().not()) return

        Column(Modifier.verticalScroll(rememberScrollState())) {
            DisplayFolder(file)
        }
    }

    @Composable
    private fun ColumnScope.DisplayFolder(folder: File, padding: Dp = 0.dp) {
        Text(
            "\uD83D\uDCC2 ${folder.name}",
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = padding)
        )
        folder.listFiles()?.forEach {
            if (it.isDirectory) DisplayFolder(it, padding + 32.dp)
            else Text("\uD83D\uDDCE ${it.name}", modifier = Modifier.padding(start = padding + 32.dp))
        } ?: Text("Empty")
    }

    override fun getMiddle(): @Composable (ColumnScope.() -> Unit) = {
        var selected by rememberIt(selectedAction)

        Text("Select an action", fontWeight = FontWeight.Bold)
        SimpleDropDown(selected, Action.entries) {
            selected = it
        }

        ButtonText("Start") {
            if (filepath.isBlank()) return@ButtonText postErrorMessage("Filepath can't be empty")
            val file = tryOrNull { File(filepath) } ?: return@ButtonText postErrorMessage("Invalid file path")
            showLoading()
            GlobalScope.launch {
                rootFile = if (file.isDirectory) Directory(file) else model.File(file)
                selectedAction = selected
                dismissLoading()
            }
        }
    }

    override fun getTopView(): @Composable BoxScope.() -> Unit = {

    }



}