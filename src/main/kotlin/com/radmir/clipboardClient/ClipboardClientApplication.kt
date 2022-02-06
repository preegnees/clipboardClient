package com.radmir.clipboardClient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication

@SpringBootApplication
class ClipboardClientApplication

fun main(args: Array<String>) {
//	val builder = SpringApplicationBuilder(ClipboardClientApplication::class.java)
//	builder.headless(false)
//	builder.run(*args)
//	System.setProperty("java.awt.headless", "false");
	runApplication<ClipboardClientApplication>(*args)
}
