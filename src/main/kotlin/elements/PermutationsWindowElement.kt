package elements

import SplittedWindow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coroutine
import createPermutations
import createPermutationsMulti
import elements.base.WindowElement
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotnexlib.ifTrue
import rememberIt
import kotlin.math.pow

@OptIn(ExperimentalFoundationApi::class)
class PermutationsWindowElement : WindowElement() {
    override val name = "Permutations"

    @Composable
    override fun windowComposable() {

        val values = remember { mutableStateListOf<String>() }

        var charsToUse by rememberIt("abcd")
        var minLength by rememberIt<Int?>(null)
        var maxLength by rememberIt<Int?>(3)

        var isLoading by rememberIt(false)
        var limitForSOO by rememberIt(false)

        var infoText by rememberIt("")
        SplittedWindow(leftSide = {
            Column {
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(minLength.toString(), onValueChange = {
                    minLength = it.toIntOrNull()
                }, label = {
                    Text("Minimal length")
                })
                Text(
                    "Optional. If set, it will generate all possible string lengths from this up to maximal length. Will use multiple threads. Could increase calculation time enormously!",
                    fontSize = 9.sp
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(maxLength.toString(), onValueChange = {
                    maxLength = it.toIntOrNull()
                }, label = {
                    Text("Maximal length")
                })
                Text(
                    "Required. If minimal is not set, it will create only strings with exact that length. Much faster. Increase calculation time enormously with higher values! If null, the count of characters below will be used.",
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
                        val generate = charsToUse.length.toDouble().pow(maxLength ?: charsToUse.length)
                        if (generate > 5_000_000) {
                            infoText =
                                "You try to generate ${generate.toInt()} permutations. As this could lead to problems like StackOverflow, please lower the length and/or the characters."
                            return@launch
                        }

                        if (maxLength == null) {
                            infoText = "Max length should be set!"
                            delay(1000)
                        }
                        if (charsToUse.isEmpty()) {
                            infoText = "You need some characters to try brute force!"
                            return@launch
                        }
                        values.clear()
                        isLoading = true

                        infoText = "Brute Force is running. Please wait until it is finished!"

                        if (minLength == null) {
                            charsToUse.toCharArray().createPermutations(maxLength ?: charsToUse.length) {
                                if (values.size > 1_000) limitForSOO = true
                                else values.add(it)
                                false
                            }.let {
                                infoText =
                                    "Finished within ${it.timeMillis}ms. -> Generated ${it.result.size} items."
                            }
                        } else {
                            charsToUse.toCharArray()
                                .createPermutationsMulti(
                                    minLength!!,
                                    maxLength ?: charsToUse.length,
                                    onEachGeneration = @Synchronized {
                                        if (values.size > 1_000) {
                                            limitForSOO = true
                                            false
                                        } else {
                                            values.add(it)
                                            false
                                        }
                                    }).let { map ->
                                    val longestTime = map.values.maxOf { it.timeMillis }
                                    infoText =
                                        "Finished within ${longestTime}ms. Generated ${map.values.sumOf { it.result.size }} items."
                                }
                        }
                        isLoading = false
                    }
                }) {
                    Text("Generate")
                    Spacer(Modifier.weight(1f))
                    isLoading.ifTrue { CircularProgressIndicator(color = Color.Black) }
                }
                Text(
                    "This may take minutes, hours or years. Depending on the possibilities/settings.",
                    fontSize = 9.sp
                )

                Spacer(Modifier.height(16.dp))
                if (infoText.isNotBlank()) Text(infoText)
            }
        }, middle = null, rightSide = {
            SelectionContainer {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    if (limitForSOO) {
                        stickyHeader {
                            Text("The output is limited to 1_000 generations to prevent an StackOverflow!")
                        }
                    }

                    items(values) {
                        Text(it, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        })
    }
}