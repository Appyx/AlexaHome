package at.rgstoettner.alexahome.skill.endpoints.rest.v2.discovery

import at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared.Header
import at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared.Response
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import java.io.File

class DiscoverAppliancesResponse : Response() {
    override val header = Header("Alexa.ConnectedHome.Discovery", "DiscoverAppliancesResponse")

    init {
        val discoveredAppliances = putArray("discoveredAppliances")

        val configFile = File("config.json")
        if (configFile.exists()) {
            val mapper=ObjectMapper()
            val config = mapper.readValue(configFile, JsonNode::class.java)
            val ids = config.get("ids")
            val devices = ids.map {
                val id = it.textValue()
                val device = config.get("devices").get(id)
                return@map device.get("v2")
            }
            discoveredAppliances.addAll(devices)

        } else {
            logger.error("No config.json found")
        }
    }
}