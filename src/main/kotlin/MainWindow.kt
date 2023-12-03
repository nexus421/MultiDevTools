
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import elements.WindowElement

@Composable
fun MainWindow(elements: List<WindowElement>) {
    var selectedElement by rememberIt<WindowElement?>(null)
    Box {
        Row(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(elements) {
                    Button(modifier = Modifier.width(200.dp), onClick = {
                        selectedElement = it
                    }) {
                        Text(it.name, color = Color.White)
                    }
                }
            }
            Divider(color = Color.White, modifier = Modifier
                .fillMaxHeight()  //fill the max height
                .width(1.dp))
            Box(modifier = Modifier.weight(1f).padding(8.dp)) {
                selectedElement?.windowComposable()
            }
        }
    }
}