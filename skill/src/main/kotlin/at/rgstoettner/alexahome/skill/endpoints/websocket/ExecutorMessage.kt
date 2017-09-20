package at.rgstoettner.alexahome.skill.endpoints.websocket

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

class ExecutorMessage(@JsonProperty("name") var name: String?) {
    var type: String? = null
    var payload: JsonNode? = null
    var deviceReached = false
    var error: String? = null

    companion object {
        val CONTROL = "CONTROL"
        val QUERY = "QUERY"
        val DISCOVER = "DISCOVER"
    }
}