package at.rgstoettner.alexahome.skill.endpoints.websocket

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Configuration
class ExecutorController : TextWebSocketHandler() {
    private var logger = LoggerFactory.getLogger(this::class.java)
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

    /**
     * Sends the name to all devices.
     *
     * @return The message containing an array of discovered devices.
     * If none are found it simply returns a message with an empty array.
     */
    fun discoverDevices(name: String): ExecutorMessage {
        val devices = mapper.nodeFactory.arrayNode()
        var newMessage = ExecutorMessage("DiscoverAppliancesResponse")
        clients.forEach {
            val message = it.discover(name)
            val arrayNode = mapper.convertValue(message.payload, ArrayNode::class.java)
            logger.info("Discovered ${arrayNode.count()} devices at: ${it.host}")
            devices.addAll(arrayNode)
            newMessage = message
        }
        newMessage.payload = devices
        return newMessage
    }

    /**
     * Sends the name and payload to all devices.
     * If a device can handle it, it has to set the "executed" flag to true.
     * This method returns the message that has the first "executed" flag set to true.
     *
     * @return The Message which contains the response payload or an error if no device was executed.
     */
    fun queryDevices(name: String, payload: JsonNode): ExecutorMessage {
        clients.forEach {
            val message = it.query(name, payload)
            if (message.executed) {
                logger.info("Executed at: ${it.host}")
                return message
            }
        }
        if (clients.size == 0) {
            return ExecutorMessage("BridgeOfflineError")
        } else {
            return ExecutorMessage("NoSuchTargetError")
        }
    }

    /**
     * Sends the name and payload to all devices.
     * If a device can handle it, it has to set the "executed" flag to true.
     * This method returns the message that has the first "executed" flag set to true.
     *
     * The Message which contains the response payload or an error if no device was executed.
     */
    fun controlDevices(name: String, payload: JsonNode): ExecutorMessage {
        clients.forEach {
            val message = it.control(name, payload)
            if (message.executed) {
                logger.info("Executed at: ${it.host}")
                return message
            }
        }
        if (clients.size == 0) {
            return ExecutorMessage("BridgeOfflineError")
        } else {
            return ExecutorMessage("NoSuchTargetError")
        }
    }


}