package at.rgstoettner.alexahome.executor

import at.rgstoettner.alexahome.executor.connection.MessageHandler
import at.rgstoettner.alexahome.executor.connection.ReconnectingSocket
import at.rgstoettner.alexahome.executor.plugin.PluginLoader


val lock = Object()

fun main(args: Array<String>) {
    var isLocal = false
    var pluginDir: String? = null
    args.forEach {
        if (it.startsWith("--local")) {
            isLocal = true
        }
        if (it.startsWith("--plugins=")) {
            val parts = it.split("=")
            pluginDir = parts.getOrNull(1)
        }
    }
    val pluginLoader = PluginLoader(pluginDir)
    val settings = Settings.load()
    val socket = ReconnectingSocket(settings, isLocal)
    val messageHandler=MessageHandler(socket,pluginLoader)
    messageHandler.handle()

    synchronized(lock) {
        lock.wait()
    }
}



fun String.println() {
    println(this)
}