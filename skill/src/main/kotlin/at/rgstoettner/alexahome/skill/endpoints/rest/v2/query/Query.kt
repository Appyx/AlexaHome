package at.rgstoettner.alexahome.skill.endpoints.rest.v2.query

import at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared.Header
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component

@Component
class Query {
    fun handleRequest(header: Header, payload: JsonNode): String {
        return ""
    }
}
