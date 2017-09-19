package at.rgstoettner.alexahome.manager.data

import com.google.gson.annotations.Expose

class AlexaHomeDevice(friendlyName: String, type: String, commandMap: Map<String, String>, version: Int = 2) {
    @Expose
    var id = "null@${System.currentTimeMillis()}"
    private var commands: MutableMap<String, String>
    var v2: AmazonDeviceV2? = null

    init {

        when (version) {
            2 -> {
                val device = AmazonDeviceV2(friendlyName, arrayListOf(type), commandMap.keys.toList())
                id = device.applianceId
                v2 = device
            }
        }

        commands = mutableMapOf()
        commandMap.forEach { k, v ->
            val key = k.capitalize()
            commands.put(key, v)
        }
    }

    fun setDescription(text: String) {
        v2?.friendlyDescription = text
    }

    fun setManufacturer(name: String) {
        v2?.manufacturerName = name
    }

    fun setModel(name: String) {
        v2?.modelName = name
    }
}