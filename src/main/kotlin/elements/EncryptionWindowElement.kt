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

//ToDo: IV muss übernommen werden, wenn einer vorhanden ist! Nicht einfach immer nen neuen generieren!
//ToDo: AES/ECB/PKCS5Padding noch anbieten.
class EncryptionWindowElement : DefaultSplitWindowElement() {

    override val name = "Encryption"

    var plainTextInput by mutableStateOf("")
    var encryptedInput by mutableStateOf("")

    var selectedAlgorithm by mutableStateOf(EncryptionAlgorithm.Blowfish)
    var expandDropDown by mutableStateOf(false)
    var compress by mutableStateOf(false)

    private val onError: (Throwable) -> Unit = {
        displayDialog(it.stackTraceToString())
    }

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

        when (selectedAlgorithm) {
            EncryptionAlgorithm.Blowfish -> BlowfishSettings()
            EncryptionAlgorithm.AES_CBC -> AesSettings()
            EncryptionAlgorithm.AES_EBC -> AesEBCSettings()
        }

    }

    @Composable
    private fun EncryptionButton(onClick: () -> Unit) {
        Button(onClick = onClick) {
            Text("Encrypt", modifier = Modifier.weight(1f))
            Text("\uD83D\uDD12")
        }
    }

    @Composable
    private fun DecryptionButton(onClick: () -> Unit) {
        Button(onClick = onClick) {
            Text("Decrypt", modifier = Modifier.weight(1f))
            Text("\uD83D\uDD13")
        }
    }

    @Composable
    private fun BlowfishSettings() {
        var passwordInput by rememberIt("")

        OutlinedTextField(passwordInput, { passwordInput = it }, label = { Text("Password") })
        EncryptionButton {
            if (plainTextInput.isBlank() || passwordInput.isBlank()) return@EncryptionButton postWarningMessage("Empty inputs")

            encryptedInput = plainTextInput.encryptWithBlowfish(passwordInput, compress) ?: run {
                postErrorMessage("Error encrypting text. Check your input.")
                ""
            }
        }
        DecryptionButton {
            if (encryptedInput.isBlank() || passwordInput.isBlank()) return@DecryptionButton postWarningMessage("Empty inputs")
            plainTextInput = encryptedInput.decryptWithBlowfish(passwordInput, compress) ?: run {
                postErrorMessage("Error decrypting text. Check your input.")
                ""
            }
        }
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

        EncryptionButton {
            if (plainTextInput.isBlank() || passwordInput.isBlank()) return@EncryptionButton postWarningMessage("Empty inputs")

            val saltToUser = if (checkSaltGeneration) {
                generateSecureRandom(8).apply { salt = joinToString() }
            } else salt.toIntByteArray()

            if (saltToUser.isEmpty()) return@EncryptionButton postWarningMessage("Salt is empty! At least one byte is required.")

            val ivToUse = iv.toIntByteArray()

            val ivParamSpec = if (ivToUse.isEmpty()) getIVSecureRandom()
                ?: return@EncryptionButton postErrorMessage("IV is null! At least one byte is required.")
            else IvParameterSpec(ivToUse)
            val key = generateSecureAesKeyFromPassword(passwordInput, saltToUser)
            plainTextInput.encryptWithAES(key, ivParamSpec, compress, onError)

            val encryption = plainTextInput.encryptWithAES(key, ivParamSpec, compress, onError)
                ?: return@EncryptionButton postErrorMessage("Error encrypting text. Check your input!")
            iv = ivParamSpec.iv.joinToString()
            encryptedInput = encryption
        }
        DecryptionButton {
            if (encryptedInput.isBlank() || passwordInput.isBlank() || iv.isBlank()) return@DecryptionButton postWarningMessage(
                "Empty inputs"
            )
            plainTextInput = encryptedInput.decryptWithAES(
                generateSecureAesKeyFromPassword(passwordInput, salt.toIntByteArray()),
                IvParameterSpec(iv.toIntByteArray()),
                compress
            ) ?: run {
                postErrorMessage("Error decrypting text. Check your inputs! Maybe wrong iv, Salt or password.")
                ""
            }
        }
    }

    @Composable
    private fun AesEBCSettings() {
        var passwordInput by rememberIt("")

        OutlinedTextField(passwordInput, { passwordInput = it }, label = { Text("Password") })
        EncryptionButton {
            if (plainTextInput.isBlank() || passwordInput.isBlank()) return@EncryptionButton postWarningMessage("Empty inputs")
            if (passwordInput.length != 16 && passwordInput.length != 32) return@EncryptionButton postWarningMessage("Password length has to be 16 or 32!")

            encryptedInput = plainTextInput.encryptWithAesAndPassword(passwordInput, compress, onError) ?: run {
                postErrorMessage("Error encrypting text. Check your input.")
                ""
            }
        }
        DecryptionButton {
            if (encryptedInput.isBlank() || passwordInput.isBlank()) return@DecryptionButton postWarningMessage("Empty inputs")
            if (passwordInput.length != 16 && passwordInput.length != 32) return@DecryptionButton postWarningMessage("Password length has to be 16 or 32!")
            plainTextInput = encryptedInput.decryptWithAesAndPassword(passwordInput, compress, onError) ?: run {
                postErrorMessage("Error decrypting text. Check your input.")
                ""
            }
        }
    }

    override fun getTopView(): @Composable BoxScope.() -> Unit = {
        Column {
            when (selectedAlgorithm) {
                EncryptionAlgorithm.Blowfish -> {
                    Text("Blowfish based encryption", fontWeight = FontWeight.Bold)
                    Text("Simple password based encryption. Not the securest one but good enough for some medium important cases.")
                }

                EncryptionAlgorithm.AES_CBC -> {
                    Text("AES encryption with AES/CBC/PKCS5Padding", fontWeight = FontWeight.Bold)
                    Text("This is currently the most secure implemented encryption!")
                    Divider()
                    Text("Hint salt: This is displayed as Bytearray. This should look like \"-1, 23, 100, -4\". 8 (Byte) should be finde.")
                    Text("Hint iv: This is displayed as Bytearray. This should look like \"-1, 23, 100, -4\". At encryption a random iv will be created, used and displayed. Use that iv for decryption.")
                }

                EncryptionAlgorithm.AES_EBC -> {
                    Text("AES encryption with AES/EBC/PKCS5Padding", fontWeight = FontWeight.Bold)
                    Text("Simple password based encryption. Not the securest one but good enough for some medium important cases.")
                }
            }
            Divider()
            Text("Use compression to compress large texts before encryption. This will provide a smaller encrypted result. Will only work on very large texts due to overhead!")
        }
    }

    private fun String.toIntByteArray() = split(",").mapNotNull { it.trim().toIntOrNull()?.toByte() }.toByteArray()
}