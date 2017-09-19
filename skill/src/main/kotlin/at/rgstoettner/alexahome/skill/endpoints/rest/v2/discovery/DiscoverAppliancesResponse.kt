package at.rgstoettner.alexahome.skill.endpoints.rest.v2.discovery

import at.rgstoettner.alexahome.skill.SkillApplication
import at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared.Header
import at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared.Response
import at.rgstoettner.alexahome.skill.endpoints.websocket.ExecutorController

class DiscoverAppliancesResponse : Response() {
    override val header = Header("Alexa.ConnectedHome.Discovery", "DiscoverAppliancesResponse")
    private val executor = SkillApplication.context.getBean(ExecutorController::class.java)

    init {
        val discoveredAppliances = putArray("discoveredAppliances")
        discoveredAppliances.addAll(executor.getDevices())
    }
}