package elements

import ButtonText
import SimpleDropDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    private var filepath by mutableStateOf("/home/nexus/Dokumente/Test")
    private var selectedAction by mutableStateOf(Action.List)

    //ToDo: Das hier muss als eine Liste gespeichert werden, damit wir auf eine LazyColumn setzen können. Wichtig bei vielen Dateien. flatMap nutzen?
    private var rootFile: AnyFile? by mutableStateOf(null)
    private val flattenDirs = mutableListOf<Directory>()

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

        rootFile?.let { rf ->
            if (rf is Directory) {
                LazyColumn {
                    items(flattenDirs) { directory ->
                        Text(
                            "\uD83D\uDCC2 ${directory.name}",
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(start = (32 * directory.depth).dp).fillMaxWidth()
                        )
                        val files = directory.files.filter { it is model.File }
                        if (files.isEmpty()) Text(
                            "Empty",
                            modifier = Modifier.padding(start = (32 * directory.depth + 32).dp).fillMaxWidth()
                        ) else {
                            files.forEach {
                                Text(
                                    "\uD83D\uDDCE ${it.name}",
                                    modifier = Modifier.padding(start = (32 * directory.depth + 32).dp).fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            } else {
                Text("\uD83D\uDDCE ${rf.name}")
            }
        }


    }

    fun flattenRootFile(dir: Directory, depth: Int = 0): List<Directory> {
        val otherDirectories = dir.files.filter { it is Directory }.map {
            flattenRootFile(it as Directory, depth + 1)
        }

        return listOf(dir) + otherDirectories.flatten()
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
                rootFile = null
                flattenDirs.clear()
                rootFile = if (file.isDirectory) {
                    Directory(file).also {
                        flattenDirs.addAll(flattenRootFile(it))
                    }
                } else model.File(file)
                selectedAction = selected
                dismissLoading()
            }
        }
    }

    override fun getTopView(): @Composable BoxScope.() -> Unit = {

    }


}