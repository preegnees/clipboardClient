package com.radmir.clipboardClient.init.platform

import org.springframework.stereotype.Component

@Component
interface Platform {
    fun notification(text: String, title: String)
    fun getFromClipboard(): String
    fun setToClipboard(text: String)
}