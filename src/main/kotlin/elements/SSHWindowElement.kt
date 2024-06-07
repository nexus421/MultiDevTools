package elements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coroutine
import elements.base.DefaultSplitWindowElement
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotnexlib.copyToClipboard
import kotnexlib.decryptWithBlowfish
import kotnexlib.encryptWithBlowfish
import kotnexlib.tryOrNull
import model.OS
import model.SSHElement
import utils.SSHUtils
import java.io.File

/**
 * Represents a window element for managing SSH connections.
 *
 * This class extends the DefaultSplitWindowElement class and provides functionalities for storing and
 * retrieving SSH connections. It uses encryption to secure the stored data.
 *
 * @property availableOS The supported operating system
 * @property name The name of the SSH client
 * @property sshFile The file used to store the encrypted SSH data
 * @property password The password used for encryption and decryption
 * @property json The JSON object used for serialization and deserialization
 * @property sshElements The list of SSH elements
 */
class SSHWindowElement : DefaultSplitWindowElement() {

    override val availableOS = OS.Linux
    override val name = "SSH Client"
    private val sshFile = File(Files.baseFolder.baseFolder, "ssh.config")
    private var password by mutableStateOf("")
    private val json = Json

    override fun onStart() {
        super.onStart()
        if (sshFile.exists().not()) sshFile.createNewFile()
        loadAllCurrentElements()
    }

    private val sshElements = mutableStateListOf<SSHElement>()

