package at.rgstoettner.alexahome.skill.endpoints.websocket

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.Semaphore

class ExecutorClient(val session: WebSocketSession) {

    private val semaphore = Semaphore(0)
    @Volatile private var data: String? = null
    private val mapper = ObjectMapper()
    private var logger = LoggerFactory.getLogger(this::class.java)
    val host = session.remoteAddress.hostName

    init {
        logger.info("Client connected: ${session.remoteAddress.hostName}")
    }

    fun close() {
        logger.info("Client disconnected: ${session.remoteAddress.hostName}")
    }

    /**
     * Discovers all devices from this executor
     * @return The received ExecutorMessage containing the payload (json-array of devices)
     */
    fun discover(name: String): ExecutorMessage {
        val message = ExecutorMessage(name)
        message.type = ExecutorMessage.DISCOVER
        val response = request(mapper.writeValueAsString(message))
        return mapper.readValue(response, ExecutorMessage::class.java)
    }

    /**
     * Asks the executor if it is able to handle the control-action
     * @return the received ExecutorMessage containing an optional payload and the "executed" flag or an error.
     */
    fun control(name: String, payload: JsonNode): ExecutorMessage {
        val message = ExecutorMessage(name)
        message.type = ExecutorMessage.CONTROL
        message.payload = payload
        val response = request(mapper.writeValueAsString(message))
        return mapper.readValue(response, ExecutorMessage::class.java)
    }

    /**
     * Asks the executor if it is able to handle the query-action
     * @return the received ExecutorMessage containing an optional payload and the "executed" flag or an error.
     */
    fun query(name: String, payload: JsonNode): ExecutorMessage {
        val message = ExecutorMessage(name)
        message.type = ExecutorMessage.QUERY
        message.payload = payload
        val response = request(mapper.writeValueAsString(message))
        return mapper.readValue(response, ExecutorMessage::class.java)
    }

    /**
     * Wrapper method for a blocking request.
     */
    private fun request(message: String): String? {
        session.sendMessage(TextMessage(message))
        semaphore.acquire()
        return data
    }

    fun onMessage(message: String) {
        data = message
        semaphore.release()
    }


}