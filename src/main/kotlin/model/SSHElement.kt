package model

import kotlinx.serialization.Serializable

@Serializable
data class SSHElement(
    val sshName: String,
    val sshPassword: String,
    val sshIP: String,
    val sshPort: Int? = null,
    val displayName: String
) {
    fun buildSSHString() = "ssh${if (sshPort != null) " -p $sshPort " else " "}$sshName@$sshIP"
}