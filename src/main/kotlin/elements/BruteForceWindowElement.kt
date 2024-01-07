package elements

import HashAlgorithm
import SplittedWindow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coroutine
import createPermutations
import createPermutationsMulti
import hash
import ifTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rememberIt

class BruteForceWindowElement : WindowElement() {
    override val name = "Brute Force"

    @Composable
    override fun windowComposable() {
        var rawInput by rememberIt("")
        var bruteForcedValue by rememberIt("")
        var selectedHashAlgorithm by rememberIt(HashAlgorithm.MD5)
        var expandDropDown by rememberIt(false)

        var charsToUse by rememberIt((('A'..'Z') + ('a'..'z') + ('0'..'9')).joinToString(""))
        var minLength by rememberIt<Int?>(null)
        var maxLength by rememberIt<Int?>(10)

        var isLoading by rememberIt(false)

        SplittedWindow(leftSide = {
            TextField(rawInput, placeholder = {
                Text("Hash-String to Brute Force")
            }, modifier = Modifier.fillMaxSize(), onValueChange = {
                rawInput = it
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
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(minLength.toString(), onValueChange = {
                minLength = it.toIntOrNull()
            }, label = {
                Text("Minimal length")
            })
            Text(
                "Optional. If set, it will check all possible string lengths from this up to maximal length. Will use multiple threads. Could increase calculation time enormously!",
                fontSize = 9.sp
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(maxLength.toString(), onValueChange = {
                maxLength = it.toIntOrNull()
            }, label = {
                Text("Maximal length")
            })
            Text(
                "Required. If minimal is not set, it will create and lookup only strings with exact that length. Much faster. Increase calculation time enormously with higher values! If null, the count of characters below will be used.",
                fontSize = 9.sp
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(charsToUse, label = {
                Text("Chars to use")
            }, modifier = Modifier.fillMaxWidth(), onValueChange = {
                charsToUse = it
            })
            Text("Required. Only this chars will be used for generating strings!", fontSize = 9.sp)

            Spacer(Modifier.height(16.dp))

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                coroutine.launch {
                    if (maxLength == null) {
                        displayDialog("Max length should be set!")
                        delay(1000)
                        return@launch
                    }
                    if (charsToUse.isEmpty()) {
                        displayDialog("You need some characters to try brute force!")
                        return@launch
                    }
                    if (rawInput.isBlank()) {
                        displayDialog("Hash-input is empty. Please insert a valid hash.")
                        return@launch
                    }

                    bruteForcedValue = ""
                    isLoading = true

                    displayDialog("Brute Force is running. Please wait until it is finished!")

                    if (minLength == null) {
                        charsToUse.toCharArray()
                            .createPermutations(maxLength ?: charsToUse.length, doNotFillResultList = true) {
                                if (it.hash(selectedHashAlgorithm) == rawInput) {
                                    bruteForcedValue = it
                                    true
                                } else false
                            }.let {
                                displayDialog("Finished within ${it.timeMillis}ms. ${if (bruteForcedValue.isEmpty()) "No matching result found." else ""}")
                        }
                    } else {
                        charsToUse.toCharArray()
                            .createPermutationsMulti(minLength!!, maxLength ?: charsToUse.length, true) {
                                if (it.hash(selectedHashAlgorithm) == rawInput) {
                                    bruteForcedValue = it
                                    true
                                } else false
                            }.let { map ->
                                val longestTime = map.values.maxOf { it.timeMillis }
                                displayDialog("Finished within ${longestTime}ms. ${if (bruteForcedValue.isEmpty()) "No matching result found." else ""}")
                            }
                    }
                    isLoading = false
                }
            }) {
                Text("Brute Force")
                isLoading.ifTrue { CircularProgressIndicator(color = Color.Black) }
            }
            Text(
                "This may take minutes, hours or years. Depending on the Hash an the possibilities/settings.",
                fontSize = 9.sp
            )
        }, rightSide = {
            SelectionContainer {
                Text(bruteForcedValue, modifier = Modifier.fillMaxSize())
            }
        })
    }

    override fun onEnd() {
        super.onEnd()
        //ToDo: Cancel brute force!!
    }
}