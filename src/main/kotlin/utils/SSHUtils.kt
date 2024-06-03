package utils

import java.io.File

object SSHUtils {

    fun openKonsole(command: String) {
        ProcessBuilder().apply {
            command("konsole", "-e", command)
            directory(File(System.getProperty("user.home")))
        }.start()
    }

}