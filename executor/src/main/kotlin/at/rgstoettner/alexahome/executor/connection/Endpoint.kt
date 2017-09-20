package at.rgstoettner.alexahome.executor.connection

import at.rgstoettner.alexahome.executor.println
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class Endpoint(url: String, timeout: Int) : WebSocketClient(URI(url), Draft_6455(), null, timeout) {

    var onOpenHandler: () -> Unit = {}
    var onClosedHandler: () -> Unit = {}
    var onExceptionHandler: () -> Unit = {}
    var onMessageHandler: (String) -> String = { "" }

    override fun onOpen(handshakedata: ServerHandshake) {
        "Socket opened".println()
        onOpenHandler.invoke()
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        "Socket closed (code $code ,reason: $reason)".println()
        onClosedHandler.invoke()
        destroy()
    }

    override fun onMessage(message: String) {
        "ExecutorMessage received: $message".println()
        val response = onMessageHandler.invoke(message)
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