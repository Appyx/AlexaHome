package at.rgstoettner.alexahome.skill.endpoints.rest.v2

import at.rgstoettner.alexahome.skill.endpoints.websocket.ExecutorController
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class Discovery {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    @Autowired
    private lateinit var executor: ExecutorController
    private val mapper = ObjectMapper()

    fun handleRequest(header: Header, payload: JsonNode): String {
        val id = header.name
        val message = executor.discover(id)
        var devices = mapper.nodeFactory.arrayNode()
        if (message.payload != null) {
            devices = message.payload as ArrayNode
        }
        val appliances = mapper.nodeFactory.objectNode()
        val array = appliances.putArray("discoveredAppliances")
        array.addAll(devices)

        val response = LambdaMessage(header.namespace, message.name!!, appliances)
        return mapper.writeValueAsString(response)
    }
}