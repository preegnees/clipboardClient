package com.radmir.clipboardClient.init.platform

import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.PumpStreamHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
//import java.awt.*
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.util.*
import javax.swing.*
import javax.swing.border.LineBorder


@Component
class PlatformDesktop: Platform {

    override fun notification(text: String, title: String) {
        try {
            val line = "python.exe notify.py --title \"$title\" --text \"$text\""
            val cmdLine = CommandLine.parse(line)
            val executor = DefaultExecutor()
            executor.execute(cmdLine)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getFromClipboard(): String {
        try {
            val line = "python " + "clipboard.py --option get"
            val cmdLine: CommandLine = CommandLine.parse(line)
            val outputStream = ByteArrayOutputStream()
            val streamHandler = PumpStreamHandler(outputStream)
            val executor = DefaultExecutor()
            executor.streamHandler = streamHandler
            executor.execute(cmdLine)
            return outputStream.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    override fun setToClipboard(text: String) {
        try {
            val line = "python.exe clipboard.py --option set --text \"$text\""
            val cmdLine = CommandLine.parse(line)
            val executor = DefaultExecutor()
            executor.execute(cmdLine)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}