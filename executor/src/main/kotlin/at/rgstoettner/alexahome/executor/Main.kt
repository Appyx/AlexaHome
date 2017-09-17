package at.rgstoettner.alexahome.executor

import at.rgstoettner.alexahome.executor.connection.SocketManager


val lock = Object()

fun main(args: Array<String>) {
    var isLocal = false
    var alias: String? = null
    args.forEach {
        if (it.startsWith("--local")) {
            isLocal = true
        }
        if (it.startsWith("--alias=")) {
            val parts = it.split("=")
            alias = parts.getOrNull(1)
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


    synchronized(lock) {
        lock.wait()
    }
}


fun String.println() {
    println(this)
}