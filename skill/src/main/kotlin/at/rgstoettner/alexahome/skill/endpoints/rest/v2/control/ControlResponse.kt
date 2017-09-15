package at.rgstoettner.alexahome.skill.endpoints.rest.v2.control

import at.rgstoettner.alexahome.skill.SkillApplication
import at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared.Header
import at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared.Response
import at.rgstoettner.alexahome.skill.endpoints.websocket.ExecutorController
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

open class ControlResponse(id: String) : Response() {
    protected val executor = SkillApplication.context.getBean(ExecutorController::class.java)

    override val header = Header("Alexa.ConnectedHome.Control", this::class.simpleName.toString() + "Confirmation")

    protected val command: String?

    init {
        val configFile = File("config.json")
        if (configFile.exists()) {
            val mapper = ObjectMapper()
            val config = mapper.readValue(configFile, JsonNode::class.java)
            command = config
                    ?.get("devices")
                    ?.get(id)
                    ?.get("commands")
                    ?.get(this::class.simpleName.toString())
                    ?.textValue()
        } else {
            command = null
        }

    }

}