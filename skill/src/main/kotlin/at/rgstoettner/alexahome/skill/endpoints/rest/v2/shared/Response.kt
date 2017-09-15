package at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.LoggerFactory

abstract class Response {
    abstract val header: Header

    private val mapper = ObjectMapper()
    val payload = mapper.createObjectNode()
    protected val logger = LoggerFactory.getLogger(this.javaClass)

    protected fun putObject(field: String): ObjectNode {
        return payload.putObject(field)
    }

    protected fun putArray(field: String): ArrayNode {
        return payload.putArray(field)
    }


    fun toJson(): String {
        return mapper.writeValueAsString(this)
    }

    fun toError(name: String): Response {
        logger.error(name)
        header.name = name
        return this
    }
}