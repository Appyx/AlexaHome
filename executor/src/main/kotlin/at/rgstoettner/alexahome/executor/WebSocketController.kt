package at.rgstoettner.alexahome.executor

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler


@Component
class WebSocketController : TextWebSocketHandler() {

    @Value("\${alias}")
    var alias: String = "anonymous"
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("Subscribing to Skill with alias: $alias")
        session.sendMessage(TextMessage("hello server"))
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println(message.payload)
    }
}