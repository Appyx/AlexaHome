package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.*
import at.rgstoettner.alexahome.manager.data.AlexaHomeDevice

class DeviceController {
    fun add() {
        "Enter a unique name for the device: ".println()
        val name = requiredReadLine()

        "Select a device type: ".println()
        val types = arrayListOf("CAMERA", "LIGHT", "SMARTLOCK", "SMARTPLUG", "SWITCH", "THERMOSTAT")
        var typeIndex = 0;
        types.forEach {
            println("$typeIndex - $it")
            typeIndex += 1
        }
        val type = types[requiredReadLine().toInt()]

        "Enter a description for the device: (optional)".println()
        val description = safeReadLine()
        "Enter a manufacturer for the device: (optional)".println()
        val manufacturer = safeReadLine()
        "Enter a model name for the device: (optional)".println()
        val model = safeReadLine()

        "The following actions can be used:".println()
        val actions = arrayListOf(
                "decrementColorTemperature",
                "incrementColorTemperature",
                "decrementPercentage",
                "incrementPercentage",
                "incrementTargetTemperature",
                "decrementTargetTemperature",
                "setColor",
                "setColorTemperature",
                "setLockState",
                "setPercentage",
                "setTargetTemperature",
                "turnOn",
                "turnOff"
        )
        var actionIndex = 0;
        actions.forEach {
            println("$actionIndex - $it")
            actionIndex += 1
        }

        "Enter the actions your device should support: [number1 number2 numberX]".println()
        val actionIndices = requiredReadLine().split(" ").map { it.toInt() }

        val commandMap = mutableMapOf<String, String>()
        actionIndices.forEach {
            println("Enter the command to execute for '${actions[it]}':")
            val command = requiredReadLine()
            commandMap.put(actions[it], command)
        }

        "$name successfully added. You can now discover the device with Alexa.".println()

        val config = skill.readConfig()
        val device = AlexaHomeDevice(name, type, commandMap)

        if (description.isNotEmpty()) device.setDescription(description)
        if (manufacturer.isNotEmpty()) device.setManufacturer(manufacturer)
        if (model.isNotEmpty()) device.setModel(model)

        config.addDevice(device)
        skill.writeConfig(config)
    }

    fun list() {
        println(skill.readConfig())
    }

    fun edit() {
        handleFatalError(CliError.NOT_IMPLEMENTED)
    }

    fun remove() {
        handleFatalError(CliError.NOT_IMPLEMENTED)
    }

    fun wipe() {
        handleFatalError(CliError.NOT_IMPLEMENTED)
    }
}