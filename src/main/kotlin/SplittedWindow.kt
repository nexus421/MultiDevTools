import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SplittedWindow(
    leftSide: @Composable BoxScope.() -> Unit,
    middle: (@Composable ColumnScope.() -> Unit)?,
    rightSide: @Composable BoxScope.() -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.weight(1f).border(2.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(8.dp)
                .fillMaxSize(), content = leftSide
        )
        middle?.let {
            Column(
                modifier = Modifier.weight(0.5f).border(2.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(8.dp)
                    .fillMaxSize(), content = it
            )
        }
        Box(
            modifier = Modifier.weight(1f).border(2.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(8.dp)
                .fillMaxSize(), content = rightSide
        )
    }
}