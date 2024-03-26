package elements

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import elements.base.DefaultSplitWindowElement
import kotnexlib.format
import kotnexlib.toDate
import kotnexlib.toDateOrNull
import kotnexlib.tryOrNull
import java.util.*

class TimeWindowElement : DefaultSplitWindowElement() {
    override val name = "Datetime"

    private var timeInMillis by mutableStateOf<Long?>(null)
    private var convertedDate by mutableStateOf("")
    private var format by mutableStateOf("dd.MM.yyyy HH:mm")

    override fun getLeftSide(): @Composable BoxScope.() -> Unit = {
        Column {
            OutlinedTextField((timeInMillis ?: "").toString(), modifier = Modifier.fillMaxWidth(), onValueChange = {
                timeInMillis = it.toLongOrNull() ?: return@OutlinedTextField
            }, label = { Text("Milliseconds") })

            Button(onClick = {
                if (timeInMillis == null && convertedDate.isBlank()) return@Button
                tryOrNull({
                    displayDialog(it.message ?: it.stackTraceToString())
                }) {
                    val converted = timeInMillis?.toDate()?.format(format)
                    if (converted != null) convertedDate = converted
                    else postErrorMessage("Error while converting. Check the entered milliseconds.")
                }
            }, Modifier.fillMaxWidth()) {
                Text("Convert to readable date")
            }
        }
    }

    override fun getRightSide(): @Composable BoxScope.() -> Unit = {
        Column {
            OutlinedTextField(convertedDate, modifier = Modifier.fillMaxWidth(), onValueChange = {
                convertedDate = it
            }, label = { Text("Converted date") })

            Button(onClick = {
                if (timeInMillis == null && convertedDate.isBlank()) return@Button
                tryOrNull({
                    displayDialog(it.message ?: it.stackTraceToString())
                }) {
                    val converted = convertedDate.toDateOrNull(format)?.time
                    if (converted != null) timeInMillis = converted
                    else postErrorMessage("Error while converting. Check the entered date and the date format.")
                }
            }, Modifier.fillMaxWidth()) {
                Text("Convert to millis")
            }
        }
    }

    override fun getMiddle(): @Composable ColumnScope.() -> Unit = {
        OutlinedTextField(format, modifier = Modifier.fillMaxWidth(), onValueChange = {
            format = it
        }, label = { Text("Date format") })

        Button(onClick = {
            tryOrNull({
                displayDialog(it.message ?: it.stackTraceToString())
            }) {
                val now = Date()
                timeInMillis = now.time
                convertedDate = now.format(format)
            }
        }, Modifier.fillMaxWidth()) {
            Text("Now")
        }
    }

    override fun getTopView() = null
}