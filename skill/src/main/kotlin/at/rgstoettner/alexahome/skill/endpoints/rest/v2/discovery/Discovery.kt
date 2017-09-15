package at.rgstoettner.alexahome.skill.endpoints.rest.v2.discovery

import at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared.Header
import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class Discovery {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun handleRequest(header: Header,payload: JsonNode): String {
        when (header.name) {
            "DiscoverAppliancesRequest" -> return DiscoverAppliancesResponse().toJson()
            else -> {
                logger.error("Category ${header.name} is not supported")
                return DiscoverAppliancesResponse().toJson()
            }
        }
    }
}