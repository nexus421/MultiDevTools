package utils

import java.io.File

object SSHUtils {

    fun openSSH() {
        ProcessBuilder().apply {
            command("konsole", "-e", "ssh -p 421 ssh@ssh.kickner.bayern")
            directory(File(System.getProperty("user.home")))
        }.start()
    }

}