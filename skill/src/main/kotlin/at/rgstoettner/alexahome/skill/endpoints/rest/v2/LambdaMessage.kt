package at.rgstoettner.alexahome.skill.endpoints.rest.v2

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

open class LambdaMessage(@JsonProperty("header") val header: Header,
                         @JsonProperty("payload") val payload: JsonNode) {
    constructor(namespace: String, name: String, payload: JsonNode)
            : this(Header(namespace, name), payload)


    //ValueOutOfRangeError - value out of range
    //BridgeOfflineError  - Networking issues hub
    //TargetOfflineError  - Networking issues device only
    //NoSuchTargetError - the device was not found in the profile
    //UnsupportedTargetError - not supported anymore
    //UnsupportedOperationError - operation is unsupported
    //UnsupportedTargetSettingError - hello not working
    //DriverInternalError - device not responding
}