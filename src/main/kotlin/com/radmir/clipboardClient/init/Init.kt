package com.radmir.clipboardClient.init

import com.radmir.clipboardClient.database.h2.ConfigDAO
import com.radmir.clipboardClient.database.h2.ConfigEntity
import com.radmir.clipboardClient.init.platform.Platform
import com.radmir.clipboardClient.init.platform.PlatformDesktop
import com.radmir.clipboardClient.init.platform.PlatformTermux
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct


@Component
class Init {

//    @Autowired
//    private lateinit var termux: PlatformTermux
//    @Autowired
//    private lateinit var desktop: PlatformDesktop
    @Autowired
    private lateinit var storage: ConfigDAO

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
    private val isConfigTimeout = "app...timeout..." // app...timeout...1000
    private val isConfigRun = "app...run..." // app...run...1 (start), app...run...0 (stop)
    val service = getPlatform()

    @PostConstruct
    fun start() {
        var oldText = ""
        var newText = ""
        while (true) {
            Thread.sleep(try {
                storage.getById("timeout").value!!.toLong()
            } catch (e: Exception) {
                1000
            })
            val fromClipboard = service.getFromClipboard()
            newText = fromClipboard
            if (isRun(fromClipboard)) {
                if (!isServer(fromClipboard)) {
                    if (!isMyName(fromClipboard)) {
                        if (!isPairName(fromClipboard)) {
                            if (!isTimeout(fromClipboard)) {
                                if (newText != oldText) {
                                    println(fromClipboard)
                                    oldText = newText
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
            if (server.split(".") .size == 4 && server.split(":").size == 2) {
                storage.save(ConfigEntity(component = "server", value = server))
                service.notification(server, "server")
                service.setToClipboard("")
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
            if (myName.isNotEmpty()) {
                storage.save(ConfigEntity(component = "myName", value = myName))
                service.notification(myName, "myName")
                service.setToClipboard("")
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
            if (pairName.isNotEmpty()) {
                storage.save(ConfigEntity(component = "pairName", value = pairName))
                service.notification(pairName, "pairName")
                service.setToClipboard("")
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
            try {
                val intTimeout = try {
                    timeout.toInt().toString()
                } catch (e: Exception) {
                    ""
                }
                if (intTimeout.isNotEmpty()) {
                    storage.save(ConfigEntity(component = "timeout", value = timeout))
                    service.notification(timeout, "timeout")
                    service.setToClipboard("")
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
                        service.setToClipboard("")
                        return true
                    } else {
                        if (run.isNotEmpty()) {
                            storage.save(ConfigEntity(component = "run", value = run))
                            service.notification("stop", "app")
                            service.setToClipboard("")
                            return true
                        }
                    }
                }
            } finally {}
        }
        return false
    }

    private fun getPlatform(): Platform {
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