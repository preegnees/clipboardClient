package com.radmir.clipboardClient.database.swaudb

import org.springframework.stereotype.Component
import swaydb.java.Map
import swaydb.java.memory.MemoryMap
import swaydb.java.serializers.Default.intSerializer
import swaydb.java.serializers.Default.stringSerializer


@Component
class MemoryStorage {
    var map: Map<String, String, Void> = MemoryMap
        .functionsOff(stringSerializer(), stringSerializer())
        .get()

    fun set(key: String, value: String) {
        map.put(key, value)
    }
    fun get(key: String): String {
        return map.get(key).get()
    }
}