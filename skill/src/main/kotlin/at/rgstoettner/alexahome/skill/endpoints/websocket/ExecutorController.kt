package at.rgstoettner.alexahome.skill.endpoints.websocket

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Configuration
class ExecutorController : TextWebSocketHandler() {
    private val clients = mutableListOf<ExecutorClient>()
    private val mapper = ObjectMapper()


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

    fun discover(name: String): ExecutorMessage {
        val devices = mapper.nodeFactory.arrayNode()
        var newMessage = ExecutorMessage("DiscoverAppliancesResponse")
        clients.forEach {
            val message = it.discover(name)
            val arrayNode = mapper.convertValue(message.payload, ArrayNode::class.java)
            devices.addAll(arrayNode)
            newMessage = message
        }
        newMessage.payload = devices
        return newMessage
    }

    fun query(name: String, payload: JsonNode?): ExecutorMessage? {
        clients.forEach {
            val message = it.control(name, payload)
            if (message.deviceReached) {
                return message
            }
        }
        return null
    }

    fun controlDevice(name: String, payload: JsonNode?): ExecutorMessage? {
        clients.forEach {
            val message = it.control(name, payload)
            if (message.deviceReached) {
                return message
            }
        }
        return null
    }


}