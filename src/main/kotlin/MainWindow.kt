import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import elements.base.WindowElement
import kotlinx.coroutines.launch
import kotnexlib.ifNotNullOrBlank

var selectedElement by mutableStateOf<WindowElement?>(null)
    private set

@Composable
fun MainWindow(elements: List<WindowElement>) {

    LaunchedEffect(Unit) {
        selectedElement = elements.first()
    }

    Box {
        Row(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(elements) { windowElement ->
                    val buttonColors =
                        if (selectedElement == windowElement) ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray) else ButtonDefaults.buttonColors()
                    Button(modifier = Modifier.width(200.dp), colors = buttonColors, onClick = {
                        coroutine.launch {
                            selectedElement?.let {
                                selectedElement = null
                                it.onEnd()
                            }
                            windowElement.onStart()
                            selectedElement = windowElement
                        }
                    }) {
                        Text(windowElement.name, color = Color.White)
                    }
                }
            }
            Divider(
                color = Color.White, modifier = Modifier
                    .fillMaxHeight()  //fill the max height
                    .width(1.dp)
            )
            Box(modifier = Modifier.weight(1f).padding(8.dp)) {

                if (selectedElement == null) SimpleLoadingDialog("Finishing current window. Please wait...")
                selectedElement?.apply {
                    windowComposable()
                    textToDisplayThroughDialog.ifNotNullOrBlank {
                        /*
                        MODIFIE THIS CAREFULLY!
                        This is the default AlertDialog for messages which can be used through all WindowElements!
                        If you want a customised dialog, do it on your own!
                         */
                        AlertDialog(onDismissRequest = {}, confirmButton = {
                            ButtonText("OK") {
                                dismissDialog()
                            }
                        }, title = {
                            Text("Information for $name")
                        }, text = {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                Text(this@ifNotNullOrBlank)
                            }
                        }, modifier = Modifier.border(2.dp, Color.Gray, MaterialTheme.shapes.medium)
                        )
                    }

                    loading?.let { loadingDialogSettings ->
                        AlertDialog(onDismissRequest = {}, confirmButton = {
                            loadingDialogSettings.onCancelRequest?.let {
                                ButtonText("Cancel") {
                                    it.invoke()
                                }
                            }
                        }, text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                loadingDialogSettings.text.ifNotNullOrBlank { Text(this) }
                                Spacer(Modifier.height(16.dp))
                                if (loadingDialogSettings.useLine) LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth(),
                                    strokeCap = StrokeCap.Round
                                )
                                else CircularProgressIndicator()
                            }
                        }, title = {
                            loadingDialogSettings.title.ifNotNullOrBlank { Text(this) }
                        }, modifier = Modifier.border(2.dp, Color.Gray, MaterialTheme.shapes.medium))
                    }
                }
            }
        }
    }
}

/**
 * Simple LoadingDialog.
 * Shows a title and a LinearProgressIndicator
 */
@Composable
fun SimpleLoadingDialog(title: String = "Please wait...") {
    AlertDialog(onDismissRequest = {}, confirmButton = {}, text = {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), strokeCap = StrokeCap.Round)
    }, title = {
        Text(title)
    }, modifier = Modifier.border(2.dp, Color.Gray, MaterialTheme.shapes.medium))
}