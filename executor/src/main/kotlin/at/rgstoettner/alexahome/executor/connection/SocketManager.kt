package at.rgstoettner.alexahome.executor.connection

import at.rgstoettner.alexahome.executor.SecureSocket
import at.rgstoettner.alexahome.executor.Settings
import at.rgstoettner.alexahome.executor.println
import java.util.*
import kotlin.system.exitProcess


class SocketManager(val settings: Settings, val local: Boolean) {

    private var endpoint: Endpoint? = null
    private var delay = 0L
    private var onReadyHandler: () -> Unit = {}
    private var onMessageHandler: (String) -> String = {""}

    private val host: String
    private val port: String


    init {
        if (local) {
            host = settings.localIp!!
            port = settings.localPort!!.toString()
        } else {
            host = settings.remoteDomain!!
            port = settings.remotePort!!.toString()
        }
    }

    fun connect(onReady: () -> Unit, onMessage: (String) -> String) {
        onMessageHandler = onMessage
        onReadyHandler = onReady

        "Connecting to: $host:$port".println()
        endpoint = createEndpoint()
        endpoint?.connect()
    }

    private fun createEndpoint(): Endpoint {
        val endpoint = Endpoint("wss://${host}:${port}/wss", 2000)
        endpoint.socket = SecureSocket(settings.password!!).getSocket()
        endpoint.onClosedHandler = { reconnect() }
        endpoint.onExceptionHandler = { reconnect() }
        endpoint.onOpenHandler = { onReadyHandler.invoke(); delay = 0 }
        endpoint.onMessageHandler = onMessageHandler
        return endpoint
    }


    private fun reconnect() {
        "Reonnecting in: $delay ms".println()
        endpoint?.close()
        endpoint = createEndpoint()
        delay += 1000

        if (delay > 1000 * 60 * 10) exitProcess(1)
        //longest wait time is 10 minutes

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                endpoint?.connect()
            }
        }, delay)
    }

    fun send(text: String) {
        endpoint?.send(text)
    }


}