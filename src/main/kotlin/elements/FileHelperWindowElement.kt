package elements

import ButtonText
import SimpleDropDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import elements.base.DefaultSplitWindowElement
import model.*

class FileHelperWindowElement : DefaultSplitWindowElement() {

    override val name = "File Helper"

    var filepath by mutableStateOf("")
    var destinationPath by mutableStateOf("")
    var selectedAction by mutableStateOf(Action.List)

    var rootFile: AnyFile? by mutableStateOf(null)
    val flattenDirs = mutableListOf<Directory>()

    var mergeResult by mutableStateOf("")

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        Column {
            OutlinedTextField(
                value = filepath,
                onValueChange = {
                    filepath = it
                    //Notwendig, damit bei einer Änderung des Pfades nichts komisches angezeigt wird.
                    rootFile = null
                    flattenDirs.clear()
                },
                placeholder = { Text("Enter file path") },
                label = { Text("Filepath") }
            )

            if (selectedAction == Action.Merge) {
                OutlinedTextField(
                    value = destinationPath,
                    onValueChange = { destinationPath = it },
                    placeholder = { Text("Enter file path destination for merge") },
                    label = { Text("Filepath destination") }
                )
            }
        }
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        Column {
            Text("Result", fontWeight = FontWeight.Bold)

            when (selectedAction) {
                Action.List -> ListFileView()
                Action.Merge -> MergeView()
            }
        }
    }

    @Composable
    fun MergeView() {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(mergeResult)
        }
    }

    @Composable
    private fun ColumnScope.ListFileView() {

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
                        if (directory.files.isEmpty()) Text(
                            "--Empty--",
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

    var deleteCopiedFilesAndFolders by mutableStateOf(false)
    var logAllCopiedFiles by mutableStateOf(false)
    var ignoreFilesInRoot by mutableStateOf(false)
    var ignoreDuplicateEntries by mutableStateOf(false)
    var ignoreFileEndingsInput by mutableStateOf("")
    var dryRun by mutableStateOf(true)

    override fun getMiddle(): @Composable (ColumnScope.() -> Unit) = {

        Text("Select an action", fontWeight = FontWeight.Bold)
        SimpleDropDown(selectedAction, Action.entries) {
            selectedAction = it
        }

        if (selectedAction == Action.Merge) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = deleteCopiedFilesAndFolders,
                    onCheckedChange = { deleteCopiedFilesAndFolders = it }
                )
                Text("Delete empty folders and copied files")
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = logAllCopiedFiles,
                    onCheckedChange = { logAllCopiedFiles = it }
                )
                Text("Log all copied files? (Errors always logged)")
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = ignoreFilesInRoot,
                    onCheckedChange = { ignoreFilesInRoot = it }
                )
                Text("Ignore files in the execution directory")
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = ignoreDuplicateEntries,
                    onCheckedChange = { ignoreDuplicateEntries = it }
                )
                Text("Ignore duplicate entries (Unchecked will tag duplicates)")
            }

            OutlinedTextField(
                value = ignoreFileEndingsInput,
                onValueChange = { ignoreFileEndingsInput = it },
                placeholder = { Text("Enter file endings to ignore, separated by commas") },
                label = { Text("Ignored file endings") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = dryRun,
                    onCheckedChange = { dryRun = it }
                )
                Text("Run in dry mode (No real actions performed)")
            }
        }


        Spacer(Modifier.weight(1f))

        ButtonText("Start", modifier = Modifier.fillMaxWidth()) {
            when (selectedAction) {
                Action.List -> runList()
                Action.Merge -> runMerge()
            }
        }
    }

    override fun getTopView(): @Composable BoxScope.() -> Unit = {
        val text = when (selectedAction) {
            Action.List -> "Lists all files in the directory you entered below."
            Action.Merge -> """
                Moves all files in the directory you entered below to the destination directory you entered below.
                Imagine this like a flat directory structure. There are no more subfolders afterwards an any file is moved to the first level.
                If the destination is empty, the source directory will be used as the new destination. A new folder will allways be created.
            """.trimIndent()
        }

        Text(text)
    }


}