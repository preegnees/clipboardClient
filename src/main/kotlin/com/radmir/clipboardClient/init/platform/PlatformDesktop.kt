package com.radmir.clipboardClient.init.platform

import org.springframework.stereotype.Component
import java.awt.*
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder


@Component
class PlatformDesktop: Platform {

    override fun notification(text: String, title: String) {
        try {
            val toast = ToastMessage("$title: $text", 3000)
            toast.isVisible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getFromClipboard(): String {
        val toolkit: Toolkit = Toolkit.getDefaultToolkit()
        val clipboard: Clipboard = toolkit.systemClipboard
        val df = DataFlavor.stringFlavor
        return try {
            clipboard.getData(df).toString()
        } catch (e: Exception) {
            ""
        }
    }

    override fun setToClipboard(text: String) {
        val selection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        try {
            clipboard.setContents(selection, selection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class ToastMessage(toastString: String?, var milliseconds: Int) : JDialog() {
    init {
        isUndecorated = true
        contentPane.layout = BorderLayout(0, 0)
        val panel = JPanel()
        panel.background = Color.GRAY
        panel.border = LineBorder(Color.LIGHT_GRAY, 2)
        contentPane.add(panel, BorderLayout.AFTER_LAST_LINE)
        val toastLabel = JLabel("")
        toastLabel.text = toastString
        toastLabel.font = Font("Dialog", Font.BOLD, 15)
        toastLabel.foreground = Color.WHITE
        setBounds(100, 100, toastLabel.preferredSize.width + 20, 31)
        isAlwaysOnTop = true
        val dim = Toolkit.getDefaultToolkit().screenSize
        val y = dim.height / 2 - size.height / 2
        val half = (y / 1.1).toInt()
//        setLocation(dim.width / 2 - size.width / 2, y + half)
        setLocation((dim.width / 1.1 - size.width / 1.1).toInt(), y + half)
        panel.add(toastLabel)
        isVisible = false
        object : Thread() {
            override fun run() {
                try {
                    sleep(milliseconds.toLong())
                    dispose()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}