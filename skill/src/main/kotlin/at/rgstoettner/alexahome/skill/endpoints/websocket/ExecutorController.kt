package at.rgstoettner.alexahome.skill.endpoints.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Configuration
class ExecutorController : TextWebSocketHandler() {
    private val clients = mutableListOf<ExecutorClient>()


    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus?) {
        val client = clients.find { it.session == session }
        client?.close()
        clients.remove(client)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        clients.add(ExecutorClient(session))
    }

    override fun handleTextMessage(session: WebSocketSession, textMessage: TextMessage) {
        val client = clients.find { it.session == session }
        client?.onMessage(textMessage.payload)
    }


    fun getDevices(): ArrayNode {
        val devices = mutableListOf<ArrayNode>()
        clients.forEach {
            devices.add(it.discover())
        }
        val mapper = ObjectMapper()
        val mergedNode = mapper.getNodeFactory().arrayNode()
        devices.forEach {
            it.forEach {
                mergedNode.add(it)
            }
        }
        return mergedNode
    }


}