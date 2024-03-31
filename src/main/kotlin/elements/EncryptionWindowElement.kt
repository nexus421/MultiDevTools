package elements

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import elements.base.DefaultSplitWindowElement
import kotnexlib.*
import model.EncryptionAlgorithm
import rememberIt
import javax.crypto.spec.IvParameterSpec


class EncryptionWindowElement : DefaultSplitWindowElement() {

    override val name = "Encryption"

    var plainTextInput by mutableStateOf("")
    var encryptedInput by mutableStateOf("")

    var selectedAlgorithm by mutableStateOf(EncryptionAlgorithm.Blowfish)
    var expandDropDown by mutableStateOf(false)
    var compress by mutableStateOf(false)

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        OutlinedTextField(plainTextInput, {
            plainTextInput = it
        }, label = {
            Text("Decrypted text")
        }, modifier = Modifier.fillMaxWidth())
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        OutlinedTextField(encryptedInput, {
            encryptedInput = it
        }, label = {
            Text("Encrypted text")
        }, modifier = Modifier.fillMaxWidth())
    }

    override fun getMiddle(): @Composable ColumnScope.() -> Unit = {

        Box {
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                expandDropDown = expandDropDown.not()
            }) {
                Text(selectedAlgorithm.name, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = "DropDown")
            }
            DropdownMenu(expandDropDown, onDismissRequest = { expandDropDown = false }) {
                EncryptionAlgorithm.entries.forEach {
                    DropdownMenuItem(onClick = {
                        selectedAlgorithm = it
                        expandDropDown = false
                    }) {
                        Text(it.name)
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Compress")
            Spacer(Modifier.weight(1f))
            Switch(compress, { compress = it })
        }

        if (selectedAlgorithm == EncryptionAlgorithm.Blowfish) BlowfishSettings() else AesSettings()

    }

    @Composable
    private fun BlowfishSettings() {
        var passwordInput by rememberIt("")

        OutlinedTextField(passwordInput, { passwordInput = it }, label = { Text("Password") })
        Button({
            if (plainTextInput.isBlank() || passwordInput.isBlank()) return@Button postWarningMessage("Empty inputs")

            encryptedInput = plainTextInput.encryptWithBlowfish(passwordInput, compress) ?: run {
                postErrorMessage("Error encrypting text. Check your input.")
                ""
            }
        }) { Text("Encrypt") }
        Button({
            if (encryptedInput.isBlank() || passwordInput.isBlank()) return@Button postWarningMessage("Empty inputs")
            plainTextInput = encryptedInput.decryptWithBlowfish(passwordInput, compress) ?: run {
                postErrorMessage("Error decrypting text. Check your input.")
                ""
            }
        }) { Text("Decrypt") }
    }

    @Composable
    private fun AesSettings() {
        var passwordInput by rememberIt("")
        var checkSaltGeneration by rememberIt(false)
        var salt by rememberIt("")
        var iv by rememberIt("")

        OutlinedTextField(passwordInput, { passwordInput = it }, label = { Text("Password") })
        OutlinedTextField(salt, { salt = it }, label = { Text("Salt (Bytearray)") })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Use random salt")
            Spacer(Modifier.weight(1f))
            Switch(checkSaltGeneration, { checkSaltGeneration = it })
        }
        OutlinedTextField(iv, { iv = it }, label = { Text("iv (Bytearray)") })

        Button({
            if (plainTextInput.isBlank() || passwordInput.isBlank()) return@Button postWarningMessage("Empty inputs")

            val saltToUser = if (checkSaltGeneration) {
                generateSecureRandom(8).apply { salt = joinToString() }
            } else salt.toIntByteArray()

            if (saltToUser.isEmpty()) return@Button postWarningMessage("Salt is empty! At least one byte is required.")

            val encryption = plainTextInput.encryptWithAesAndPasswordHelper(passwordInput, saltToUser, compress)
                ?: return@Button postErrorMessage("Error encrypting text. Check your input!")
            iv = encryption.ivParameterSpec.iv.joinToString()
            encryptedInput = encryption.encryptedText
        }) { Text("Encrypt") }
        Button({
            if (encryptedInput.isBlank() || passwordInput.isBlank() || iv.isBlank()) return@Button postWarningMessage("Empty inputs")
            plainTextInput = encryptedInput.decryptWithAES(
                generateSecureAesKeyFromPassword(passwordInput, salt.toIntByteArray()),
                IvParameterSpec(iv.toIntByteArray()),
                compress
            ) ?: run {
                postErrorMessage("Error decrypting text. Check your inputs! Maybe wrong iv, Salt or password.")
                ""
            }
        }) { Text("Decrypt") }
    }

    override fun getTopView(): @Composable BoxScope.() -> Unit = {
        if (selectedAlgorithm == EncryptionAlgorithm.AES) {
            Column {
                Text("AES encryption with AES/CBC/PKCS5Padding", fontWeight = FontWeight.Bold)
                Divider()
                Text("Hint salt: This is displayed as Bytearray. This should look like \"-1, 23, 100, -4\". 8 (Byte) should be finde.")
                Text("Hint iv: This is displayed as Bytearray. This should look like \"-1, 23, 100, -4\". At encryption a random iv will be created, used and displayed. Use that iv for decryption.")
            }
        }
    }

    private fun String.toIntByteArray() = split(",").mapNotNull { it.trim().toIntOrNull()?.toByte() }.toByteArray()
}