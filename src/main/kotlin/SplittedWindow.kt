import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Default implementation for WindowElements.
 *
 * @param topView optional view at the top
 * @param leftSide required view on the left side inside a Box
 * @param middle optional view for in the middle. Should be used for settings, etc.
 * @param rightSide required view on the right side inside a Box
 */
@Composable
fun SplittedWindow(
    topView: (@Composable BoxScope.() -> Unit)? = null,
    leftSide: @Composable BoxScope.() -> Unit,
    middle: (@Composable ColumnScope.() -> Unit)?,
    rightSide: @Composable BoxScope.() -> Unit
) {
    Column {
        topView?.let {
            Box(
                modifier = Modifier.padding(bottom = 4.dp).border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp).fillMaxWidth(), content = it
            )
        }
        Row(Modifier.weight(1f)) {
            Box(
                modifier = Modifier.weight(1f).border(2.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(8.dp)
                    .fillMaxHeight(),
                content = leftSide
            )
            Spacer(Modifier.width(4.dp))
            middle?.let {
                Column(
                    modifier = Modifier.weight(0.5f).border(2.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(8.dp)
                        .fillMaxHeight(),
                    content = it
                )
                Spacer(Modifier.width(4.dp))
            }
            Box(
                modifier = Modifier.weight(1f).border(2.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(8.dp)
                    .fillMaxHeight(),
                content = rightSide
            )
        }
    }
}

/**
 * Default implementation for WindowElements.
 *
 * @param topView optional view at the top
 * @param leftSide required view on the left side inside a Box
 * @param middle optional view for in the middle. Should be used for settings, etc.
 * @param rightSide required view on the right side inside a Box
 */
@Composable
fun ColumnScope.DynamicSplitWindow(
    topView: (@Composable BoxScope.() -> Unit)? = null,
    leftSide: @Composable BoxScope.() -> Unit,
    middle: (@Composable ColumnScope.() -> Unit)?,
    rightSide: @Composable BoxScope.() -> Unit
) {
    Column(Modifier.weight(1f)) {
        topView?.let {
            Box(
                modifier = Modifier.padding(bottom = 4.dp).border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp).fillMaxWidth(), content = it
            )
        }
        Row {
            Box(
                modifier = Modifier.weight(1f).border(2.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(8.dp)
                    .fillMaxHeight(), content = leftSide
            )
            Spacer(Modifier.width(4.dp))
            middle?.let {
                Column(
                    modifier = Modifier.weight(0.5f).border(2.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(8.dp)
                        .fillMaxHeight(), content = it
                )
                Spacer(Modifier.width(4.dp))
            }
            Box(
                modifier = Modifier.weight(1f).border(2.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(8.dp)
                    .fillMaxHeight(), content = rightSide
            )
        }
    }
}