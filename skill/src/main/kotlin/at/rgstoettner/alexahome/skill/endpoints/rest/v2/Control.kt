package at.rgstoettner.alexahome.skill.endpoints.rest.v2

import at.rgstoettner.alexahome.skill.endpoints.websocket.ExecutorController
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Control {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper()
    @Autowired
    private lateinit var executor: ExecutorController

    fun handleRequest(header: AmazonHeader, payload: JsonNode): String {
        val message = executor.controlDevices(header.name, payload)
        message.error?.let {
            return getErrorResponse(header.namespace, it)
        }
        //null pointer exception is wanted if the name is null
        val response = AmazonMessage(header.namespace, message.name!!, message.payload!!)
        return mapper.writeValueAsString(response)
    }

    fun getErrorResponse(namespace: String, error: String): String {
        val pl = mapper.nodeFactory.objectNode()
        val response = AmazonMessage(namespace, error, pl)
        return mapper.writeValueAsString(response)
    }


}