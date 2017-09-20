package at.rgstoettner.alexahome.skill.endpoints.rest.v2

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

open class AmazonMessage(
        @JsonProperty("header") val header: AmazonHeader,
        @JsonProperty("payload") val payload: JsonNode) {

    constructor(namespace: String, name: String, payload: JsonNode)
            : this(AmazonHeader(namespace, name), payload)
}