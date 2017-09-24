package at.rgstoettner.alexahome.skill.endpoints.rest

import at.rgstoettner.alexahome.skill.endpoints.rest.v2.Control
import at.rgstoettner.alexahome.skill.endpoints.rest.v2.Discovery
import at.rgstoettner.alexahome.skill.endpoints.rest.v2.Query
import at.rgstoettner.alexahome.skill.endpoints.rest.v2.AmazonHeader
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@RestController
class LambdaController {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var discovery: Discovery
    @Autowired
    private lateinit var control: Control
    @Autowired
    private lateinit var query: Query


    @RequestMapping(value = "/", method = arrayOf(RequestMethod.POST))
    fun handleCloudFunction(@RequestBody json: JsonNode): String {
        //logger.info("REQUEST: $json")
        val mapper = ObjectMapper()

        var result = "{}"
        val headerNode = json.get("header")
        val payloadNode = json.get("payload")
        if (headerNode != null && payloadNode != null) {
            val header = mapper.convertValue(headerNode, AmazonHeader::class.java)
            when (header.payloadVersion) {
                2 -> when (header.namespace) {
                    "Alexa.ConnectedHome.Discovery" -> result = discovery.handleRequest(header, payloadNode)
                    "Alexa.ConnectedHome.Control" -> result = control.handleRequest(header, payloadNode)
                    "Alexa.ConnectedHome.Query" -> result = query.handleRequest(header, payloadNode)
                }
                else -> logger.warn("Payload version ${header.payloadVersion} is not supported")
            }
        }
        //logger.info("RESPONSE: $result")
        return result
    }
}