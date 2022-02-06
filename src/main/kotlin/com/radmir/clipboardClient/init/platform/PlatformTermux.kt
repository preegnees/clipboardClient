package com.radmir.clipboardClient.init.platform

import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.springframework.stereotype.Component
import java.io.PrintWriter
import java.util.*


@Component
class PlatformTermux: Platform {

    override fun notification(text: String, title: String) {
        try {
            val line = "python termuxNotify.py --title \"$title\" --text \"$text\""
            val cmdLine = CommandLine.parse(line)
            val executor = DefaultExecutor()
            executor.execute(cmdLine)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getFromClipboard(): String {
        try {
            val command = Runtime.getRuntime().exec("cmd")
            val stdIn = PrintWriter(command.outputStream)
            stdIn.println("termux-clipboard-get")
            stdIn.close()
            command.waitFor()
            var out = ""
            val scanner = Scanner(command.inputStream, "UTF-8")
            while (scanner.hasNextLine()) {
                out += scanner.nextLine()
            }
            return out
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    override fun setToClipboard(text: String) {
        try {
            val command = Runtime.getRuntime().exec("cmd")
            val stdIn = PrintWriter(command.outputStream)
            stdIn.println("termux-clipboard-set")
            stdIn.close()
            command.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}