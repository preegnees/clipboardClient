package com.radmir.clipboardClient.init

import com.radmir.clipboardClient.database.h2.ConfigDAO
import com.radmir.clipboardClient.database.h2.ConfigEntity
import com.radmir.clipboardClient.init.gson.FromServer
import com.radmir.clipboardClient.init.gson.Message
import com.radmir.clipboardClient.init.gson.ToServer
import com.radmir.clipboardClient.init.platform.Platform
import com.radmir.clipboardClient.init.platform.PlatformDesktop
import com.radmir.clipboardClient.init.platform.PlatformTermux
import com.radmir.clipboardClient.network.WebSocketClientSSL
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct
import kotlin.concurrent.thread


@Component
class Init {

    @Autowired
    private lateinit var webSocketClientSSL: WebSocketClientSSL
    @Autowired
    private lateinit var storage: ConfigDAO
    @Autowired
    private lateinit var fromServer: FromServer
    @Autowired
    private lateinit var toServer: ToServer

    /**
    1) при старте приложение должно посмотреть в базу данных, если она пуста,
    то нужно не включать до тех пор, когда пользователь не сконфигурирует программу
    (server, myName, pairName, timeout, on, off, text), где on, off, text не заносить в базу данных
    2) если приложение сконфигурировано, то заносим эту конфигурацию в memoryStorage
    3) если у нас есть server, myName, pairName и on = true, то программа начинает работу
     * */
    private val isConfigServer = "app...server..." // app...server...192.168.0.101:8080
    private val isConfigMyName = "app...myname..." // app...myname...radmir
    private val isConfigPairName = "app...pairname..." // app...pairname...bogdan
    private val isConfigTimeout = "app...timeout..." // app...timeout...300
    private val isConfigRun = "app...run..." // app...run...1 (start), app...run...0 (stop)

    private val service = getPlatform()

    private var newMessage = ""
    private var oldMessage = ""
    var oldText = ""
    var newText = ""

    @PostConstruct
    fun start() {
        webSocketClientSSL.open()
        storage.save(ConfigEntity(component = "run", value = "1"))
        storage.save(ConfigEntity(component = "myName", value = "default"))
        storage.save(ConfigEntity(component = "pairName", value = "defualt"))

        thread {
            newMessage = webSocketClientSSL.listen()
        }

        var onStartup = false
        while (true) {
            if (newMessage != oldMessage) {
                service.setToClipboard(fromServer.start(newMessage).text!!)
                service.notification(fromServer.start(newMessage).text!!, fromServer.start(newMessage).who!!)
                oldMessage = newMessage
            }

            Thread.sleep(try {
                storage.getById("timeout").value!!.toLong()
            } catch (e: Exception) {
                1000
            })
            val fromClipboard = service.getFromClipboard()
            newText = fromClipboard
            if (newText != oldText) {
                if (isRun(fromClipboard)) {
                    if (!isServer(fromClipboard)) {
                        if (!isMyName(fromClipboard)) {
                            if (!isPairName(fromClipboard)) {
                                if (!isTimeout(fromClipboard)) {
                                    if (!onStartup) {
                                        onStartup = true
                                        oldText = newText
                                        continue
                                    } else {
                                        webSocketClientSSL
                                            .send(toServer
                                                .start(Message(who = storage.getById("myName").value,
                                                    whom = storage.getById("pairName").value,
                                                    text = fromClipboard)))
                                        oldText = newText
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

    }

    private fun isServer(text: String): Boolean {
        if (isConfigServer in text) {
            val server = text
                .replace(" ","")
                .replace(isConfigServer, "")
                .replace("\r","")
                .replace("\n", "")
            if (server.split(".") .size == 4 && server.split(":").size == 2) {
                storage.save(ConfigEntity(component = "server", value = server))
                service.notification(server, "server")
                service.setToClipboard(":)")
                return true
            }
        }
        return false
    }
    private fun isMyName(text: String): Boolean {
        if (isConfigMyName in text) {
            val myName = text
                .replace(" ","")
                .replace(isConfigMyName, "")
                .replace("\r","")
                .replace("\n", "")
            if (myName.isNotEmpty()) {
                storage.save(ConfigEntity(component = "myName", value = myName))
                service.notification(myName, "myName")
                service.setToClipboard(":)")
                return true
            }
        }
        return false
    }
    private fun isPairName(text: String): Boolean {
        if (isConfigPairName in text) {
            val pairName = text
                .replace(" ","")
                .replace(isConfigPairName, "")
                .replace("\r","")
                .replace("\n", "")
            if (pairName.isNotEmpty()) {
                storage.save(ConfigEntity(component = "pairName", value = pairName))
                service.notification(pairName, "pairName")
                service.setToClipboard(":)")
                return true
            }
        }
        return false
    }
    private fun isTimeout(text: String): Boolean {
        if (isConfigTimeout in text) {
            val timeout = text
                .replace(" ","")
                .replace(isConfigTimeout, "")
                .replace("\r","")
                .replace("\n", "")
            try {
                val intTimeout = try {
                    timeout.toInt().toString()
                } catch (e: Exception) {
                    ""
                }
                if (intTimeout.isNotEmpty()) {
                    storage.save(ConfigEntity(component = "timeout", value = timeout))
                    service.notification(timeout, "timeout")
                    service.setToClipboard(":)")
                    return true
                }
            } finally {}
        }
        return false
    }
    private fun isRun(text: String): Boolean {
        if (storage.getById("run").value == "1" && isConfigRun !in text) {
            return true
        }
        if (isConfigRun in text) {
            val run = text
                .replace(" ","")
                .replace(isConfigRun, "")
                .replace("\r","")
                .replace("\n", "")
            try {
                val isint = try {
                    run.toInt().toString()
                } catch (e: Exception) {
                    ""
                }
                if (isint.isNotEmpty()) {
                    if (run.toInt() == 1) {
                        storage.save(ConfigEntity(component = "run", value = run))
                        service.notification("start", "app")
                        service.setToClipboard(":)")
                        return true
                    } else {
                        if (run.isNotEmpty()) {
                            storage.save(ConfigEntity(component = "run", value = run))
                            service.notification("stop", "app")
                            service.setToClipboard(":)")
                            return true
                        }
                    }
                }
            } finally {}
        }
        return false
    }

    private fun getPlatform(): Platform {
//        return PlatformTermux()
        return when (OsCheck.operatingSystemType) {
            OsCheck.OSType.Windows -> PlatformDesktop()
            OsCheck.OSType.MacOS -> PlatformDesktop()
            OsCheck.OSType.Linux -> PlatformDesktop() // or termux
            else -> PlatformTermux() // or nothing
        }
    }
}

object OsCheck {
    private var detectedOS: OSType? = null
    val operatingSystemType: OSType?
        get() {
            if (detectedOS == null) {
                val os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH)
                detectedOS = if (os.indexOf("mac") >= 0 || os.indexOf("darwin") >= 0) {
                    OSType.MacOS
                } else if (os.indexOf("win") >= 0) {
                    OSType.Windows
                } else if (os.indexOf("nux") >= 0) {
                    OSType.Linux
                } else {
                    OSType.Other
                }
            }
            return detectedOS
        }
    enum class OSType {
        Windows, MacOS, Linux, Other
    }
}