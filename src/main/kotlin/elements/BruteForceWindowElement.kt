package elements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coroutine
import createPermutations
import createPermutationsMulti
import elements.base.DefaultSplitWindowElement
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotnexlib.HashAlgorithm
import kotnexlib.hash
import kotnexlib.ifTrue

class BruteForceWindowElement : DefaultSplitWindowElement() {
    override val name = "Brute Force"

    private var currentRunningJob by mutableStateOf<Job?>(null)

    private var rawInput by mutableStateOf("")
    private var bruteForcedValue by mutableStateOf("")
    private var selectedHashAlgorithm by mutableStateOf(HashAlgorithm.MD5)
    private var expandDropDown by mutableStateOf(false)

    private var charsToUse by mutableStateOf((('A'..'Z') + ('a'..'z') + ('0'..'9')).joinToString(""))
    private var minLength by mutableStateOf("")
    private var maxLength by mutableStateOf("10")

    private var isLoading by mutableStateOf(false)

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        TextField(rawInput, placeholder = {
            Text("Hash-String to Brute Force")
        }, modifier = Modifier.fillMaxSize(), onValueChange = {
            rawInput = it
        })
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        Text(bruteForcedValue)
    }

    override fun getMiddle(): @Composable ColumnScope.() -> Unit = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
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
            OutlinedTextField(minLength, onValueChange = {
                minLength = it.toIntOrNull()?.toString() ?: return@OutlinedTextField
            }, label = {
                Text("Minimal length")
            })
            Text(
                "Optional. If set, it will check all possible string lengths from this up to maximal length. Will use multiple threads. Could increase calculation time enormously!",
                fontSize = 9.sp
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(maxLength, onValueChange = {
                maxLength = it.toIntOrNull()?.toString() ?: return@OutlinedTextField
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
                currentRunningJob?.let {
                    if (it.isActive) it.cancel()
                    currentRunningJob = null
                }
                currentRunningJob = coroutine.launch {
                    if (maxLength.isBlank() || maxLength.toIntOrNull() == null) {
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

                    if (minLength.isBlank()) {
                        charsToUse.toCharArray()
                            .createPermutations(
                                maxLength.toIntOrNull() ?: charsToUse.length,
                                doNotFillResultList = true
                            ) {
                                if (it.hash(selectedHashAlgorithm) == rawInput) {
                                    bruteForcedValue = it
                                    true
                                } else false
                            }.let {
                                displayDialog("Finished within ${it.timeMillis}ms. ${if (bruteForcedValue.isEmpty()) "No matching result found." else ""}")
                            }
                    } else {
                        charsToUse.toCharArray()
                            .createPermutationsMulti(
                                minLength.toIntOrNull() ?: 1,
                                maxLength.toIntOrNull() ?: charsToUse.length,
                                true
                            ) {
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
                    currentRunningJob = null
                }
            }) {
                Text("Brute Force")
                isLoading.ifTrue { CircularProgressIndicator(color = Color.Black) }
            }
            Text(
                "This may take minutes, hours or years. Depending on the Hash an the possibilities/settings.",
                fontSize = 9.sp
            )

            if (currentRunningJob != null) {
                Button({
                    currentRunningJob?.cancel()
                    currentRunningJob = null
                    isLoading = false
                }, Modifier.fillMaxWidth()) {
                    Text("Cancel (not working)")
                }
            }
        }
    }

    override fun getTopView() = null

    override fun onEnd() {
        super.onEnd()
        currentRunningJob?.cancel()
    }
}