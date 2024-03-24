package elements

import SplittedWindow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotnexlib.HashAlgorithm
import kotnexlib.copyToClipboard
import kotnexlib.hash
import rememberIt

class HashWindowElement : WindowElement() {
    override val name = "Hash"

    @Composable
    override fun windowComposable() {
        var rawInput by rememberIt("")
        var hashedInput by rememberIt("")
        var selectedHashAlgorithm by rememberIt(HashAlgorithm.MD5)
        var expandDropDown by rememberIt(false)
        var checkRealtime by rememberIt(false)
        var checkClipboard by rememberIt(true)

        SplittedWindow(leftSide = {
            TextField(rawInput, placeholder = {
                Text("String to hash")
            }, modifier = Modifier.fillMaxSize(), onValueChange = {
                rawInput = it
                if (checkRealtime) hashedInput = rawInput.hash(selectedHashAlgorithm)
            })
        }, middle = {

            Box {
                OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                    expandDropDown = expandDropDown.not()
                }) {
                    Text(selectedHashAlgorithm.algorithm, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "DropDown")
                }
                DropdownMenu(expandDropDown, onDismissRequest = { expandDropDown = false }) {
                    HashAlgorithm.entries.forEach {
                        DropdownMenuItem(onClick = {
                            selectedHashAlgorithm = it
                            expandDropDown = false
                        }) {
                            Text(it.algorithm)
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Realtime", modifier = Modifier.weight(1f))
                Checkbox(checkRealtime, onCheckedChange = {
                    checkRealtime = checkRealtime.not()
                })
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Copy to clipboard", modifier = Modifier.weight(1f))
                Checkbox(checkClipboard, onCheckedChange = {
                    checkClipboard = checkClipboard.not()
                })
            }

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                hashedInput = rawInput.hash(selectedHashAlgorithm)
                if (checkClipboard) hashedInput.copyToClipboard()
            }) {
                Text("Hash".plus(if (checkClipboard) " and copy" else ""))
            }
        }, rightSide = {
            SelectionContainer {
                Text(hashedInput)
            }
        })
    }
}