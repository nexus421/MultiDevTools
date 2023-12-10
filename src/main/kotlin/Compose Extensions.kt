import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CheckBoxText(text: String, state: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text, modifier = Modifier.weight(1f))
        Checkbox(state, onCheckedChange)
    }
}

@Composable
fun ButtonText(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick, modifier) {
        Text(text)
    }
}

@Composable
fun <T> SimpleDropDown(selected: T, items: List<T>, onSelectedChanged: (T) -> Unit) {
    var expandDropDown by rememberIt(false)
    Box {
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
            expandDropDown = expandDropDown.not()
        }) {
            Text(selected.toString(), modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowDropDown, contentDescription = "DropDown")
        }
        DropdownMenu(expandDropDown, onDismissRequest = { expandDropDown = false }) {
            items.forEach {
                DropdownMenuItem(onClick = {
                    onSelectedChanged(it)
                    expandDropDown = false
                }) {
                    Text(it.toString())
                }
            }
        }
    }
}