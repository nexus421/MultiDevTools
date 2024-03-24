package utils

import org.jetbrains.skia.Bitmap
import org.jetbrains.skiko.toBufferedImage
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.*
import java.io.IOException

/**
 * Based on https://stackoverflow.com/questions/4552045/copy-bufferedimage-to-clipboard
 */
internal class TransferableImage(private val image: Image?) : Transferable, ClipboardOwner {

    @Throws(UnsupportedFlavorException::class, IOException::class)
    override fun getTransferData(flavor: DataFlavor?): Any {
        if (flavor?.equals(DataFlavor.imageFlavor) == true && image != null) return image
        else throw UnsupportedFlavorException(flavor)
    }

    override fun getTransferDataFlavors(): Array<DataFlavor?> {
        val flavors = arrayOfNulls<DataFlavor>(1)
        flavors[0] = DataFlavor.imageFlavor
        return flavors
    }

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        for (i in transferDataFlavors.indices) {
            if (flavor.equals(transferDataFlavors[i])) return true
        }
        return false
    }

    override fun lostOwnership(clipboard: Clipboard?, contents: Transferable?) {

    }
}

fun Bitmap.copyToClipboard() {
    val image = TransferableImage(toBufferedImage())
    Toolkit.getDefaultToolkit().systemClipboard.setContents(image, image)
}
