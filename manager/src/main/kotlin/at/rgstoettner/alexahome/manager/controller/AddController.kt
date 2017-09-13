package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.*
import at.rgstoettner.alexahome.manager.data.ExtendedDevice

class AddController {

    fun addDevice() {
        println("Enter a unique name for the device: (required)")
        val name = safeReadLine()
        println("Select a device type: (required)")
        val types = arrayListOf("CAMERA", "LIGHT", "SMARTLOCK", "SMARTPLUG", "SWITCH", "THERMOSTAT")
        var typeIndex = 0;
        types.forEach {
            println("$typeIndex - $it")
            typeIndex += 1
        }
        val type = types[safeReadLine().toInt()]
        println("Enter a description for the device: (optional)")
        val description = safeReadLine()
        println("Enter a manufacturer for the device: (optional)")
        val manufacturer = safeReadLine()
        println("Enter a model name for the device: (optional)")
        val model = safeReadLine()
        println("The following actions can be used:")
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
        println("Enter the actions your device should support: [number1 number2 numberX]")
        val actionIndices = safeReadLine().split(" ").map { it.toInt() }

        val commandMap = mutableMapOf<String, String>()
        actionIndices.forEach {
            println("Enter the command to execute for '${actions[it]}':")
            val command = safeReadLine()
            commandMap.put(actions[it], command)
        }

        if (name.isNotEmpty() && type.isNotEmpty() && commandMap.isNotEmpty()) {
            println("$name successfully added. You can now discover the new device with Alexa.")

            val config = loadConfiguration()
            val device = ExtendedDevice(name, type, commandMap.keys.toList())
            device.commands = commandMap

            if (description.isNotEmpty()) device.friendlyDescription = description
            if (manufacturer.isNotEmpty()) device.manufacturerName = manufacturer
            if (model.isNotEmpty()) device.modelName = model

            config.devices.add(device)
            saveConfiguration(config)

        } else {
            handleFatalError(CliError.CONFIGURATION_INCOMPLETE)
        }
    }


    fun addScene() {

    }
}