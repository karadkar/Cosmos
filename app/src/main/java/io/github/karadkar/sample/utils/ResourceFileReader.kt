package io.github.karadkar.sample.utils

import java.io.InputStreamReader

class ResourceFileReader(path: String) {
    var content: String = ""
        private set

    init {
        try {
            val reader = InputStreamReader(this.javaClass.classLoader!!.getResourceAsStream(path))
            content = reader.readText()
            reader.close()
        } catch (e: Exception) {
            logError("failed to parse $path", e)
        }
    }

}