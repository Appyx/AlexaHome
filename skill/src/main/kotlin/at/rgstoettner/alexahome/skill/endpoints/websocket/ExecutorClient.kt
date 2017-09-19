package at.rgstoettner.alexahome.skill.endpoints.websocket

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.CountDownLatch

class ExecutorClient(val session: WebSocketSession) {

    private val latch = CountDownLatch(1);
    private var data: String? = null


    fun close() {

    }

    fun discover(): ArrayNode {
        val mapper = ObjectMapper()
        val response = request(mapper.writeValueAsString(ExecutorMessage("discover")))
        val node = mapper.readValue(response, JsonNode::class.java)
        return mapper.convertValue(node, ArrayNode::class.java)
    }


    fun request(message: String): String? {
        sendMessage(message)
        return data
    }


    fun sendMessage(message: String) {
        session.sendMessage(TextMessage(message))
        latch.await()
    }


    fun onMessage(message: String) {
        data = message
        latch.countDown()
    }


}