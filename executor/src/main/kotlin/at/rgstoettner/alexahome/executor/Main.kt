package at.rgstoettner.alexahome.executor

import at.rgstoettner.alexahome.executor.connection.Endpoint

val lock = Object()

fun main(args: Array<String>) {

    val settings = Settings.load()
    val endpoint = Endpoint("wss://${settings.localIp}:${settings.localPort}/wss")
    endpoint.socket = SSLSocket(settings.password!!).getSocket()
    endpoint.connectBlocking()


    synchronized(lock) {
        lock.wait()
    }
}


fun String.println() {
    println(this)
}