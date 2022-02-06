package com.radmir.clipboardClient.init.gson

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.springframework.stereotype.Component

@Component
data class Message(
    @SerializedName("who") var who: String? = null,
    @SerializedName("text") var text: String? =null
)
@Component
class FromServer() {
    fun start(json: String): Message {
        return Gson().fromJson(json, Message::class.java)
    }
}
@Component
class ToServer() {
    fun start(message: Message): String {
        return Gson().toJson(message)
    }
}