package at.rgstoettner.alexahome.skill.endpoints.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Configuration
class ExecutorController : TextWebSocketHandler() {


    fun getValueForCommand(command: String): String? {
        println(command)
        return "2700"
    }

    fun setValue(): Boolean {
        return true
    }

    override fun afterConnectionClosed(session: WebSocketSession?, status: CloseStatus?) {
        // The WebSocket has been closed
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        session.sendMessage(TextMessage("hello client"))
    }

    override fun handleTextMessage(session: WebSocketSession, textMessage: TextMessage) {
        // A message has been received
        println("Message received: " + textMessage.payload)
    }
}