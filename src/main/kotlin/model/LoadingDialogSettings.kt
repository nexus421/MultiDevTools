package model

/**
 * @param title of the dialog
 * @param text will be displayed above the loading indicator
 * @param useLine if true, it will use the LinearProgressIndicator. Otherwise it uses the CircularProgessIndicator.
 * @param onCancelRequest if set, the dialog is cancelable. This callback will be called, when the users wants to cancel. It is your responsibility to close the dialog.
 * If not set, the loading is not cancelable!
 */
data class LoadingDialogSettings(
    val title: String = "Please wait...",
    val text: String = "",
    val useLine: Boolean = true,
    val onCancelRequest: (() -> Unit)? = null
)