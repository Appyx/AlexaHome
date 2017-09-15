package at.rgstoettner.alexahome.skill.endpoints.rest.v2.control

import at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared.Header
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Control {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper()

    fun handleRequest(header: Header, payload: JsonNode): String {
        val id = payload.get("appliance")?.get("applianceId")?.textValue()
        if (id == null) return ControlResponse("null").toError("NoSuchTargetError").toJson()

        when (header.name) {
            "DecrementColorTemperatureRequest" -> return DecrementColorTemperature(id).toJson()


            else -> {
                logger.error("Category ${header.name} is not supported")
                return ControlResponse(id).toError("UnsupportedOperationError").toJson()
            }
        }
    }


}