package at.rgstoettner.alexahome.executor.connection

import at.rgstoettner.alexahome.executor.println
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class Endpoint(url: String) : WebSocketClient(URI(url), Draft_6455(), null, 2000) {
    override fun onOpen(handshakedata: ServerHandshake?) {
        "onOpen".println()
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        "onClose".println()
    }

    override fun onMessage(message: String?) {
        "onMessage".println()
    }

    override fun onError(ex: Exception?) {
        "onError".println()
    }
}