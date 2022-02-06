package com.radmir.clipboardClient.network

import com.neovisionaries.ws.client.*
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.ssl.SSLContexts
import org.springframework.stereotype.Component
import java.io.File
import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.net.ssl.SSLContext

@Component
class WebSocketClientSSL {

    val ws: WebSocket = getFactory().createSocket("wss://localhost:8080/test")

    private fun getFactory(): WebSocketFactory {
        val sslContext = getSslContext("my-https.jks", "secret")
        val factory = WebSocketFactory().setConnectionTimeout(999999999)
        factory.sslContext = sslContext
        return factory
    }
    private fun getSslContext(fileName: String, key: String): SSLContext? {
        return SSLContexts.custom()
            .loadTrustMaterial(
                File(fileName), key.toCharArray(), // secret это ключ, который указывали при создании сертификата
                TrustSelfSignedStrategy()
            )
            .build()
    }

    fun open() {
        try {
            ws.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun send(message: String) {
        try {
            ws.sendText(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun listen(): String {
        var str = ""
        try {
            ws.addListener(object : WebSocketAdapter() {
                override fun onTextMessage(websocket: WebSocket?, data: String?) {
                    if (data != null) {
                        str = data
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return str
    }
    fun close() {
        try {
            ws.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}