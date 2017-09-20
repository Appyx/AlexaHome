package at.rgstoettner.alexahome.skill.endpoints.websocket

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.CountDownLatch

class ExecutorClient(val session: WebSocketSession) {

    private val latch = CountDownLatch(1);
    private var data: String? = null
    private val mapper = ObjectMapper()


    fun close() {

    }

    fun discover(name: String): ExecutorMessage {
        val message = ExecutorMessage(name)
        message.type = ExecutorMessage.DISCOVER
        val response = request(mapper.writeValueAsString(message))
        return mapper.readValue(response, ExecutorMessage::class.java)
    }

    fun control(name: String, payload: JsonNode?): ExecutorMessage {
        val message = ExecutorMessage(name)
        message.type = ExecutorMessage.CONTROL
        message.payload = payload
        val response = request(mapper.writeValueAsString(message))
        return mapper.readValue(response, ExecutorMessage::class.java)
    }

    fun query(name: String, payload: JsonNode?): ExecutorMessage {
        val message = ExecutorMessage(name)
        message.type = ExecutorMessage.QUERY
        message.payload = payload
        val response = request(mapper.writeValueAsString(message))
        return mapper.readValue(response, ExecutorMessage::class.java)
    }

    private fun request(message: String): String? {
        sendMessage(message)
        return data
    }


    private fun sendMessage(message: String) {
        session.sendMessage(TextMessage(message))
        latch.await()
    }


    fun onMessage(message: String) {
        data = message
        latch.countDown()
    }


}