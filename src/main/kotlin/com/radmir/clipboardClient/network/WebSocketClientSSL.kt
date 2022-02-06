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

    fun init() {
        val sslContext = getSslContext("my-https.jks", "secret")
        val factory = getFactory(999999999)
        factory.sslContext = sslContext
        val ws = factory.createSocket("wss://localhost:8080/test")
        open(ws)
        send(ws, "hello world")
        listen(ws)
        close(ws)
    }
    private fun getFactory(millisecond: Int): WebSocketFactory {
        val factory = WebSocketFactory()
        factory.connectionTimeout = millisecond
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
    private fun open(ws: WebSocket) {
        try {
            ws.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun send(ws: WebSocket, message: String) {
        try {
            ws.sendText(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun listen(ws: WebSocket): String {
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
    private fun close(ws: WebSocket) {
        try {
            ws.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}