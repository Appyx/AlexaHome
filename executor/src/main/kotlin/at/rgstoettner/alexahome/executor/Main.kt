package at.rgstoettner.alexahome.executor

import at.rgstoettner.alexahome.executor.connection.SocketManager
import at.rgstoettner.alexahome.executor.plugin.PluginLoader


val lock = Object()

fun main(args: Array<String>) {
    var isLocal = false
    var alias: String? = null
    var plugins: String? = null
    args.forEach {
        if (it.startsWith("--local")) {
            isLocal = true
        }
        if (it.startsWith("--alias=")) {
            val parts = it.split("=")
            alias = parts.getOrNull(1)
        }
        if (it.startsWith("--plugins=")) {
            val parts = it.split("=")
            plugins = parts.getOrNull(1)
        }
    }

    val settings = Settings.load()
    val socket = SocketManager(settings, isLocal)
    socket.connect(onReady = {
        alias?.let {
            socket.send(it)
        }
    }, onMessage = {
        print("Command is: $it")
        "response"
    })


    val pluginLoader = PluginLoader(plugins)
    println(pluginLoader.runPlugin("Command", "hello plugin"))


    synchronized(lock) {
        lock.wait()
    }
}


fun String.println() {
    println(this)
}