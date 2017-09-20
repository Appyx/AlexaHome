package at.rgstoettner.alexahome.skill.endpoints.rest.v2

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class Header constructor(@JsonProperty("namespace") var namespace: String,
                         @JsonProperty("name") var name: String,
                         @JsonProperty("payloadVersion") var payloadVersion: Int = 2,
                         @JsonProperty("messageId") var messageId: String = UUID.randomUUID().toString()) {
}