    private var connectionName by mutableStateOf("")
    private var sshName by mutableStateOf("")
    private var sshPassword by mutableStateOf("")
    private var sshIp by mutableStateOf("")
    private var sshPort by mutableStateOf("")
    private var additionalInformation by mutableStateOf("")

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        if (password.isBlank().not()) {
            Column {
                TextField(
                    value = connectionName,
                    onValueChange = { connectionName = it },
                    label = { Text("Name this SSH connection") })
                TextField(
                    value = sshName,
                    onValueChange = { sshName = it },
                    label = { Text("Enter SSH username") }
                )
                TextField(
                    value = sshPassword,
                    onValueChange = { sshPassword = it },
                    label = { Text("Enter SSH password") }
                )
                TextField(
                    value = sshIp,
                    onValueChange = { sshIp = it },
                    label = { Text("Enter SSH IP/Domain") }
                )
                TextField(
                    value = sshPort,
                    onValueChange = { sshPort = it },
                    label = { Text("Enter SSH port or empty for default") }
                )

                TextField(
                    value = additionalInformation,
                    onValueChange = { additionalInformation = it },
                    label = { Text("Additional information (optional)") }
                )
                Button({
                    sshElements.add(
                        SSHElement(
                            sshName,
                            sshPassword,
                            sshIp,
                            sshPort.toIntOrNull(),
                            connectionName,
                            additionalInformation
                        )
                    )
                    storeAllCurrentElements()

                    connectionName = ""
                    sshName = ""
                    sshIp = ""
                    sshPassword = ""
                    sshPort = ""
                    additionalInformation = ""
                }) {
                    Text("Save SSH connection")
                }
            }
        } else {
            Text("Password required")
        }
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {

        if (password.isBlank()) {
            var localPassword by remember { mutableStateOf(password) }
            Column {
                TextField(
                    value = localPassword,
                    onValueChange = { localPassword = it.trim() },
                    label = { Text("Password required") },
                    modifier = Modifier.onKeyEvent {
                        if (it.key == Key.Enter) {
                            if (localPassword.length < 4) {
                                postErrorMessage("Password length must be at least 4 characters long.")
                                return@onKeyEvent false
                            }
                            password = localPassword
                            loadAllCurrentElements()
                        }
                        true
                    })
                Button({
                    if (localPassword.length < 4) return@Button postErrorMessage("Password length must be at least 4 characters long.")
                    password = localPassword
                    loadAllCurrentElements()
                }) {
                    Text("Decrypt")
                }
            }
        } else {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Row {
                    Text(text = "Name", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text(text = "Username", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text(text = "IP/Domain", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                    Text(text = "Port", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
                }
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                sshElements.forEach { sshElement ->
                    Row {
                        Text(
                            text = sshElement.displayName,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = sshElement.sshName, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Text(text = sshElement.sshIP, modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                        Text(
                            text = (sshElement.sshPort ?: 22).toString(),
                            modifier = Modifier.weight(0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(sshElement.buildSSHString())
                    Spacer(Modifier.height(8.dp))
                    if (sshElement.additionalInformation.isNotBlank()) {
                        Text(
                            sshElement.additionalInformation,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Thin
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Button({
                            connectionName = sshElement.displayName
                            sshName = sshElement.sshName
                            sshIp = sshElement.sshIP
                            sshPassword = sshElement.sshPassword
                            sshPort = sshElement.sshPort?.toString() ?: ""
                            additionalInformation = sshElement.additionalInformation
                        }) {
                            Icon(Icons.Default.Edit, null)
                        }
                        Spacer(Modifier.width(16.dp))

                        var delete by remember { mutableStateOf(false) }
                        Button({
                            if (delete.not()) {
                                postMessage("Click again to delete this entry.")
                                coroutine.launch {
                                    delay(2000)
                                    delete = false
                                }
                                delete = true
                            } else {
                                sshElements.remove(sshElement)
                                storeAllCurrentElements()
                            }
                        }) {
                            Icon(Icons.Default.Delete, null, tint = if (delete) Color.Red else Color.Unspecified)
                        }

                        Spacer(Modifier.weight(1f))

                        Button({
                            sshElement.sshPassword.copyToClipboard()
                            postMessage("Password copied to clipboard")
                            SSHUtils.openKonsole(sshElement.buildSSHString())
                        }) {
                            Text("Connect", fontWeight = FontWeight.Bold)
                        }
                    }
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    override fun getMiddle(): @Composable (ColumnScope.() -> Unit)? = null

    override fun getTopView(): @Composable (BoxScope.() -> Unit) = {
        Column {
            Text("This is a simple SSH password storage.", fontWeight = FontWeight.Bold)
            Text(
                """
            Enter your SSH data. This will all be stored and encrypted with the given password.
            After click on "connect", a terminal will be opened with the ssh command executed and the password will
            be copied to the clipboard. You only need to paste the password to the terminal an click enter.
            Never forget access data for your ssh servers.
            
            Hint: This will use the "konsole"-programm installed with KDE.
            Hint: The password will only be used after the first item was added.
        """.trimIndent()
            )
        }
    }


    /**
     * Stores all the current elements by encoding the SSH elements list to JSON,
     * encrypting the JSON with Blowfish using the provided password,
     * and writing the encrypted JSON to the SSH file.
     * If there is an error encrypting the data, it displays an error message and returns.
     */
    private fun storeAllCurrentElements() {
        val plainJson = json.encodeToString(sshElements.toList())
        val encryptedJson =
            plainJson.encryptWithBlowfish(password, true) ?: return postErrorMessage("Error encrypting data")
        sshFile.writeText(encryptedJson)
    }

    /**
     * Loads all the current elements by decrypting the encrypted JSON file using the provided password.
     * If the password is blank, the method returns without performing any further action.
     * If the encrypted JSON file is empty, it displays a message "No data found" and returns.
     * If there is an error in decrypting the data, it sets the password to blank and displays an error message "Error decrypting data. Wrong password or corrupted file.".
     * If the decryption is successful, it clears the existing SSH elements and adds the decrypted elements to the collection.
     */
    private fun loadAllCurrentElements() {
        if (password.isBlank()) return
        val encryptedJson = sshFile.readText()
        if (encryptedJson.isEmpty()) return postMessage("No data found")
        val decryptedJson = encryptedJson.decryptWithBlowfish(password, true) ?: run {
            password = ""
            return postErrorMessage("Error decrypting data. Wrong password or corrupted file.")
        }

        tryOrNull { json.decodeFromString<List<SSHElement>>(decryptedJson) }?.let {
            sshElements.clear()
            sshElements.addAll(it)
        }
    }

}