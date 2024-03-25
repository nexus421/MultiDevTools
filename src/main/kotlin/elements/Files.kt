package elements

import file.BaseFolder
import file.LogFile

object Files {
    val baseFolder = BaseFolder(name = "MultiDevTools")
    val logFile = LogFile(baseFolder = baseFolder)

}

fun log(msg: String, t: Throwable? = null, printToStdout: Boolean = true) =
    Files.logFile.writeLog(msg, t, printToStdout)