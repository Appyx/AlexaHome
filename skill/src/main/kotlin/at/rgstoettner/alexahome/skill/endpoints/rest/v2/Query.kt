package at.rgstoettner.alexahome.skill.endpoints.rest.v2

import at.rgstoettner.alexahome.skill.endpoints.websocket.ExecutorController
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Query {
    @Autowired
    private lateinit var executor: ExecutorController
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper()

    fun handleRequest(header: Header, payload: JsonNode): String {
        val id = header.name
        val message = executor.controlDevice(id, payload)
        if (message != null) {
            message.error?.let {
                return getErrorResponse(header.namespace, it)
            }
            var pl = mapper.nodeFactory.objectNode()
            if (message.payload != null) {
                pl = message.payload as ObjectNode
            }
            val response = LambdaMessage(header.namespace, message.name!!, pl)
            return mapper.writeValueAsString(response)

        } else {
            return getErrorResponse(header.namespace, "BridgeOfflineError")
        }
    }

    fun getErrorResponse(namespace: String, error: String): String {
        val pl = mapper.nodeFactory.objectNode()
        val response = LambdaMessage(namespace, error, pl)
        return mapper.writeValueAsString(response)
    }
}
