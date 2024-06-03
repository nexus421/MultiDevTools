package model

import kotlinx.serialization.Serializable

/**
 * Represents an SSH element with required properties.
 *
 * @property sshName The SSH username for authentication.
 * @property sshPassword The SSH password for authentication.
 * @property sshIP The IP address or domain of the SSH server.
 * @property sshPort The port number of the SSH server. Set to `null` for default port.
 * @property displayName The display name of the SSH element.
 */
@Serializable
data class SSHElement(
    val sshName: String,
    val sshPassword: String,
    val sshIP: String,
    val sshPort: Int? = null,
    val displayName: String,
    val additionalInformation: String = ""
) {
    fun buildSSHString() = "ssh${if (sshPort != null) " -p $sshPort " else " "}$sshName@$sshIP"
}