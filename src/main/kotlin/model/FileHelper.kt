package model

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