package at.rgstoettner.alexahome.skill.endpoints.rest.v2

import at.rgstoettner.alexahome.skill.endpoints.websocket.ExecutorController
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class Discovery {
    @Autowired
    private lateinit var executor: ExecutorController
    private val mapper = ObjectMapper()

    fun handleRequest(header: AmazonHeader, payload: JsonNode): String {
        val id = header.name
        val message = executor.discoverDevices(id)
        val devices = message.payload as ArrayNode

        //cannot simply return the devices, they need to be wrapped inside a special tag
        val discoveredAppliances = mapper.nodeFactory.objectNode()
        val devicesNode = discoveredAppliances.putArray("discoveredAppliances")
        devicesNode.addAll(devices)

        val response = AmazonMessage(header.namespace, message.name!!, discoveredAppliances)
        return mapper.writeValueAsString(response)
    }
}