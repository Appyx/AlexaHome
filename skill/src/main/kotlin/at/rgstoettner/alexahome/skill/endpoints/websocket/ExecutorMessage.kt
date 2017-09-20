package at.rgstoettner.alexahome.skill.endpoints.websocket

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper


/**
 * This class defines the contract between executors and skill.
 * It is used in both directions as transport protocol.
 *
 * The name of the message must match the name of the header in the Amazon spec.
 */
class ExecutorMessage(@JsonProperty("name") var name: String?) {

    /**
     * Required for the request.
     */
    var type: String? = null
    /**
     * Optional in both directions.
     */
    var payload: JsonNode? = null
        get() {
            if (field == null) {
                return ObjectMapper().nodeFactory.objectNode()
            }
            return field
        }

    /**
     * Required in the response
     */
    var executed = false

    /**
     * Optional in the response. Can be:
     *
     * ValueOutOfRangeError - value out of range
     *
     * BridgeOfflineError  - Networking issues hub
     *
     * TargetOfflineError  - Networking issues device only
     *
     * NoSuchTargetError - the device was not found in the profile
     *
     * UnsupportedTargetError - not supported anymore
     *
     * UnsupportedOperationError - operation is unsupported
     *
     * UnsupportedTargetSettingError - hello not working
     *
     * DriverInternalError - device not responding
     */
    var error: String? = null


    /**
     * type
     */
    companion object {
        val CONTROL = "CONTROL"
        val QUERY = "QUERY"
        val DISCOVER = "DISCOVER"
    }
}