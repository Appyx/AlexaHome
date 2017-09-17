package at.rgstoettner.alexahome.executor

import at.rgstoettner.alexahome.executor.connection.Endpoint
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLSocket


val lock = Object()

fun main(args: Array<String>) {

    val settings = Settings.load()
    val endpoint = Endpoint("wss://${settings.localIp}:${settings.localPort}/wss")
    endpoint.socket = SecureSocket(settings.password!!).getSocket()
    endpoint.connectBlocking()

    val verifier = HttpsURLConnection.getDefaultHostnameVerifier()
    val socket = endpoint.getSocket() as SSLSocket
    val session = socket.getSession()
    if (!verifier.verify(settings.localIp, session)) {
        "Certificate for <${settings.localIp}> doesn't match any of the subject alternative names".println()
        throw SSLHandshakeException("Hostname verification failed")
    }


    synchronized(lock) {
        lock.wait()
    }
}


fun String.println() {
    println(this)
}