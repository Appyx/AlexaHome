package at.rgstoettner.alexahome.executor.connection

import at.rgstoettner.alexahome.executor.println
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class Endpoint(val url: String, timeout: Int) : WebSocketClient(URI(url), Draft_6455(), null, timeout) {

    var onOpenHandler: () -> Unit = {}
    var onClosedHandler: () -> Unit = {}
    var onExceptionHandler: () -> Unit = {}
    var onMessageHandler: (String) -> String = { "" }

    override fun onOpen(handshakedata: ServerHandshake) {
        "Connected to: $url".println()
        onOpenHandler.invoke()
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        "Disconnected: $reason".println()
        onClosedHandler.invoke()
        destroy()
    }

    override fun onMessage(message: String) {
        //"Message received: $message".println()
        val response = onMessageHandler.invoke(message)
        //"Message sent: $response".println()
        send(response)
    }

    override fun onError(ex: Exception) {
        "Exception occurred: ${ex.message}".println()
        onExceptionHandler.invoke()
        destroy()
    }

    private fun destroy() {
        onMessageHandler = { "" }
        onClosedHandler = {}
        onMessageHandler = { "" }
        onOpenHandler = {}
    }

}