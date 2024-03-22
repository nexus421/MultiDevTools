package elements

import SplittedWindow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotnexlib.toDate
import kotnexlib.tryOrNull
import rememberIt
import java.text.SimpleDateFormat

class TimeWindowElement : WindowElement() {
    override val name = "Datetime"

    @Composable
    override fun windowComposable() {

        var timeInMillis by rememberIt<Long?>(null)
        var convertedDate by rememberIt("")
        var format by rememberIt("dd.MM.yyyy HH:mm:ss")

        SplittedWindow(leftSide = {
            OutlinedTextField((timeInMillis ?: "").toString(), modifier = Modifier.fillMaxWidth(), onValueChange = {
                timeInMillis = it.toLongOrNull()
            }, label = { Text("Milliseconds") })
        }, middle = {
            OutlinedTextField(format, modifier = Modifier.fillMaxWidth(), onValueChange = {
                format = it
            }, label = { Text("Date format") })

            Button(onClick = {
                if (timeInMillis == null && convertedDate.isBlank()) return@Button
                tryOrNull({
                    displayDialog(it.message ?: it.stackTraceToString())
                }) {
                    val sdf = SimpleDateFormat(format)
                    if (timeInMillis == null && convertedDate.isNotBlank()) {
                        timeInMillis = sdf.parse(convertedDate).time
                    } else {
                        convertedDate = sdf.format(timeInMillis!!.toDate())
                    }
                }
            }) {
                Text("<<Convert>>")
            }
        }, rightSide = {
            OutlinedTextField(convertedDate, modifier = Modifier.fillMaxWidth(), onValueChange = {
                convertedDate = it
            }, label = { Text("Converted date") })
        })
    }
}