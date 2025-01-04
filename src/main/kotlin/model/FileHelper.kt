package model

import elements.FileHelperWindowElement
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotnexlib.tryOrNull
import kotnexlib.withNewLine
import java.io.File
import kotlin.system.measureTimeMillis

enum class Action {
    //List all files and subfiles / folder
    List,

    //Merge all files to a given path to eliminate subfolders
    Merge
}

enum class FileType {
    Directory, File
}

abstract class AnyFile(val type: FileType, val name: String)
data class File(val path: java.io.File) : AnyFile(FileType.File, path.name)
data class Directory(val path: java.io.File, val depth: Int = 0) : AnyFile(FileType.Directory, path.name) {
    val files: List<AnyFile> = path.listFiles().map {
        if (it.isDirectory) Directory(it, depth + 1) else File(it)
    }
}

fun FileHelperWindowElement.runList() {
    if (filepath.isBlank()) return postErrorMessage("Filepath can't be empty")
    val file = tryOrNull { File(filepath) } ?: return postErrorMessage("Invalid file path")
    if (file.exists().not()) return postErrorMessage("Path does not exist")

    showLoading()
    GlobalScope.launch {
        rootFile = null
        flattenDirs.clear()
        rootFile = if (file.isDirectory) {
            Directory(file).also {
                flattenDirs.addAll(flattenRootFile(it))
            }
        } else File(file)
        selectedAction = Action.List
        dismissLoading()
    }
}

fun FileHelperWindowElement.runMerge() {
    count = 0
    error = 0
    mergeResult = "[[$count]]\n"
    val path = if (filepath.isBlank()) {
        println("Using default. Current Directory.\n")
        ".${File.separator}"
    } else {
        println("Using entered path: $filepath\n")
        if (File(filepath).exists().not()) {
            postErrorMessage("Path does not exist.")
            mergeResult = "Path does not exist."
            return
        }
        filepath
    }
    if (dryRun) mergeResult += "Dry run enabled. No files will be moved or deleted.\n"

    val rootFolder = File(path)
    val files = rootFolder.listFiles()?.let {
        if (ignoreFilesInRoot) it.filter { file -> file.isDirectory }.toTypedArray()
        else it
    } ?: emptyArray()

    val destination = if (destinationPath.isBlank() || File(destinationPath).exists().not()) {
        mergeResult += "Destination path does not exist. Using same path as source path.\n"
        filepath
    } else destinationPath

    val newPlace = File(destination, "MergedResult").also {
        if (it.exists()) {
            it.deleteRecursively()
            mergeResult += "Deleted old merged result folder and created new one.\n"
        }
        it.mkdir()
    }

    mergeResult += "\"The result will be stored at ${newPlace.absolutePath}\".\n"
    mergeResult += "\nThis operation may take a while. Please be patient. Starting...\n\n"
    GlobalScope.launch {
        showLoading()
        val time = measureTimeMillis {
            copyFilesToMerged(
                files,
                newPlace,
                deleteCopiedFilesAndFolders,
                logAllCopiedFiles,
                dryRun,
                ignoreDuplicateEntries,
                ignoreFileEndingsInput.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() })
        }
        if (dryRun) mergeResult += ("\nExecuted as dryRun. No files were really copied or deleted!\n")
        ("Copied $count files in $time ms with $error errors").also {
            mergeResult += it.withNewLine()
            postSuccessMessage(it)
        }
        dismissLoading()
    }
}

private var count = 0
private var error = 0

private fun FileHelperWindowElement.copyFilesToMerged(
    files: Array<File>,
    newPlace: File,
    delete: Boolean,
    logCopies: Boolean,
    dryRun: Boolean,
    ignoreDuplicateEntries: Boolean,
    ignoreFileEndings: List<String>
) {
    files.forEach { file ->
        try {

            if (file.isDirectory) {
                file.listFiles()?.let {
                    copyFilesToMerged(
                        it,
                        newPlace,
                        delete,
                        logCopies,
                        dryRun,
                        ignoreDuplicateEntries,
                        ignoreFileEndings
                    )
                }
                if (dryRun.not() && delete && file.listFiles()?.isEmpty() == true) file.deleteRecursively()
            } else if (file.isFile) {
                if (ignoreFile(file, ignoreFileEndings)) return@forEach

                val newDestination = File(newPlace, file.name).let {
                    if (it.exists()) {
                        if (ignoreDuplicateEntries) {
                            mergeResult += ("Ignoring duplicated file from ${file.absolutePath}\n")
                            it
                        } else File(newPlace, "duplicate_${System.nanoTime()}_" + file.name)
                    } else it
                }

                //Kopiert die Datei an das neue Ziel
                if (dryRun.not()) file.copyTo(newDestination)
                count++
                mergeResult = mergeResult.replaceBefore("]]", "[[$count")

                //Löscht die Datei, falls Flag aktiv
                if (dryRun.not() && delete) file.delete()

                //Info-Log, falls aktiviert
                if (logCopies) mergeResult += ("#${count} Copy file to new place (${newDestination.name}). ${if (delete) "Deleted file." else ""}\n")
            }
        } catch (e: Exception) {
            postErrorMessage("Error: ${e.message}")
            error++
        }
    }
}

/**
 * Checks if the given file ends with any of the specified file endings.
 *
 * @param file The file to be checked.
 * @param ignoreFileEndings The list of file endings to be ignored.
 * @return `true` if the file ends with any of the specified file endings, `false` otherwise.
 */
private fun ignoreFile(file: File, ignoreFileEndings: List<String>) =
    ignoreFileEndings.any { file.name.lowercase().endsWith(it) }