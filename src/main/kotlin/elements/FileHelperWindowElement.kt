package elements

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
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
import java.io.File

class FileHelperWindowElement : DefaultSplitWindowElement() {

    override val name = "File Helper"

    private var filepath by mutableStateOf("")
    private var selectedAction by mutableStateOf(Action.List)

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        Column {
            OutlinedTextField(
                value = filepath,
                onValueChange = { filepath = it },
                placeholder = { Text("Enter file path") },
                label = { Text("Filepath") }
            )

            ListFileView()
        }
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        Column {
            Text("Result", fontWeight = FontWeight.Bold)

            when (selectedAction) {
                Action.List -> ListFileView()
                Action.Delete -> TODO()
                Action.Merge -> TODO()
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
        Text("D ${folder.name}", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = padding))
        folder.listFiles()?.forEach {
            if (it.isDirectory) DisplayFolder(it, padding + 32.dp)
            else Text("F ${it.name}", modifier = Modifier.padding(start = padding + 32.dp))
        } ?: Text("Empty")
    }

    override fun getMiddle(): @Composable (ColumnScope.() -> Unit) = {
//        Button({
//            val selectedFile = FileDialog(ComposeWindow(), "Select a folder").apply {
//                isVisible = true
//            }.file?.let { File(it) }
//            if(selectedFile?.isDirectory != true) postErrorMessage("Please select a directory")
//            else filepath = selectedFile.absolutePath
//        }) {
//            Text("Select Folder")
//        }

        Text("Select an action", fontWeight = FontWeight.Bold)
        Button(onClick = {
            selectedAction = Action.List
        }) {
            Text("List all files")
        }
        Button(onClick = {
            selectedAction = Action.Merge
        }) {
            Text("Merge files")
        }
    }

    override fun getTopView(): @Composable BoxScope.() -> Unit = {

    }

    enum class Action {
        //List all files and subfiles / folder
        List,

        //Delete all files/folders recursively
        Delete,

        //Merge all files to a given path to eliminate subfolders
        Merge,

        //Uses FFMPEG to change a video container
        ChangeContainer
    }

}